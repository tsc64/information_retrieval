import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;


public class SearchEngine {
	File docsDir;
	File indexDir;
	File queryFile;
	File answerFile;
	int numDocuments = 0;
	
	public SearchEngine(String docsDir, String indexDir, String queryFile, String answerFile) {
		this.docsDir = new File(docsDir);
		this.indexDir = new File(indexDir);
		this.queryFile = new File(queryFile);
		this.answerFile = new File(answerFile);
		numDocuments = this.docsDir.listFiles().length;
	}
	
	/**
	 * a)
	 * Creates a list of (number of words, frequency) pairs
	 * Prints the numResults (5) most frequent and the numResults (5) least frequent words
	 */
	private void verifyZipf(int numResults) {
		HashMap<String, Integer> occurences = new HashMap<String, Integer>();
		StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_44);
		TokenStream stream;
		
		try {
			File[] children = docsDir.listFiles();
			Scanner scanner;
			for (File child : children) {
				scanner = new Scanner(new FileReader(child));
				while ( scanner.hasNextLine() ){
					String line = scanner.nextLine();

					stream = sa.tokenStream(null, new StringReader(line)); //content
					CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
					stream.reset();
			        while (stream.incrementToken())
			        {
			        	String token = cattr.toString();
			            if (occurences.containsKey(token)) {
			            	occurences.put(token, occurences.get(token) + 1);
			            } else {
			            	occurences.put(token, 1);
			            }
			        }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PriorityQueue<Tuple> highQ = new PriorityQueue<Tuple>();
		PriorityQueue<Tuple> lowQ = new PriorityQueue<Tuple>();
		
		for (String token : occurences.keySet()) {
			int value = occurences.get(token);
			//maintains a a queue with the numResults greatest values
			if (highQ.size() >= numResults) {
				if (highQ.peek().value < value) {
					highQ.poll();
					highQ.add(new Tuple(token, value));
				}
			} else {
				highQ.add(new Tuple(token, value));
			}
			
			//maintains a a queue with the numResults lowest values
			if (lowQ.size() >= numResults) {
				if (lowQ.peek().value < -value) {
					lowQ.poll();
					lowQ.add(new Tuple(token, -value));
				}
			} else {
				lowQ.add(new Tuple(token, -value));
			}
		}
		
		int numWords = 0;
		for (String word : occurences.keySet()) {
			numWords += occurences.get(word);
		}
		Tuple[] maxArr = new Tuple[highQ.size()];
		for (int i = 0; i < numResults; i++) {
			maxArr[i] = highQ.poll();
		}
		Arrays.sort(maxArr);
		String max = "";
		for (int i = 0; i < maxArr.length; i++) {
			Tuple t = maxArr[i];
			int r = (maxArr.length - i);
			double prob = (t.value / (double) numWords);
			max = "Word: " + t.object 
					+ ", Freq: " + t.value 
					+ ", r: " + r 
					+ ", prob: " + (Math.round(prob * 10000.0) / 10000.0)
					+ ", c: " + (Math.round(r * prob * 10000.0) / 10000.0)
					+ "\n" + max;
		}
		System.out.println(max);
		
		Tuple[] minArr = new Tuple[lowQ.size()];
		for (int i = 0; i < numResults; i++) {
			minArr[i] = lowQ.poll();
		}
		Arrays.sort(minArr);
		String min = "";
		for (int i = 0; i < minArr.length; i++) {
			Tuple t = minArr[i];
			int r = (occurences.size() - i);
			double prob = (-t.value / (double) numWords);
			min = "Word: " + t.object 
					+ ", Freq: " + (int)-t.value 
					+ ", r: " + r 
					+ ", prob: " + (Math.round(prob * 1000000000.0) / 1000000000.0)
					+ ", c: " + (Math.round(r * prob * 1000000000.0) / 1000000000.0)
					+ "\n" + min;
		}
		System.out.println(min);
		System.out.println("Total number of words: " + numWords);

		//Prints <#of words, freq> for top 5 and lowest 5 frequencies
		//Highest
		int numA = 0; int numB = 0; int numC = 0; int numD = 0; int numE = 0;
		for (String word : occurences.keySet()) {
			int freq = occurences.get(word); 
			if (freq == 3204) numA++;
			else if (freq == 3001) numB++;
			else if (freq == 2996) numC++;
			else if (freq == 2220) numD++;
			else if (freq == 1831) numE++;
		}
		System.out.println();
		System.out.println("Frequency:Number of Words");
		System.out.println("3204: " + numA);
		System.out.println("3001: " + numB);
		System.out.println("2996: " + numC);
		System.out.println("2220: " + numD);
		System.out.println("1831: " + numE);

		//Lowest
		int num1 = 0; int num2 = 0; int num3 = 0; int num4 = 0; int num5 = 0;
		for (String word : occurences.keySet()) {
			int freq = occurences.get(word); 
			if (freq == 1) num1++;
			else if (freq == 2) num2++;
			else if (freq == 3) num3++;
			else if (freq == 4) num4++;
			else if (freq == 5) num5++;
		}
		System.out.println();
		System.out.println("Frequency:Number of Words");
		System.out.println("1: " + num1);
		System.out.println("2: " + num2);
		System.out.println("3: " + num3);
		System.out.println("4: " + num4);
		System.out.println("5: " + num5);
	}
	
	
	private ArrayList<Tuple> min_five (ArrayList<Tuple> current, Tuple next){
		//newMin holds the largest Tuple of the 6 values
		Tuple newMin = new Tuple(null,Integer.MIN_VALUE);
		for (Tuple t : current){
			newMin = newMin.max(t);
		}
		newMin = newMin.max(next);
		ArrayList<Tuple> newAL = new ArrayList<Tuple>();
		for (Tuple t : current){
			if (!(newMin.equals(t))) {
				newAL.add(t);
			}
		}
		if (!(newMin.equals(next))) newAL.add(next);
		return newAL;
	}
	
	private ArrayList<Tuple> max_five (ArrayList<Tuple> current, Tuple next){
		//newMax holds the smallest Tuple of the 6 values
		Tuple newMax = new Tuple(null,Integer.MAX_VALUE);
		for (Tuple t : current){
			newMax = newMax.min(t);
		}
		newMax = newMax.min(next);
		ArrayList<Tuple> newAL = new ArrayList<Tuple>();
		for (Tuple t : current){
			if (!(newMax.equals(t))) {
				newAL.add(t);
			}
		}
		if (!(newMax.equals(next))) newAL.add(next);
		return newAL;
	}
	
	/**
	 * b)
	 * Creates an inverted index
	 * Prints the 5 most frequent and the 5 least frequent words
	 * Returns the inverted index
	 */
	private Object invertedIndexFrequencies() {
		HashMap<String, HashMap<String, Integer>> invIndex = new HashMap<String, HashMap<String, Integer>>();
		
		try {
			File[] children = docsDir.listFiles();
			Scanner scanner;
			for (File child : children) {
				scanner = new Scanner(new FileReader(child));
				while ( scanner.hasNextLine() ){
					String line = scanner.nextLine();
					StandardTokenizer src = new StandardTokenizer(Version.LUCENE_44, new StringReader(line));
					src.setMaxTokenLength(Integer.MAX_VALUE);
					TokenStream tokenStream = new StandardFilter(Version.LUCENE_44, src);
					CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

					tokenStream.reset();
					while (tokenStream.incrementToken()) {
					   String term = charTermAttribute.toString();
					   String fileName = child.getName();
					   if (invIndex.containsKey(term)) {
						   HashMap<String, Integer> oldMap = invIndex.get(term);
						   if (oldMap.containsKey(fileName)){
							   oldMap.put(fileName, (1 + oldMap.get(fileName)));
						   } else {
							   oldMap.put(fileName, 1);
						   }
						   invIndex.put(term, oldMap);
					   } else {
						   HashMap<String, Integer> newEntry = new HashMap<String,Integer>();
						   newEntry.put(fileName, 1);
						   invIndex.put(term, newEntry);
					   }
					}

			        
				}
			}
		}
			catch (IOException e) {
				e.printStackTrace();
	}
		//here is where you determine the big/small 5
		ArrayList<Tuple> minBase = new ArrayList<Tuple>();
		ArrayList<Tuple> maxBase = new ArrayList<Tuple>();
		//set up base values to compare to
		for (int i = 1; i < 6; i ++){
			minBase.add(new Tuple(null,Integer.MAX_VALUE - i));
			maxBase.add(new Tuple(null,Integer.MIN_VALUE + i));
		}
		//System.out.println(invIndex.size());
		//find the five min and max values in the index
		for (String x : invIndex.keySet()){
			minBase = min_five(minBase, new Tuple(x,invIndex.get(x).size()));
			maxBase = max_five(maxBase, new Tuple(x,invIndex.get(x).size()));
		}
		//printing the 10 values
		for (Tuple x : minBase){
			System.out.println(x.object + ": " + x.value);
		}
		for (Tuple x : maxBase){
			System.out.println(x.object + ": " + x.value);
		}
		//here is where you change counts to frequencies
		
		
		return invIndex;
		

	}

	

	/**
	 * c)
	 * tf.idf(i) = termFrequency(i) * log(total#documents / documentFrequency(i))
	 * Prints Precision@5 on the queries
	 */
	private void tf_idf(HashMap<String, HashMap<String, Double>> invertedIndex) {
		try {
			double averagePrecision = 0;
			int numQueries = 0;
			Scanner queryScanner = new Scanner(new FileReader(queryFile));
			Scanner answersScanner = new Scanner(new FileReader(answerFile));
			while (queryScanner.hasNextLine()) {
				String line = queryScanner.nextLine();
				String query = line.substring(line.indexOf(",") + 1);
				double precision = processQuery(query, answersScanner.nextLine(), invertedIndex);
				numQueries++;
				averagePrecision += precision;
			}
			queryScanner.close();
			answersScanner.close();
			
			averagePrecision /= numQueries;
			System.out.println("Precision@5: " + averagePrecision);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private double processQuery(String query, String answers, HashMap<String, HashMap<String, Double>> invertedIndex) {
		HashMap<String, Double> totalScoreMap = new HashMap<String, Double>();
		//For each word in tokenized query, process word and multiply? together
		//TODO Tokenize each word
		for (String word : query.split(" ")) {
			HashMap<String, Double> docToTfidfMap = processWord(word, invertedIndex);
			for (String doc : docToTfidfMap.keySet()) {
				if (totalScoreMap.containsKey(doc)) {
					totalScoreMap.put(doc, totalScoreMap.get(doc) * docToTfidfMap.get(doc));
				} else {
					totalScoreMap.put(doc, docToTfidfMap.get(doc));
				}
			}
		}
		
		int pAt = 5;
		PriorityQueue<Tuple> topResults = new PriorityQueue<Tuple>();
		for (String doc : totalScoreMap.keySet()) {
			double score = totalScoreMap.get(doc);
			if (topResults.size() >= pAt) {
				if (topResults.peek().value < score) {
					topResults.poll();
					topResults.add(new Tuple(doc, score));
				}
			} else {
				topResults.add(new Tuple(doc, score));
			}
		}
		
		HashSet<String> answersSet = parseAnswers(answers, pAt);
		int numPresent = 0;
		for (Tuple t : topResults) {
			if (answersSet.contains(t.object)) numPresent++;
		}
		
		return (double) numPresent / (double) answersSet.size();
	}
	
	/**
	 * 
	 * @param word
	 * @param invertedIndex
	 * @return returns mapping of document->tf.idf for given word
	 */
	private HashMap<String, Double> processWord(String word, HashMap<String, HashMap<String, Double>> invertedIndex) {
		HashMap<String, Double> documentScores = new HashMap<String, Double>();
		HashMap<String, Double> docToFreqMap = invertedIndex.get(word);
		
		for (String doc : docToFreqMap.keySet()) {
			double freq = docToFreqMap.get(doc);
			double tf_idf = freq * Math.log((double) numDocuments / (double) docToFreqMap.size());
			documentScores.put(doc, tf_idf);
		}
		
		return documentScores;
	}
	
	private HashSet<String> parseAnswers(String answers, int pAt) {
		answers = answers.substring(answers.indexOf(" ") + 1);
		String[] answersArr = answers.split(" ");
		HashSet<String> answersSet = new HashSet<String>();
		for (int i = 0; i < pAt && i < answersArr.length; i++) {
			answersSet.add(answersArr[i]);
		}
		return answersSet;
	}
	
	/**
	 * d)
	 * Figure out what formula is being used by IndexSearcher's DefaultSimilarity.
	 */
	private void examineDefaultSimilarity() {
		
	}
	
	/**
	 * e)
	 * Implement two versions of your own tf.idf similarity measures
	 * Compare their performance
	 */
	private void compareTfidfs() {
		
	}
	
	/**
	 * f)
	 * Extend your program with a BM25 similarity measure. Experiment with varying the levels of b and k1
	 */
	private void bm25() {
		
	}
	
	/**
	 * g)
	 * Print the 3 most relevant documents in the CACM collection based on term frequency 
	 * and the 3 most relevant documents using tf-idf weighting
	 */
	private void getMostRevelantDocuments() {
		String query = "proposal or survey , binary variable , Fibonaccian";
		
	}

	public static void main(String[] args) {
		SearchEngine engine = new SearchEngine("data/txt/", "data/index/", "data/cacm_processed.query", "data/cacm_processed.rel");
		
		System.out.println("Part A:");
		//engine.verifyZipf(5);
		
		System.out.println();
		System.out.println("Part B:");
		HashMap<String, HashMap<String, Double>> invertedIndex = (HashMap<String, HashMap<String, Double>>) engine.invertedIndexFrequencies();
		
		System.out.println();
		System.out.println("Part C:");
//		engine.tf_idf((HashMap<String, HashMap<String, Double>>) invertedIndex);

		System.out.println();
		System.out.println("Part D:");
		engine.examineDefaultSimilarity();

		System.out.println();
		System.out.println("Part E:");
		engine.compareTfidfs();

		System.out.println();
		System.out.println("Part F:");
		engine.bm25();
	}

}
