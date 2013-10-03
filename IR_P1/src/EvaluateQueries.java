import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.hunspell.HunspellStemmer.Stem;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;

public class EvaluateQueries {
	public final static boolean USE_STEMMING = true;
	public final static boolean USE_STOP_WORDS = true;
	
	public static void main(String[] args) {
		String docsDir = "data/txt/"; // directory containing documents
		String indexDir = "data/index/"; // the directory where index is written into

		String queryFile = "data/cacm_processed.query";    // query file
		String answerFile = "data/cacm_processed.rel";   // relevance judgements file

		int numResults = 5;
		CharArraySet stopwords;
		if (USE_STOP_WORDS) {
			ArrayList<String> stoplist = new ArrayList<String>();
			try{
				BufferedReader br = new BufferedReader(new FileReader("data/stopwords_union.txt"));

				String newline;
				while ((newline = br.readLine()) != null){
					stoplist.add(newline.trim());
//					System.out.println(newline.trim());

				}
				br.close();
			}catch(Exception e){
				System.out.println("Check your try block");
			}

			stopwords = new CharArraySet(Version.LUCENE_44, stoplist, false); 
		} else {
			stopwords = new CharArraySet(Version.LUCENE_44, 0, false);
		}
		System.out.println(evaluate(indexDir, docsDir, queryFile,
				answerFile, numResults, stopwords));
		
		System.out.println("done.");
		
	}

	private static Map<Integer, String> loadQueries(String filename) {
		HashMap<Integer, String> queryIdMap = new HashMap<Integer, String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(
					new File(filename)));
		} catch (FileNotFoundException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}

		String line;
		try {
			while ((line = in.readLine()) != null) {
				int pos = line.indexOf(',');
				//Stemming
//				if (USE_STEMMING) {
//					queryIdMap.put(Integer.parseInt(line.substring(0, pos)), 
//						createStems(line.substring(pos + 1)));
//				} else {
					queryIdMap.put(Integer.parseInt(line.substring(0, pos)), 
						line.substring(pos + 1));
//				}
			}
		} catch(IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		} finally {
			try {
				in.close();
			} catch(IOException e) {
				System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
			}
		}
		return queryIdMap;
	}

	private static Map<Integer, HashSet<String>> loadAnswers(String filename) {
		HashMap<Integer, HashSet<String>> queryAnswerMap = new HashMap<Integer, HashSet<String>>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(
					new File(filename)));

			String line;
			while ((line = in.readLine()) != null) {
				String[] parts = line.split(" ");
				HashSet<String> answers = new HashSet<String>();
				for (int i = 1; i < parts.length; i++) {
					answers.add(parts[i]);
				}
				queryAnswerMap.put(Integer.parseInt(parts[0]), answers);
			}
		} catch(IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		} finally {
			try {
				in.close();
			} catch(IOException e) {
				System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
			}
		}
		return queryAnswerMap;
	}

	private static double precision(HashSet<String> answers,
			List<String> results) {
		double matches = 0;
		for (String result : results) {
			if (answers.contains(result))
				matches++;
		}

		return matches / results.size();
	}

	private static double evaluate(String indexDir, String docsDir,
			String queryFile, String answerFile, int numResults,
			CharArraySet stopwords) {

		// Build Index
		IndexFiles.buildIndex(indexDir, docsDir, stopwords);

		// load queries and answer
		Map<Integer, String> queries = loadQueries(queryFile);
		Map<Integer, HashSet<String>> queryAnswers = loadAnswers(answerFile);

		// Search and evaluate
		double sum = 0;
		for (Integer i : queries.keySet()) {
			String query = queries.get(i);
//			System.out.println(query);
			List<String> results = SearchFiles.searchQuery(indexDir, 
					query, numResults, stopwords);
			sum += precision(queryAnswers.get(i), results);
		}

		return sum / queries.size();
	}
	
	/*
	public static String createStems(String str) {
		String[] words = str.split(" ");
		PorterStemmer ps = new PorterStemmer();
		String output = "";
		for (String word : words) {
			ps.setCurrent(word);
			ps.stem();
			output += ps.getCurrent() + " ";
		}
		return output.trim();
	}
	*/
}
