import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;


public class Analytical {

	//Word -> tfidf value
	HashMap<String, Double> d1 = new HashMap<String, Double>();
	HashMap<String, Double> d2 = new HashMap<String, Double>();
	HashMap<String, Double> d3 = new HashMap<String, Double>();
	HashMap<String, Double> d4 = new HashMap<String, Double>();
	HashMap<String, Double> d5 = new HashMap<String, Double>();
	HashMap<String, Double> d6 = new HashMap<String, Double>();


	public double dist(HashMap<String, Double> u, HashMap<String, Double> v) {
		double dist = 0;
		for (String key : u.keySet()) {
			double vValue = (v.containsKey(key)) ? v.get(key) : 0;
			dist += Math.pow(u.get(key) - vValue, 2);
		}
		for (String key : v.keySet()) {
			if (!u.containsKey(key)) {
				dist += Math.pow(v.get(key), 2);
			}
		}
		return Math.sqrt(dist);
	}

	public void computeDistances() {
		LinkedList<HashMap<String, Double>> lst = new LinkedList<HashMap<String, Double>>();
		lst.add(d1);
		lst.add(d2);
		lst.add(d3);
		lst.add(d4);
		lst.add(d5);

		lst.add(d6);

		for (int i = 0; i < lst.size(); i++) {
			for (int j = 0; j < lst.size(); j++) {
				if (i != j) {
					HashMap<String, Double> map1 = lst.get(i);
					HashMap<String, Double> map2 = lst.get(j);
					double dist = dist(map1, map2);
					System.out.println("D" + (i+1) + ", D" + (j+1) + ": " + roundThreeDecimals(dist));
				}
			}
		}
	}

	double roundThreeDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.###");
		return Double.valueOf(twoDForm.format(d));
	}

	private void processSentences() {
		LinkedList<String[]> sentences = new LinkedList<String[]>();
		sentences.add("jaguar honda mercedes vehicle bentley car toyota acura".split(" "));
		sentences.add("jaguar jaguar car car vehicle black white silver blue".split(" "));
		sentences.add("jaguar cat panther animal orange lion cat cat black orange".split(" "));
		sentences.add("amazon jaguar rainforest jungle amazon".split(" "));
		sentences.add("amazon books store products music shipping products amazon".split(" "));

		sentences.add("jaguar panther jaguar black jungle panther".split(" "));

		for (int idx = 0; idx < sentences.size(); idx++) {
			String[] sentence = sentences.get(idx);
			for (String word : sentence) {
				HashMap<String, Double> map;
				switch (idx) {
				case 0:
					map = d1;
					break;
				case 1:
					map = d2;
					break;
				case 2:
					map = d3;
					break;
				case 3:
					map = d4;
					break;
				case 4:
					map = d5;
					break;
				default:
					map = d6;
					break;
				}
				processTfidf(word, sentences, idx, map);
			}
		}
	}

	private void processTfidf(String word, LinkedList<String[]> sentences, int idx, HashMap<String, Double> map) {
		int freq = 0;
		for (String w : sentences.get(idx)) {
			if (w.equals(word)) freq++; 
		}
		int docsWithWord = 0;
		for (String[] sentence : sentences) {
			for (String w : sentence) {
				if (w.equals(word)) {
					docsWithWord++;
					break;
				}
			}
		}
		double tfidf = freq * Math.log10((double) sentences.size() / (double) docsWithWord);
		map.put(word, tfidf);
	}

	public void kMeans(int k) {
		LinkedList<double[]> vectors = toArray();
		Object[] clusters = new Object[k];
		//centroid initialization
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = vectors.get((i+2) % vectors.size()).clone(); 
		}
		int[] clusterAssignment = new int[vectors.size()];

		boolean change;
		do {
			change = false;
			//assigns each ex to closest cluster
			for (int i = 0; i < vectors.size(); i++) {
				int bestClusterAssignment = argMinCluster(clusters, vectors.get(i));
				if (clusterAssignment[i] != bestClusterAssignment) {
					clusterAssignment[i] = bestClusterAssignment;
					change = true;
				}
			}
			//update cluster centroids
			if (change) {
				for (int clusterNum = 0; clusterNum < clusters.length; clusterNum++) {
					double[] centroid = new double[vectors.get(0).length];
					for (int exIdx = 0; exIdx < clusterAssignment.length; exIdx++) {
						if (clusterAssignment[exIdx] == clusterNum) {
							double[] ex = vectors.get(exIdx);
							for (int j = 0; j < ex.length; j++) {
								centroid[j] += ex[j];
							}
						}
					}
					for (int j = 0; j < centroid.length; j++) {
						centroid[j] /= vectors.size();
					}
					clusters[clusterNum] = centroid;
				}
			}
		} while (!change);
		
		for (int i = 0; i < clusterAssignment.length; i++) {
			System.out.println("D" + (i+1) + ": cluster " + clusterAssignment[i]);
		}
	}

	private int argMinCluster(Object[] clusters, double[] example) {
		int bestClusterAssignment = -1;
		double bestDist = Double.MAX_VALUE;
		for (int i = 0; i < clusters.length; i++) {
			double dist = 0;
			double[] cluster = (double[]) clusters[i];
			for (int j = 0; j < example.length; j++) {
				dist += Math.pow(cluster[j] - example[j], 2);
			}
			dist = Math.sqrt(dist);
			if (dist < bestDist) {
				bestClusterAssignment = i;
				bestDist = dist;
			}
		}
		return bestClusterAssignment;
	}

	private LinkedList<double[]> toArray() {
		HashMap<String, Integer> uniqueKeys = stringToUniqueNum();
		LinkedList<double[]> xVectors = new LinkedList<double[]>();

		LinkedList<HashMap<String, Double>> lst = new LinkedList<HashMap<String, Double>>();
		lst.add(d1);
		lst.add(d2);
		lst.add(d3);
		lst.add(d4);
		lst.add(d5);

		for (HashMap<String, Double> ex : lst) {
			double[] arr = new double[uniqueKeys.size()];
			for (String key : ex.keySet()) {
				arr[uniqueKeys.get(key)] = ex.get(key);
			}
			xVectors.add(arr);
		}
		return xVectors;
	}

	private HashMap<String, Integer> stringToUniqueNum() {
		LinkedList<HashMap<String, Double>> lst = new LinkedList<HashMap<String, Double>>();
		lst.add(d1);
		lst.add(d2);
		lst.add(d3);
		lst.add(d4);
		lst.add(d5);

		HashMap<String, Integer> uniqueKeys = new HashMap<String, Integer>();
		for (HashMap<String, Double> ex : lst) {
			for (String key : ex.keySet()) {
				if (!uniqueKeys.containsKey(key)) {
					uniqueKeys.put(key, uniqueKeys.size());
				}
			}
		}
		return uniqueKeys;
	}

	public static void main(String[] args) {
		Analytical a = new Analytical();
		a.processSentences();
		a.computeDistances();
		System.out.println();
		a.kMeans(3);
	}
}
