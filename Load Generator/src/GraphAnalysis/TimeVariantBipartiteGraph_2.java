package GraphAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

//import GraphAnalysis.TimeVarientBipartiteGraph_RealData.PatternStat;

public class TimeVariantBipartiteGraph_2 {


	
	public final int MIN_PATTERN_LENGTH = 3; //This is the minimum length of a pattern.
	public final int PATTERN_SUPPORT = 4; //This is the minimum number of patterns in a bucket. If a bucket has less then these many patterns, that bucket is discarded. 
	public final int EPOCH_LENGTH = 8000;
	public int[] ColorThresholds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};//new int [NoOfCOLORS];
	public String[] Colors = {"RED", "YELLOW", "BLUE", "GREEN", "PURPULE", "BROWN", "BLACK", "WHITE", "ORANGE", "GRAY"};
	public final int NoOfCOLORS = Colors.length; //How many different colors the edges can have
	public int Buckets = NoOfCOLORS;
	public Hashtable<Integer, ArrayList<Bucket>> graphBuckets = null; //This is the list of buckets. The buckets are in phases.
	//The phase that starts first has an id = 1 contains all the patterns starting from first instance of Bipartite Graph (BG)
	// Phase that starts next, i.e. from 2nd instance of BG, will have an id = 2 and so on and so forth.

	BufferedReader br = null;
	PrintStream pr = null;
	FileOutputStream fos = null;
	public int InstanceCounter = 0; //This counter counts how many BG instances we have read so far.
	
	int quantumCounter = 0;

	public ArrayList<Hashtable<String, ArrayList<Edge>>> BipartiteGraph = null; //We have a sequence of Bipartite graphs. Hence, this is an arrayList.

	public ArrayList<Hashtable<String, HashSet<String>>> Patterns = null; //In this arraylist of hashtables we store the patterns. Key of the hashtable is the bucket id, and value is the list of edges belonging to that bucket.


	public HashSet<String> LHSNodes = null; //This set contains the LHS nodes in the bipartite graph.
	public HashSet<String> RHSNodes = null; //This set contains the LHS nodes in the bipartite graph.

	int MAX_NODES_RHS = -1; //This parameter records the maximum number of nodes seen so far on the right hand side of BG
	int MAX_NODES_LHS = -1; //This parameter records the maximum number of nodes seen so far on the left hand side of BG
	int MAX_EDGES = -1; //Maximum number of edges seen so far in any instance of BG. This will determine the number of buckets to be created.

	//No LHS node should appear on RHS and vice versa. If a line contains a node that is in the wrong set, we ignore it. Since we read sequentially, we don't need any second pass.
	//By following this simple rule (ignoring), we will have no node appearing on the wrong side.


	public TimeVariantBipartiteGraph_2(String path, String fileName) {
		// TODO Auto-generated constructor stub
		String[] fileTokens;
		Hashtable<String, ArrayList<Edge>> graphInstance = null;
		Hashtable<String, HashSet<String>> currentPatterns = null;

		String filePath = path + fileName;
		fileTokens = fileName.split("\\.");
		String outputPath = path + "output_LongPatterns_" + fileTokens[0] + "_" +  PATTERN_SUPPORT + "_" + MIN_PATTERN_LENGTH + ".txt";
		String line;
		String[] tokens;
		try {
			br = new BufferedReader (new FileReader (new File (filePath)));
			fos = new FileOutputStream (new File (outputPath));
			pr = new PrintStream (fos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BipartiteGraph = new ArrayList<Hashtable<String, ArrayList<Edge>>> ();
		graphInstance = new Hashtable<String, ArrayList<Edge>> ();
		BipartiteGraph.add(graphInstance);

		Patterns = new ArrayList<Hashtable<String, HashSet<String>>> ();
		currentPatterns = new Hashtable<String, HashSet<String>> ();
		Patterns.add(currentPatterns);

	}

	//Read a file here to construct the graph. In the first line, we specify the number of instances of bipartiteGraph

	public void ConstructGraph () {

		String line, lhsNode, rhsNode, edge, key="", oldKey;
		ArrayList<Edge> edgeList;
		int color = NoOfCOLORS -1; //We initialize with highest color number
		String[] tokens;
		String epochDelimiter = "ED";
		Edge e;
		int lineCounter = 0;
		int maxEdgeCounter = Integer.MIN_VALUE, edgeCounter=0, totalEdges=0;

		Hashtable<String, ArrayList<Edge>> graphInstance = BipartiteGraph.get(InstanceCounter);

		Hashtable<String, HashSet<String>> currentPatterns = Patterns.get(InstanceCounter);

		Set keySet = currentPatterns.keySet();
		HashSet<String> edgesSet;

		int GraphCounter = 0; //This counter counts how many timeEpochs (or bipartite graphs) he has read so far from the file.

		try {
			while ( (line = br.readLine()) != null) {

				lineCounter++;
			
				//if (lineCounter % EPOCH_LENGTH != 0) {
				if (line.indexOf("----------") < 0) {
					edgeCounter++;
					tokens = line.split(" ");
					if (tokens.length == 3) {
						lhsNode = tokens[0];
						rhsNode = tokens[2];
						edge = lhsNode + "_" + rhsNode;
						oldKey = findKey (edge);
						for (int i=0;i<NoOfCOLORS;i++) {
							if (ColorThresholds[i] == Integer.parseInt(tokens[1])) {
								color = i;
								key = Colors[i];
								break;
							}
						}
						if (oldKey != null)
							key = oldKey + "_" + key;
						keySet = currentPatterns.keySet();
						if (keySet.contains(key)) {
							edgesSet = (HashSet)currentPatterns.get(key);
							edgesSet.add(edge);
						}
						else {
							edgesSet = new HashSet<String> ();
							edgesSet.add(edge);
							currentPatterns.put(key, edgesSet);
						}
						/*
						e = new Edge (lhsNode, rhsNode, color);
						if (graphInstance != null) {
							if ( (edgeList = graphInstance.get(lhsNode)) == null) {
								edgeList = new ArrayList<Edge>();
								graphInstance.put(lhsNode, edgeList);
							}
							edgeList.add(e);
						}
						 */
					}
				}
				else { //We need to initialize the new instance
					cleanupPatterns(currentPatterns); //But first we throw away all those buckets which do not contain the minimum threshold of patterns.
					InstanceCounter++;
					graphInstance = new Hashtable<String, ArrayList<Edge>> ();
					BipartiteGraph.add(graphInstance);
					currentPatterns = new Hashtable<String, HashSet<String>> ();
					Patterns.add(currentPatterns);
					if (edgeCounter > maxEdgeCounter)
						maxEdgeCounter = edgeCounter;
					totalEdges += edgeCounter;
					edgeCounter = 0;
					quantumCounter++;
				}


			}
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		publishPatternsChrom();
		System.out.println("Max Edges in a quantum:" + maxEdgeCounter + ", totalEdges:" + totalEdges + ", quantums:" + quantumCounter);
		
	}

	public void cleanupPatterns (Hashtable<String, HashSet<String>> currentPatterns) {

		String nextKey;
		HashSet<String> edges;
		Set keySet = currentPatterns.keySet();
		Iterator it = keySet.iterator();
		ArrayList<String> bucketsToRemove = new ArrayList<String>();
		for (int i=0;i<keySet.size();i++) {
			nextKey = (String)it.next();
			edges = currentPatterns.get(nextKey);
			if (edges.size() < PATTERN_SUPPORT)
				bucketsToRemove.add(nextKey);
			//currentPatterns.remove(nextKey);
		}
		for (int i=0;i<bucketsToRemove.size();i++) {
			currentPatterns.remove(bucketsToRemove.get(i));
		}
	}



	//We try to search for the current edge in the previous epoch. If it doesn't exist here, we just drop this edge. 

	public String findKey (String edge) {

		String oldKey = null, nextKey;
		Set keySet;
		Hashtable<String, HashSet<String>> lastPatterns; //Points to the patterns discovered till previous epochs. 
		Iterator it;
		HashSet edges;

		if (InstanceCounter > 0) {

			lastPatterns = Patterns.get(InstanceCounter - 1);
			keySet = lastPatterns.keySet();
			it = keySet.iterator();
			for (int i=0;i<keySet.size();i++) {
				nextKey = (String)it.next();
				edges = lastPatterns.get(nextKey);
				if (edges.contains(edge)) {
					edges.remove(edge); //Remove this edge from the previous epoch as this edge has extended it's pattern to the next epoch.
					return nextKey;
				}

			}
		}
		return oldKey;
	}

	public void publishPatterns() {

		Hashtable<String, HashSet<String>> currentPatterns;
		String nextKey;
		HashSet<String> edges;
		Set keySet;
		Iterator it, it1;
		String[] tokens;
		int patternCounter=0;

		for (int i=MIN_PATTERN_LENGTH - 1; i< InstanceCounter;i++) {

			currentPatterns = Patterns.get(i);
			keySet = currentPatterns.keySet();
			it = keySet.iterator();
			for (int j=0;j<keySet.size();j++) {
				nextKey = (String)it.next();
				tokens = nextKey.split("_");
				if (tokens.length >=MIN_PATTERN_LENGTH) {
					edges = currentPatterns.get(nextKey);
					if (edges.size() >= PATTERN_SUPPORT) {
						patternCounter++;
						pr.println(nextKey + ", Epoch:" + i);
						pr.println("----------------------");
						it1 = edges.iterator();
						for (int k =0;k<edges.size();k++) {
							pr.print(it1.next() + ", ");
						}
						pr.println();
						pr.println("----------------------");
					}
				}
			}
		}
		pr.println("Total number of patterns:" + patternCounter);

	}

	public void publishPatternsChrom() {

		Hashtable<String, HashSet<String>> currentPatterns;
		String nextKey, someKey;
		HashSet<String> edges;
		Set keySet;
		Iterator it, it1;
		String[] tokens;
		int patternCounterMono=0, patternCounterMulti=0, patternCounterMany = 0, someCounter=0;;
		PatternStat counterKey;
		Hashtable<String, PatternStat> patternCounter = new Hashtable <String, PatternStat>();

		for (int i=MIN_PATTERN_LENGTH - 1; i< InstanceCounter;i++) {

			currentPatterns = Patterns.get(i);
			keySet = currentPatterns.keySet();
			it = keySet.iterator();
			for (int j=0;j<keySet.size();j++) {
				nextKey = (String)it.next();
				tokens = nextKey.split("_");
				if (tokens.length >=MIN_PATTERN_LENGTH) {
					edges = currentPatterns.get(nextKey);
					if (edges.size() >= PATTERN_SUPPORT) {
						
						someKey = tokens.length + "_" + isMonochrom(tokens);
						
						counterKey = (PatternStat)patternCounter.get(someKey);
						
						if (counterKey == null) {
							patternCounter.put(someKey, new PatternStat (1, edges.size()));
						}
						
						else {
							counterKey.update(edges.size());
							//patternCounter.put(someKey, new Integer (counterKey.intValue() + 1));	
							patternCounter.put(someKey, counterKey);
						}
						if (isMonochrom(tokens) == 1) {
							patternCounterMono++;
						}
						else if (isMonochrom(tokens) == 2)
							patternCounterMulti++;
						else {
							System.out.println(nextKey);
							patternCounterMany++;
						}
						pr.println(nextKey + ", Epoch:" + i);
						pr.println("----------------------");
						it1 = edges.iterator();
						for (int k =0;k<edges.size();k++) {
							pr.print(it1.next() + ", ");
						}
						pr.println();
						pr.println("----------------------");
					}
				}

			}
		}
		pr.println("Total number of monochromatic patterns:" + patternCounterMono);
		pr.println("Total number of non-monochromatic patterns:" + patternCounterMulti);
		pr.println("Total number of multi colored non-monochromatic patterns:" + patternCounterMany);
		pr.println("No of colors:" + NoOfCOLORS + ", Min support:" + PATTERN_SUPPORT + ", min pattern length:" + MIN_PATTERN_LENGTH + ", Epoch Size:" + EPOCH_LENGTH);
		
		printPatternCounter2(patternCounter);
		
		System.out.println("Total number of monochromatic patterns:" + patternCounterMono);
		System.out.println("Total number of non-monochromatic patterns:" + patternCounterMulti);
		System.out.println("Total number of multi colored non-monochromatic patterns:" + patternCounterMany);
		System.out.println("No of colors:" + NoOfCOLORS + ", Min support:" + PATTERN_SUPPORT + ", min pattern length:" + MIN_PATTERN_LENGTH + ", Epoch Size:" + EPOCH_LENGTH);

	}

	
	private void printPatternCounter(Hashtable<String, Integer>  patternCounter) {
		
		int count = patternCounter.size();
		
		Set keySet = patternCounter.keySet();
		Iterator it = keySet.iterator();
		String key;
		String[] tokens;
		for (int i=0;i<patternCounter.size();i++) {
			key = (String)it.next();
			tokens = key.split("_");
			System.out.println("Pattern Length:" + tokens[0] + ", no. of colors:" + tokens[1] + ", count" + ((Integer)patternCounter.get(key)).intValue());
		
			
		}
		
		
	}
	
private void printPatternCounter2(Hashtable<String, PatternStat>  patternCounter) {
		
		double avgSupport;
		
		int count = patternCounter.size();
		
		Set keySet = patternCounter.keySet();
		Iterator it = keySet.iterator();
		String key;
		String[] tokens;
		for (int i=0;i<patternCounter.size();i++) {
			key = (String)it.next();
			tokens = key.split("_");
			avgSupport = (double)((double)(((PatternStat)patternCounter.get(key)).support)/(double)(((PatternStat)patternCounter.get(key)).counter));
			System.out.println("Pattern Length:" + tokens[0] + ", no. of colors:" + tokens[1] + ", count:" + ((PatternStat)patternCounter.get(key)).counter + ", avg. support:" + avgSupport);
		
			
		}
	}
	
	private class PatternStat {
		int counter = 0; //No. of patterns
		int support = 0; //Total no. of edges, avg support for the patterns is support/counter
		public PatternStat (int counter, int support) {
			this.counter = counter;
			this.support = support;
		}
		public void update (int support) {
			
			this.counter++;
			this.support += support;
			
		}
	}

	
	private int isMonochrom (String[] tokens) {
		
		HashSet<String> colorSet = new HashSet<String>();
		for (int i=0;i<tokens.length;i++) {
			colorSet.add(tokens[i]);
		}
			
		return colorSet.size();
		
	}
	
	public void findPatterns () {

	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long time = System.currentTimeMillis(); 

		TimeVariantBipartiteGraph_2 tvbg = new TimeVariantBipartiteGraph_2("E:\\manoj\\3gBI\\Novartis\\Paper\\data\\", "dummyData_2_dense_20_20_8000_50_0.1.txt");

		tvbg.ConstructGraph();
		
		System.out.println("Time taken:" + (System.currentTimeMillis() - time));

		System.out.println("Done");

	}

}
