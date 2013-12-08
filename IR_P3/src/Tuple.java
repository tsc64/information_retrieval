import java.io.File;


public class Tuple {
	public String word;
	public File inFile;
	public Double value;
	
	public Tuple(String keyword, File file) {
		word = keyword;
		inFile = file;
	}
	
	public Tuple(String keyword, Double assValue){
		word = keyword;
		value = assValue;
	}
	
	public boolean equals (Tuple other) {
		if ((word == other.word) && (inFile == other.inFile)) return true; else return false;
	}

	
}
