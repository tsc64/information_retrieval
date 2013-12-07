import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

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
		SortedMap<String, SortedSet<String>> map = new SortedMap<String, SortedSet<String>>() {
			
			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public SortedSet<String> remove(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void putAll(Map<? extends String, ? extends SortedSet<String>> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public SortedSet<String> put(String arg0, SortedSet<String> arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public SortedSet<String> get(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean containsValue(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean containsKey(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Collection<SortedSet<String>> values() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, SortedSet<String>> tailMap(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, SortedSet<String>> subMap(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String lastKey() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<String> keySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, SortedSet<String>> headMap(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String firstKey() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<Entry<String, SortedSet<String>>> entrySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Comparator<? super String> comparator() {
				// TODO Auto-generated method stub
				return null;
			}
		}; 
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
						SortedSet<String> set = new SortedSet<String>() {
							
							@Override
							public <T> T[] toArray(T[] arg0) {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public Object[] toArray() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public int size() {
								// TODO Auto-generated method stub
								return 0;
							}
							
							@Override
							public boolean retainAll(Collection<?> arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public boolean removeAll(Collection<?> arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public boolean remove(Object arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public Iterator<String> iterator() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public boolean isEmpty() {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public boolean containsAll(Collection<?> arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public boolean contains(Object arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public void clear() {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public boolean addAll(Collection<? extends String> arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public boolean add(String arg0) {
								// TODO Auto-generated method stub
								return false;
							}
							
							@Override
							public SortedSet<String> tailSet(String arg0) {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public SortedSet<String> subSet(String arg0, String arg1) {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public String last() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public SortedSet<String> headSet(String arg0) {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public String first() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public Comparator<? super String> comparator() {
								// TODO Auto-generated method stub
								return null;
							}
						};
						
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
		SortedMap<String, SortedSet<String>> map = new SortedMap<String, SortedSet<String>>() {
			
			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public SortedSet<String> remove(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void putAll(Map<? extends String, ? extends SortedSet<String>> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public SortedSet<String> put(String arg0, SortedSet<String> arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public SortedSet<String> get(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean containsValue(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean containsKey(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Collection<SortedSet<String>> values() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, SortedSet<String>> tailMap(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, SortedSet<String>> subMap(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String lastKey() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<String> keySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, SortedSet<String>> headMap(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String firstKey() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<Entry<String, SortedSet<String>>> entrySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Comparator<? super String> comparator() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
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
				SortedSet sub = (SortedSet) subcluster;
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
		SortedMap<String, String> map = new SortedMap<String, String>() {
			
			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String remove(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void putAll(Map<? extends String, ? extends String> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String put(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String get(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean containsValue(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean containsKey(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Collection<String> values() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, String> tailMap(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, String> subMap(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String lastKey() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<String> keySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SortedMap<String, String> headMap(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String firstKey() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<Entry<String, String>> entrySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Comparator<? super String> comparator() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
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
