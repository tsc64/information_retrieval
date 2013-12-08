import java.io.File;


public class Tuple implements Comparable<Tuple> {
	public String word;
	public String inFile;
	public Double value;
	
	public Tuple(String keyword, String filename) {
		word = keyword;
		inFile = filename;
	}
	
	public Tuple(String keyword, Double assValue){
		word = keyword;
		value = assValue;
	}
	
	public boolean equals (Tuple other) {
		if ((word == other.word) && (inFile == other.inFile)) return true; else return false;
	}

	
	public int compareTo(Tuple other) {
		// TODO Auto-generated method stub
		if (other.value < value){
			return 1;
		}
		if (other.value > value){
			return -1;
		}
		return 0;
	}

	
}
