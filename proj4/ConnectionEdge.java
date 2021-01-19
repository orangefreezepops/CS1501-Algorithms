public class ConnectionEdge implements Comparable<ConnectionEdge> {
	private int vStart;								//starting vertex
	private int vEnd;									//ending vertex
	private String cableMat;					//materilal of rthe cable
	private double bandwidth;					//bandwidth of the edge
	private int len;									//length of the wire
	private ConnectionEdge nextEdge;	//next edge in the graph
	private double latency; 					//latency of the cinnection (weight)

	private static final int COPPER_SPEED = 230000000;
	private static final int OPTICAL_SPEED = 200000000;

	public ConnectionEdge(int start, int end, String cableType, double b, int l) {
		setStart(start);
		setEnd(end);
		setCable(cableType);
		setBandwidth(b);
		setLen(l);
		nextEdge = null;
		latency = this.cableMat.equals("copper") ? ((double)this.len / (double)COPPER_SPEED) : ((double)this.len / (double)OPTICAL_SPEED);
	}

	public double getLatency(){
		return this.latency;
	}

	public void setStart(int start){
		this.vStart = start;
	}

	public int getStart(){
		return this.vStart;
	}

	public void setEnd(int end){
		this.vEnd = end;
	}

	public int getEnd(){
		return this.vEnd;
	}

	public void setCable(String material){
		this.cableMat = material;
	}

	public String getCable(){
		return this.cableMat;
	}

	public void setBandwidth(double b){
		this.bandwidth = b;
	}

	public double getBandwidth(){
		return this.bandwidth;
	}

	public void setLen(int l){
		this.len = l;
	}

	public int getLen(){
		return this.len;
	}

	public void setNext(ConnectionEdge n){
		this.nextEdge = n;
	}

	public ConnectionEdge getNext(){
		return this.nextEdge;
	}

	//adaptations from Edge.java for this project

	/**
		* Compare edges by weight.
		*/
	 public int compareTo(ConnectionEdge that) {
			 if      (this.latency < that.latency) return -1;
			 else if (this.latency > that.latency) return +1;
			 else                                    return  0;
	 }

	/**
		* Return either endpoint of this edge.
		*/
	 public int either() {
			 return this.vStart;
	 }

	/**
		* Return the endpoint of this edge that is different from the given vertex
		* (unless a self-loop).
		*/
	 public int other(int vertex) {
			 if      (vertex == this.vStart) return this.vEnd;
			 else if (vertex == this.vEnd) return this.vStart;
			 else throw new RuntimeException("Illegal endpoint");
	 }

	 /**
     * Return a string representation of this edge.
     */
    public void connectionString() {
			System.out.print("\n    " + vStart + " <--> " + vEnd + ", ");
      System.out.printf("Latency: %.4f", latency*1000000);
			System.out.println();
    }
}
