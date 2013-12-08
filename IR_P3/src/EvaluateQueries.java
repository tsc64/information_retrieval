import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;

/**
 * To return all the results as described, uncomment
 * the various lines in preprocessQuery
 *
 */
public class EvaluateQueries {
	private static SortedMap<String, String> word2keyMap = null;
	private static SortedMap<String, SortedSet<String>> key2wordsMap = null;
	private static final CharArraySet STOPWORDS = StandardAnalyzer.STOP_WORDS_SET;
	private static final Version VERSION = Version.LUCENE_44;
	private static final String DATA_DIR = "data/";
	
	public static void main(String[] args) {
		String docsDir = DATA_DIR + "txt"; // directory containing documents
		String indexDir = DATA_DIR + "index"; // the directory where index is written into
		String queryFile = DATA_DIR + "cacm_processed.query";    // query file
		String answerFile = DATA_DIR + "cacm_processed.rel";   // relevance judgements file

		int numResults = 5;

		key2wordsMap = WordCluster.getStem2WordsMap(docsDir);
		System.out.println("Problem 1 Part 1");
		WordCluster.problem1part1(key2wordsMap);
		
		System.out.println("\nProblem 1 Part 2");
		WordCluster.problem1part2(key2wordsMap);
		
		System.out.println("\nProblem 1 Part 3b");
		
//		key2wordsMap = WordCluster.subclusterStem2WordsMap(key2wordsMap);
//		word2keyMap = WordCluster.getWord2KeyMap(key2wordsMap);
		
		System.out.println();
		System.out.println("Average P@5: " + evaluate(indexDir, docsDir, queryFile,
				answerFile, numResults));
		
	}
	
	/**
	 * Once you finish implementing WordCluster class, you can use 
	 * "key2wordsMap" and "word2keyMap" to transform the query.
	 * (You may also structure the code differently, 
	 * where you may/may not make use of these maps.)
	 * 
	 * @param query: original query extracted from cacm_processed.query file
	 * @return a modified query
	 * @throws IOException 
	 */
	private static String preprocessQuery(String query) throws IOException{
		//uncomment the code on the line below for the results in 4a)
		//return query;
		//uncomment the code on the line below for the results in 4b)
		//return stemQuery(query);
		//uncomment the code on the line below for the results in 4c)
		return stemClassQuery(query);
		//uncomment the code on the line below for the results in 4d)
	}

	private static String stemQuery (String query) throws IOException{
		String newQuery = "";
		PorterStemmer stemmer = new PorterStemmer();
		StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_44);
		TokenStream stream = sa.tokenStream(null, new StringReader(query));
		CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		while (stream.incrementToken()){
			String token = cattr.toString();
			stemmer.setCurrent(token);
			stemmer.stem();
			String stemmed = stemmer.getCurrent() + " ";
			newQuery += stemmed;
		}
		return newQuery;
	}
	
	private static String stemClassQuery(String query) throws IOException{
		String newQuery = "";
		PorterStemmer stemmer = new PorterStemmer();
		StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_44);
		TokenStream stream = sa.tokenStream(null, new StringReader(query));
		CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		while (stream.incrementToken()){
			String token = cattr.toString();
			stemmer.setCurrent(token);
			stemmer.stem();
			String stemmed = stemmer.getCurrent();
			SortedSet<String> wordSet = key2wordsMap.get(stemmed);
			String newWords = "";
			if (wordSet != null){
			for (String x : wordSet){
				newWords = newWords + x + " ";
			}} else newWords = stemmed;
			newQuery += newWords;
		}
		return newQuery;
	}
	
	private static Map<Integer, String> loadQueries(String filename) {
		Map<Integer, String> queryIdMap = new HashMap<Integer, String>();
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
				queryIdMap.put(Integer.parseInt(line.substring(0, pos)), preprocessQuery(line.substring(pos + 1)));
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
			in = new BufferedReader(new FileReader(new File(filename)));

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

	private static double precision(HashSet<String> answers,List<String> results) {
		double matches = 0;
		for (String result : results) {
			if (answers.contains(result))
				matches++;
		}

		return matches / results.size();
	}

	private static double evaluate(String indexDir, String docsDir,
			String queryFile, String answerFile, int numResults) {

		// Build Index
		IndexFiles.buildIndex(indexDir, docsDir, STOPWORDS);

		// load queries and answer
		Map<Integer, String> queries = loadQueries(queryFile);
		Map<Integer, HashSet<String>> queryAnswers = loadAnswers(answerFile);

		// Search and evaluate
		double sum = 0;
		for (Integer i : queries.keySet()) {
			List<String> results = SearchFiles.searchQuery(indexDir, queries
					.get(i), numResults, STOPWORDS);
			sum += precision(queryAnswers.get(i), results);
		}

		return sum / queries.size();
	}
}
