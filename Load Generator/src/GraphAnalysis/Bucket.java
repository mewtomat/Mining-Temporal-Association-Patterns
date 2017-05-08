package GraphAnalysis;

import java.util.ArrayList;

public class Bucket {
	
	public int bucketId = -1;
	public ArrayList<Integer> bucketColors = null; //This list contains the colors of pattern seen so far.
	
	ArrayList<Edge> edgesList = null;
	
	public Bucket (int bucketId, int bucketColor) {
		edgesList = new ArrayList<Edge>();
		bucketColors = new ArrayList<Integer>();
	}
	
	public int getBucketId () {
		return bucketId;
	}
	
	public ArrayList<Integer> getBucketColor () {
		return bucketColors;
	}
	
}
