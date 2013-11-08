
public class Tuple implements Comparable<Tuple> {
	public Object object;
	public int value;
	
	public Tuple(Object o, int i) {
		object = o;
		value = i;
	}
	
	public int compareTo(Tuple other) {
		if (value < other.value) return -1;
		else if (value == other.value) return 0;
		else return 1;
	}
}
