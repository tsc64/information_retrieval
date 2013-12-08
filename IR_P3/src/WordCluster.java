import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;

/**
 * You may, and should, implement any additional methods
 * including a main() method for outputing results, but
 * you should still implement and make use of the following methods:
 * getStem2WordsMap()
 * subclusterStem2WordsMap()
 * getWord2StemMap()
 */
public class WordCluster {
	public static HashMap<Tuple,Integer> allTheWords = new HashMap<Tuple,Integer>();
	public static HashMap<String,Integer> combinedWordCounts = new HashMap<String,Integer>();
	public static int totalWordCount = 0;
	public static double windows = numDocs();
	public static ArrayList<File> computerList = new ArrayList<File>();
	public static HashMap<String,Integer> coHash = new HashMap<String,Integer> ();
	
	public static void main(String[] args) {
		allTheWords = wordCounter();
		for (Tuple t : allTheWords.keySet()){
			if (t.word == "computer"){
				computerList.add(t.inFile);
			}
		}
		for (Tuple t : allTheWords.keySet()){
			if (!(coHash.containsKey(t.word))){
				coHash.put(t.word, 0);
			}
			if (computerList.contains(t.inFile)){
				coHash.put(t.word, coHash.get(t.word)+1);
			}
		}
		combinedWordCounts = countOfWord(allTheWords);
		totalWordCount = totalWords(allTheWords);
		int i = 0;
/**		for (String s : combinedWordCounts.keySet()){
			coHash.put(s,getCoOccurrences(s,"computers",allTheWords));
			System.out.println("coHash #: " + i++);
		}  */
		miIterator();
	}
	
	
	/**
	 * Given a directory containing a set of text files (CACM), return a mapping from stem to words with the given stem.
	 */
	public static SortedMap<String, SortedSet<String>> getStem2WordsMap(String text_dir) {
		SortedMap<String, SortedSet<String>> map = new TreeMap<String, SortedSet<String>>(); 
		PorterStemmer stemmer = new PorterStemmer();
		File dir = new File(text_dir);
		StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_44);
		TokenStream stream;

		try {
			File[] children = dir.listFiles();
			Scanner scanner;
			for (File child : children) {
				scanner = new Scanner(new FileReader(child));
				while ( scanner.hasNextLine() ){
					String line = scanner.nextLine();

					stream = sa.tokenStream(null, new StringReader(line));
					CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
					stream.reset();
					while (stream.incrementToken())
					{
						String token = cattr.toString();
						stemmer.setCurrent(token);
						stemmer.stem();
						String stemmed = stemmer.getCurrent();
						if (map.containsKey(stemmed)) {
							TreeSet<String> set = (TreeSet<String>) map.get(stemmed);
							set.add(token);

						} else {
							TreeSet<String> set = new TreeSet<String>();
							set.add(token);
							map.put(stemmed, set);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sa.close();
		return map;
	}

	public static void problem1part1(SortedMap<String, SortedSet<String>> map) {
		StringBuffer stemmedClasses = new StringBuffer();
		HashMap<Integer, Integer> setSizeToNumClasses = new HashMap<Integer, Integer>(); 
		for (String stemmed : map.keySet()) {
			stemmedClasses.append(stemmed + ", ");
			int setSize = map.get(stemmed).size();
			int value = (setSizeToNumClasses.containsKey(setSize)) ? setSizeToNumClasses.get(setSize) : 0;
			setSizeToNumClasses.put(setSize, value + 1);	
		}
		System.out.println(setSizeToNumClasses);
//		System.out.println(stemmedClasses);
	}
	
	public static void problem1part2(SortedMap<String, SortedSet<String>> map) {
		for (String stemmed : map.keySet()) {
			SortedSet<String> set = map.get(stemmed);
			int setSize = set.size();
			if (setSize > 10) {
				System.out.println(stemmed + ": " + set);
			}
		}
	}

	public static Integer numDocs() {
		File docsDir = new File("data/txt/");
		int numberOfDocs = 0;
		File[] children = docsDir.listFiles();
		for (File child : children) {
			numberOfDocs += 1;
		}
		return numberOfDocs;
	}
	
	public static HashMap<Tuple, Integer> wordCounter() {
		HashMap<Tuple, Integer> occurences = new HashMap<Tuple, Integer>();
		StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_44);
		CharArraySet stopWords = sa.STOP_WORDS_SET;
		TokenStream stream;
		File docsDir = new File("data/txt/");
		
		try {
			File[] children = docsDir.listFiles();
			Scanner scanner;
			for (File child : children) {
				scanner = new Scanner(new FileReader(child));
				while ( scanner.hasNextLine() ){
					String line = scanner.nextLine();

					stream = sa.tokenStream(null, new StringReader(line));
					CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
					stream.reset();
			        while (stream.incrementToken())
			        {
			        	String token = cattr.toString();
			        	if (!(stopWords.contains(token))){
			        	Tuple newToken = new Tuple(token,child);
			            if (occurences.containsKey(newToken)) {
			            	occurences.put(newToken, occurences.get(token) + 1);
			            } else {
			            	occurences.put(newToken, 1);
			            }}
			        	else {System.out.println("Lies Tre! There are stopwords!");}
			        }
				}
				scanner.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("done with main word count");
		return occurences;
	}
	
	public static HashMap<String,Integer> countOfWord(HashMap<Tuple,Integer> wordCount){
		HashMap<String, Integer> combinedDocs = new HashMap<String, Integer>();
		for (Tuple t : wordCount.keySet()){
			if (combinedDocs.containsKey(t.word)){
				combinedDocs.put(t.word, (combinedDocs.get(t.word) + wordCount.get(t)));
			} else {
				combinedDocs.put(t.word, wordCount.get(t));
			}
		}
		return combinedDocs;
	}
	
	public static Integer totalWords(HashMap<Tuple, Integer> wordCount){
		int total = 0;
		for (Tuple s : wordCount.keySet()){
			total += wordCount.get(s);
		}
		return total;
	}
	//make a list of all the files that computer is in
	//make a hash of (word,count)
	//for Tuple t : allWords
	//if compList.contains(t.inFile) then Hash.put(word,count+1)
	public static Integer getCoOccurrences(String w1, String w2, HashMap<Tuple,Integer> words){
		ArrayList<File> filesw1 = new ArrayList<File>();
		ArrayList<File> filesw2 = new ArrayList<File>();
		for (Tuple t : words.keySet()){
			if (t.word == w1){
				filesw1.add(t.inFile);
			}
			if (t.word == w2){
				filesw2.add(t.inFile);
			}
		}
		int countInCommon = 0;
		for (int i = 0; i < filesw1.size(); i++){
			if (filesw2.contains(filesw1.get(i))){
				countInCommon ++;
			}
		}
		return countInCommon;
	}
	
	private static void miIterator(){
		String s = "computer";
		HashMap<String,Double> wordAndMIVal = new HashMap<String,Double>();
		HashMap<String,Double> wordAndEMIVal = new HashMap<String,Double>();
		HashMap<String,Double> wordAndChiVal = new HashMap<String,Double>();
		HashMap<String,Double> wordAndDice = new HashMap<String,Double>();
		int i = 0;
		for (String t : combinedWordCounts.keySet()){
			wordAndMIVal.put(t, miScore(s,t));
			wordAndEMIVal.put(t, emiScore(s,t));
			wordAndChiVal.put(t,chiSquaredScore(s,t));
			wordAndDice.put(t, diceScore(s,t));
		}
		System.out.println("done computing association values");
		//find 10 max values
		PriorityQueue<Tuple> highQ = new PriorityQueue<Tuple>();
		
	}
	
	private static double miScore(String w1, String w2) {
        int coOccurrences = coHash.get(w2);
        int wordCountw1 = combinedWordCounts.get(w1);
        int wordCountw2 = combinedWordCounts.get(w2);
        double probw1 = wordCountw1 / totalWordCount;
        double probw2 = wordCountw2 / totalWordCount;
        double probw1w2 = coOccurrences / windows;
		return Math.log(probw1w2 / (probw1 * probw2));
	}

	private static double emiScore(String w1, String w2) {
		int coOccurrences = coHash.get(w2);
		return miScore(w1,w2) * coOccurrences / windows;
	}

	private static double chiSquaredScore(String w1, String w2) {
		double coOccurrences = coHash.get(w2);
		double countw1 = combinedWordCounts.get(w1);
		double countw2 = combinedWordCounts.get(w2);
		double numerator = (coOccurrences - (countw1 * countw2) / totalWordCount);
		double denom = countw1 * countw2 / totalWordCount;
		return numerator * numerator / denom;
	}

	private static double diceScore(String w1, String w2) {
		double coOccurrences = coHash.get(w2);
		int countw1 = combinedWordCounts.get(w1);
		double countw2 = combinedWordCounts.get(w2);
		return 2 * coOccurrences / (countw1 + countw2);
	}

	/**
	 * Given a clustering of words with their stem as the key,
	 * return a new clustering of words, where each cluster is 
	 * a subcluster of a stem class, and the respective key
	 * can be something arbitrary (e.g. stem + number, such as "polic1", "polic2")
	 * 
	 */
	public static SortedMap<String, SortedSet<String>> subclusterStem2WordsMap(SortedMap<String, SortedSet<String>> stem2WordsMap){
		SortedMap<String, SortedSet<String>> map = new TreeMap<String, SortedSet<String>>();

		int threshold = 0;

		for (String stemmed : stem2WordsMap.keySet()) {
			SortedSet<String> set = stem2WordsMap.get(stemmed);
			HashMap<String, HashSet<String>> wordToOutNodes = new HashMap<String, HashSet<String>>();
			for (String word1 : set) {
				for (String word2 : set) {
					if (word1 != word2 && diceScore(word1, word2) >= threshold) {
						HashSet<String> s1 = (wordToOutNodes.containsKey(word1)) ? wordToOutNodes.get(word1) : new HashSet<String>();
						HashSet<String> s2 = (wordToOutNodes.containsKey(word2)) ? wordToOutNodes.get(word2) : new HashSet<String>();
						s1.add(word2);
						s2.add(word1);
						wordToOutNodes.put(word1, s1);
						wordToOutNodes.put(word2, s2);
					}
				}
			}
			LinkedList<HashSet<String>> subclusterList = new LinkedList<HashSet<String>>();
			for (String word : wordToOutNodes.keySet()) {
				boolean toContinue = true;
				//checks if word is already in a cluster- continue if not
				for (HashSet<String> subcluster : subclusterList) {
					if (subcluster.contains(word)) {
						toContinue = false;
						break;
					}
				}
				if (toContinue) {
					HashSet<String> subcluster = new HashSet<String>();
					fillSubcluster(word, subcluster, wordToOutNodes);
					subclusterList.add(subcluster);
				}
			}
			for (int i = 0; i < subclusterList.size(); i++) {
				HashSet<String> subcluster = subclusterList.get(i);
				//TODO turn subcluster into SortedSet
				TreeSet<String> sub = null;//(TreeSet<String>) subcluster;
				map.put(stemmed + i, sub);
			}
		}

		return map; //after you implement this method, you don't have to keep this line. You can build a new map and return it.
	}

	private static void fillSubcluster(String word, final HashSet<String> subcluster, final HashMap<String, HashSet<String>> wordToOutNodes) {
		if (!subcluster.contains(word)) {
			subcluster.add(word);
			if (wordToOutNodes != null) {
				for (String child : wordToOutNodes.keySet()) { //TODO check over
					fillSubcluster(child, subcluster, wordToOutNodes);
				}
			}
		}
	}

	/**
	 * Given a map that maps a key to a set of words,
	 * return a map that maps each word in the set to the key.
	 * e.g. {"polic":{"police","policy"}} --> {"policy":"polic", "police":"polic"}
	 */
	public static SortedMap<String, String> getWord2KeyMap(SortedMap<String, SortedSet<String>> key2wordsMap){
		TreeMap<String, String> map = new TreeMap<String, String>();

		for (String stemmed : key2wordsMap.keySet()) {
			for (String word : key2wordsMap.get(stemmed)) {
				map.put(word, stemmed);
			}
		}
		return map;
	}

}
