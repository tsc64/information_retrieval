
public class Tuple implements Comparable<Tuple> {
	public Object object;
	public double value;
	
	public Tuple(Object o, double i) {
		object = o;
		value = i;
	}
	
	public int compareTo(Tuple other) {
		if (value < other.value) return -1;
		else if (value == other.value) return 0;
		else return 1;
	}
	
	//doesn't care if they are equal
	public Tuple min (Tuple other) {
		if ((compareTo(other)) < 0) return this;
		else return other;
	}
	
	//doesn't care if they are equal
	public Tuple max (Tuple other) {
		if ((compareTo(other)) > 0) return this;
		else return other;
	}
	
	public boolean equals (Tuple other) {
		if (value == other.value) return true; else return false;
	}
}
