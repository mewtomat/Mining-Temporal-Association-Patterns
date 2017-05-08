package GraphAnalysis;

public class Edge {
	
	public String edgeIDStart = null;
	public String edgeIDEnd = null;
	
	public int color = -1;
	
	public Edge (String edgeIDStart, String edgeIDEnd, int color) {
		this.edgeIDStart = edgeIDStart;
		this.edgeIDEnd = edgeIDEnd;
		this.color = color;
	}
	
	public Edge (String edgeID, int color) {
		this.edgeIDEnd = edgeID;
		this.color = color;
	}
	
	public int getEdgeColor () {
		return color;
	}
	
	public String getEdgeID () {
		return edgeIDEnd;
	}

}
