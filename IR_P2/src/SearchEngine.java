import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;


public class SearchEngine {
	File docsDir;
	File indexDir;
	File queryFile;
	File answerFile;
	
	public SearchEngine(String docsDir, String indexDir, String queryFile, String answerFile) {
		this.docsDir = new File(docsDir);
		this.indexDir = new File(indexDir);
		this.queryFile = new File(queryFile);
		this.answerFile = new File(answerFile);
	}
	
	/**
	 * a)
	 * Creates a list of (number of words, frequency) pairs
	 * Prints the 5 most frequent and the 5 least frequent words
	 */
	private void verifyZipf() {
		StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_44);
		TokenStream stream;
		
		try {
			stream = sa.tokenStream(null, new StringReader("how are you today")); //content
			CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
	        while (stream.incrementToken())
	        {
	            System.out.println(cattr.toString());
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}
	
	/**
	 * b)
	 * Creates an inverted index
	 * Prints the 5 most frequent and the 5 least frequent words
	 * Returns the inverted index
	 */
	private Object invertedIndexFrequencies() {

		return null;
	}

	/**
	 * c)
	 * tf.idf(i) = termFrequency(i) * log(total#documents / documentFrequency(i))
	 * Prints Precision@5 on the queries
	 */
	private void tf_idf(Object invertedIndex) {
		
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
		// TODO Auto-generated method stub
		SearchEngine engine = new SearchEngine("data/txt/", "data/index/", "data/cacm_processed.query", "data/cacm_processed.rel");
		
		System.out.println("Part A:");
		engine.verifyZipf();
		
		System.out.println();
		System.out.println("Part B:");
		Object invertedIndex = engine.invertedIndexFrequencies();
		
		System.out.println();
		System.out.println("Part C:");
		engine.tf_idf(invertedIndex);

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
