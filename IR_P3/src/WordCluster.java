import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	public static HashMap<Tuple,Integer> allTheWords;
	public static HashMap<String,Integer> combinedWordCounts;
	public static int totalWordCount;
	public static double windows = 0;
	public static ArrayList<String> computerList;
	public static HashMap<String,Integer> coHash;

	public enum Similarity {
		MI(10.5), EMI(0.005), CHI(2500), DICE(0.05);

		double threshold;
		Similarity(double threshold) {
			this.threshold = threshold;
		}
	}
	public static Similarity sim;

	public static void main(String[] args) {
		System.out.println("Problem 1 Part 3");
		System.out.println("a)");
		setUpForSimilarity("computer");
		miIterator();
		
		System.out.println("\nb)");
		sim = Similarity.DICE;
		SortedMap<String, SortedSet<String>> key2wordsMap = getStem2WordsMap("data/txt/");
		subclusterStem2WordsMap(key2wordsMap, true);
	}

	public static void setUpForSimilarity(String mainWord) {
		totalWordCount = 0;

		if (allTheWords == null) {
			allTheWords = new HashMap<Tuple,Integer>();
			allTheWords = wordCounter();
		}

		//		System.out.println(windows);
		computerList = new ArrayList<String>();
		for (Tuple t : allTheWords.keySet()){
			if (t.word.equals(mainWord)){
				computerList.add(t.inFile);
			}
		}

		//		System.out.println(computerList);
		coHash = new HashMap<String,Integer>();
		for (Tuple t : allTheWords.keySet()){
			if (!(coHash.containsKey(t.word))){
				coHash.put(t.word, 0);
			}
			if (computerList.contains(t.inFile)){
				coHash.put(t.word, coHash.get(t.word)+1);
			}
		}

		if (combinedWordCounts == null) {
			combinedWordCounts = new HashMap<String,Integer>();
			combinedWordCounts = countOfWord(allTheWords);
		}
		//		System.out.println(combinedWordCounts.get("computer"));
		totalWordCount = totalWords(allTheWords);
		coHash.put(mainWord, 0);
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
				windows ++;
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
							Tuple newToken = new Tuple(token,child.toString());
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
		//		System.out.println("done with main word count");
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

	public static Integer getCoOccurrences(String w1, String w2, HashMap<Tuple,Integer> words){
		ArrayList<String> filesw1 = new ArrayList<String>();
		ArrayList<String> filesw2 = new ArrayList<String>();
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
		for (String t : combinedWordCounts.keySet()){
			if (s != t){
				wordAndMIVal.put(t, miScore(s,t));
				wordAndEMIVal.put(t, emiScore(s,t));
				wordAndChiVal.put(t,chiSquaredScore(s,t));
				wordAndDice.put(t, diceScore(s,t));}
		}
		//		System.out.println("done computing association values");
		//find 10 max values
		PriorityQueue<Tuple> highQmi = new PriorityQueue<Tuple>();
		PriorityQueue<Tuple> highQemi = new PriorityQueue<Tuple>();
		PriorityQueue<Tuple> highQchi = new PriorityQueue<Tuple>();
		PriorityQueue<Tuple> highQdice = new PriorityQueue<Tuple>();
		for (String x : wordAndMIVal.keySet()){
			double mivalue = wordAndMIVal.get(x);
			double emivalue = wordAndEMIVal.get(x);
			double chivalue = wordAndChiVal.get(x);
			double dicevalue = wordAndDice.get(x);
			if (highQmi.size() >= 10){
				if (highQmi.peek().value < mivalue){
					highQmi.poll();
					highQmi.add(new Tuple(x,mivalue));
				}
			}
			else { highQmi.add(new Tuple(x,mivalue)); }
			if (highQemi.size() >= 10){
				if (highQemi.peek().value < emivalue){
					highQemi.poll();
					highQemi.add(new Tuple(x,emivalue));
				}
			}
			else { highQemi.add(new Tuple(x,emivalue)); }
			if (highQchi.size() >= 10){
				if (highQchi.peek().value < chivalue){
					highQchi.poll();
					highQchi.add(new Tuple(x,chivalue));
				}
			}
			else { highQchi.add(new Tuple(x,chivalue)); }
			if (highQdice.size() >= 10){
				if (highQdice.peek().value < dicevalue){
					highQdice.poll();
					highQdice.add(new Tuple(x,dicevalue));
				}
			}
			else { highQdice.add(new Tuple(x,dicevalue)); }
		}
		//print out final values
		String outputmi = "MI VALUES";
		String outputemi = "EMI VALUES";
		String outputchi = "CHI VALUES";
		String outputdice = "DICE VALUES";
		for (int i = 0; i < 10; i++){
			outputmi += "\n" + highQmi.peek().word + ": " + highQmi.poll().value;
			outputemi += "\n" + highQemi.peek().word + ": " + highQemi.poll().value;
			outputchi += "\n" + highQchi.peek().word + ": " + highQchi.poll().value;
			outputdice += "\n" + highQdice.peek().word + ": " + highQdice.poll().value;
		}
		System.out.println(outputmi + "\n");
		System.out.println(outputemi + "\n");
		System.out.println(outputchi + "\n");
		System.out.println(outputdice);
	}

	private static double miScore(String w1, String w2) {
		double coOccurrences = coHash.get(w2);
		double wordCountw1 = combinedWordCounts.get(w1);
		double wordCountw2 = combinedWordCounts.get(w2);
		double probw1 = wordCountw1 / totalWordCount;
		double probw2 = wordCountw2 / totalWordCount;
		if (probw2 == 0) System.out.println(w2);
		double probw1w2 = coOccurrences / windows;
		return Math.log(probw1w2 / (probw1 * probw2));
	}

	private static double emiScore(String w1, String w2) {
		double coOccurrences = coHash.get(w2);
		if (coOccurrences == 0) return 0;
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
		double countw1 = combinedWordCounts.get(w1);
		double countw2 = combinedWordCounts.get(w2);
		return 2 * coOccurrences / (countw1 + countw2);
	}

	private static double computeSimilarity(String w1, String w2) {
		setUpForSimilarity(w1);

		switch (sim) {
		case MI:
			return miScore(w1, w2);
		case EMI:
			return emiScore(w1, w2);
		case CHI:
			return chiSquaredScore(w1, w2);
		default:
			return diceScore(w1, w2);
		}
	}

	/**
	 * Given a clustering of words with their stem as the key,
	 * return a new clustering of words, where each cluster is 
	 * a subcluster of a stem class, and the respective key
	 * can be something arbitrary (e.g. stem + number, such as "polic1", "polic2")
	 * 
	 */
	public static SortedMap<String, SortedSet<String>> subclusterStem2WordsMap(SortedMap<String, SortedSet<String>> stem2WordsMap, boolean calculateOnlyTop10){
		//cluster name -> subcluster set
		SortedMap<String, SortedSet<String>> subclusterMap = new TreeMap<String, SortedSet<String>>();

		for (String stemmed : stem2WordsMap.keySet()) {
			//a set of words with the same stem
			SortedSet<String> wordsSet = stem2WordsMap.get(stemmed);

			if (calculateOnlyTop10) {
				if (wordsSet.size() <= 10) continue;
			}
			HashMap<String, HashSet<String>> wordToOutNodes = new HashMap<String, HashSet<String>>();
			Object[] wordsArr = wordsSet.toArray();
			//note- does not process words with a wordsSet of size 0
			for (int i = 0; i < wordsArr.length; i++) {
				for (int j = i + 1; j < wordsArr.length; j++) {
					String word1 = (String) wordsArr[i];
					String word2 = (String) wordsArr[j];

					if (!wordToOutNodes.containsKey(word1)) {
						wordToOutNodes.put(word1, new HashSet<String>());
					}
					if (!wordToOutNodes.containsKey(word2)) {
						wordToOutNodes.put(word2, new HashSet<String>());
					}

					if (!word1.equals(word2)) {
						double similarity = computeSimilarity(word1, word2);
						//						System.out.println(similarity);
						if (similarity >= sim.threshold) {
							HashSet<String> s1 = wordToOutNodes.get(word1);
							HashSet<String> s2 = wordToOutNodes.get(word2);
							s1.add(word2);
							s2.add(word1);
						}
					}
				}
			}
			LinkedList<HashSet<String>> subclusterList = new LinkedList<HashSet<String>>();
			for (String word : wordToOutNodes.keySet()) {
				boolean wordInACluster = false;
				//checks if word is already in a cluster- continue if not
				for (HashSet<String> subcluster : subclusterList) {
					if (subcluster.contains(word)) {
						wordInACluster = true;
						break;
					}
				}
				if (!wordInACluster) {
					HashSet<String> newCluster = new HashSet<String>();
					fillSubcluster(word, newCluster, wordToOutNodes);
					subclusterList.add(newCluster);
				}
			}
			//Turn subclusters into SortedSets
			for (int i = 0; i < subclusterList.size(); i++) {
				HashSet<String> subcluster = subclusterList.get(i);
				TreeSet<String> sub = new TreeSet<String>();//(TreeSet<String>) subcluster;
				for (String str : subcluster) {
					sub.add(str);
				}
				subclusterMap.put(stemmed + i, sub);
			}

			//prints to compare to problem 1 part 2
			if (stem2WordsMap.get(stemmed).size() > 10) {
				System.out.println(stemmed + ":");
				for (HashSet<String> subcluster : subclusterList) {
					System.out.println("     " + subcluster);
				}
			}
		}

		return subclusterMap;
	}
	
	public static SortedMap<String, SortedSet<String>> MIsubclusterStem2WordsMap;
	public static SortedMap<String, SortedSet<String>> EMIsubclusterStem2WordsMap;
	public static SortedMap<String, SortedSet<String>> CHIsubclusterStem2WordsMap;
	public static SortedMap<String, SortedSet<String>> DICEsubclusterStem2WordsMap;
	
	public static void serializeAndLoadMaps(SortedMap<String, SortedSet<String>> key2wordsMap) {
		String[] filenames = new String[] {"miSubclusters.ser", "emiSubclusters.ser", "chiSubclusters.ser", "diceSubclusters.ser"};
		
		//Serialize maps
		for (int i = 0; i < filenames.length; i++) {
			String filename = filenames[i];
			File file = new File(filename);
			SortedMap<String, SortedSet<String>> subclusterStem2WordsMap = null;
			
			if (!file.exists()) {
				switch (i) {
				case 0:
					WordCluster.sim = WordCluster.Similarity.MI;
					break;
				case 1:
					WordCluster.sim = WordCluster.Similarity.EMI;
					break;
				case 2:
					WordCluster.sim = WordCluster.Similarity.CHI;
					break;
				default:
					WordCluster.sim = WordCluster.Similarity.DICE;
					break;
				}
				subclusterStem2WordsMap = WordCluster.subclusterStem2WordsMap(key2wordsMap, false);
				
				try {
					FileOutputStream fileOut = new FileOutputStream(filename);
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(subclusterStem2WordsMap);
					out.close();
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					FileInputStream fileIn = new FileInputStream(filename);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					subclusterStem2WordsMap = (SortedMap<String, SortedSet<String>>) in.readObject();
					in.close();
					fileIn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			switch (i) {
			case 0:
				MIsubclusterStem2WordsMap = subclusterStem2WordsMap;
				break;
			case 1:
				EMIsubclusterStem2WordsMap = subclusterStem2WordsMap;
				break;
			case 2:
				CHIsubclusterStem2WordsMap = subclusterStem2WordsMap;
				break;
			default:
				DICEsubclusterStem2WordsMap = subclusterStem2WordsMap;
				break;
			}
		}
	}

	private static void fillSubcluster(String word, final HashSet<String> subcluster, final HashMap<String, HashSet<String>> wordToOutNodes) {
		if (!subcluster.contains(word)) {
			subcluster.add(word);
			for (String child : wordToOutNodes.get(word)) {
				fillSubcluster(child, subcluster, wordToOutNodes);
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
