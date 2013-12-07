import java.util.SortedMap;
import java.util.SortedSet;

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
		return null;
	}
	
	/**
	 * Given a clustering of words with their stem as the key,
	 * return a new clustering of words, where each cluster is 
	 * a subcluster of a stem class, and the respective key
	 * can be something arbitrary (e.g. stem + number, such as "polic1", "polic2")
	 * 
	 */
	public static SortedMap<String, SortedSet<String>> subclusterStem2WordsMap(SortedMap<String, SortedSet<String>> stem2WordsMap){
		return stem2WordsMap; //after you implement this method, you don't have to keep this line. You can build a new map and return it.
	}
	
	/**
	 * Given a map that maps a key to a set of words,
	 * return a map that maps each word in the set to the key.
	 * e.g. {"polic":{"police","policy"}} --> {"policy":"polic", "police":"polic"}
	 */
	public static SortedMap<String, String> getWord2KeyMap(SortedMap<String, SortedSet<String>> key2wordsMap){
		return null;
	}
}
