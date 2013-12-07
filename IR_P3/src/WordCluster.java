import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
	/**
	 * Given a directory containing a set of text files (CACM), return a mapping from stem to words with the given stem.
	 */
	public static SortedMap<String, SortedSet<String>> getStem2WordsMap(String text_dir) {
		TreeMap<String, TreeSet<String>> map = new TreeMap<String, TreeSet<String>>(); 
		PorterStemmer stemmer = new PorterStemmer();
		File dir = new File(text_dir);

		try {
			File[] children = dir.listFiles();
			Scanner scanner;
			for (File child : children) {
				scanner = new Scanner(new FileReader(child));
				while (scanner.hasNext()) {
					String token = scanner.next();
					stemmer.setCurrent(token);
					stemmer.stem();
					String stemmed = stemmer.getCurrent();
					if (map.containsKey(stemmed)) {
						SortedSet<String> set = map.get(stemmed);
						set.add(token);
					} else {
						TreeSet<String> set = new TreeSet<String>();

						set.add(token);
						map.put(stemmed, set);
					}
				}
			}
		} catch (IOException e) {

		}
		return null;
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

		for (int key : setSizeToNumClasses.keySet()) {
			int value = setSizeToNumClasses.get(key);
			System.out.println(key + " : " + value);
		}

		System.out.println(stemmedClasses);
	}

	private static double miScore(String w1, String w2) {

		return 0;
	}

	private static double emiScore(String w1, String w2) {
		return 0;
	}

	private static double chiSquaredScore(String w1, String w2) {

		return 0;
	}

	private static double diceScore(String w1, String w2) {

		return 0;
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
			for (String child : wordToOutNodes.get(wordToOutNodes)) {
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

	public static void main(String[] args) {

	}
}
