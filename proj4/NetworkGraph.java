import java.io.*;
import java.util.*;

public class NetworkGraph {

	private final int V;
	private int E;
	private Bag<ConnectionEdge>[] adj;
	public int[][] path;

	public NetworkGraph(int V){
		if (V < 0) {
				throw new RuntimeException("Number of vertices must be nonnegative");
		}
		this.V = V;
		this.E = 0;
		adj = (Bag<ConnectionEdge>[]) new Bag[V];
		for (int v = 0; v < V; v++) {
				adj[v] = new Bag<ConnectionEdge>();
		}
	}

	public int V() {
			return V;
	}

	/**
	 * Return the number of edges in this graph.
	 */
	public int E() {
			return E;
	}

	public void add(ConnectionEdge edge, ConnectionEdge[] graph) {
		int i = edge.getStart();

		if (graph[i] == null) {
			graph[i] = edge;
		}
		else {
			ConnectionEdge cur = graph[i];
			while (cur.getNext() != null) {
				cur = cur.getNext();
			}
			cur.setNext(edge);
		}
		int v = edge.either();
		int w = edge.other(v);
		adj[v].add(edge);
		adj[w].add(edge);
		E++;
	}

	public ConnectionEdge connectionExists(int from, int to, ConnectionEdge[] graph) {
		ConnectionEdge cur = graph[from];
		while (cur != null) {
			if (cur.getEnd() == to) {
				return cur;
			}
			cur = cur.getNext();
		}
		return null;
	}

	public String lowestLatencyPath(int start, int end, ConnectionEdge[] graph) {
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		ArrayList<Double> time = new ArrayList<Double>();
		ConnectionVertex[] route  = new ConnectionVertex[graph.length];
		double bw = Double.POSITIVE_INFINITY;

		for (int i = 0; i < graph.length; i++) {
			visited.add(i, false);
			time.add(i, Double.POSITIVE_INFINITY);
		}
		ConnectionVertex firstVert = new ConnectionVertex(start);
		route[start] = firstVert;
		time.set(start, 0.0);

		int pos = start;
		while (pos >= 0) {
			ConnectionEdge sibling = graph[pos];
			while(sibling != null) {
				if (!visited.get(sibling.getEnd())) {
					double moreTime = 0.0;
					moreTime = sibling.getLatency();
					double path = (time.get(pos) + moreTime);
					if (path < time.get(sibling.getEnd())) {
						time.set(sibling.getEnd(), path);
						ConnectionVertex[] newVert = route[pos].duplicate();
						newVert[1].next = new ConnectionVertex(sibling.getEnd());
						route[sibling.getEnd()] = newVert[0];
					}
				}
				sibling = sibling.getNext();
			}
			visited.set(pos, true);
			double min = Double.POSITIVE_INFINITY;
			int index = -1;
			for (int i = 0; i < visited.size(); i++) {
				if ((!visited.get(i)) && time.get(i) < min) {
					min = time.get(i);
					index = i;
				}
			}
			pos = index;
		}

		if (start == end) {
			System.out.println("The lowest latency is 0.\nThe bandwith is 0.");
		}
		else {
			ConnectionVertex lowest = route[end];
			while (lowest != null) {
				int begin = lowest.value;
				ConnectionVertex nextVert = lowest.next;
				if (nextVert != null) {
					ConnectionEdge current = connectionExists(begin, nextVert.value, graph);
					if(current != null && current.getBandwidth() < bw) {
						bw = current.getBandwidth();
					}
					System.out.println();
					System.out.print("    A " + current.getLen() + " meter " + current.getCable() + " cable ");
					System.out.printf("from %d -> %d\n    with a bandwidth of %.1f megabits per second\n    and latency of %.4f ms\n", current.getStart(), current.getEnd(), current.getBandwidth(), (current.getLatency() * 1000000));
				}
				lowest = lowest.next;
			}
		}
		return "\n    Bandwith along the path is: " + bw + " megabits per second";
	}


	public String isCopperOnly(ConnectionEdge graph[]) {
		ArrayList<Integer> q = new ArrayList<Integer>();
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		for (int i = 0; i < graph.length; i++) {
			visited.add(i, false);
		}
		q.add(0);

		while (q.size() >= 1) {
			int firstIndex = q.remove(0);
			visited.set(firstIndex, true);
			ConnectionEdge sibling = graph[firstIndex];
			while (sibling != null) {
				if ((!visited.get(sibling.getEnd())) && sibling.getCable().equals("copper")) {
					q.add(sibling.getEnd());
				}
				sibling = sibling.getNext();
			}
		}
		int i = 0;
		while (i < visited.size()) {
			if (!visited.get(i)) {
        return "    The graph is not copper-only connected\n";
      }
			i++;
		}
		return "    The graph is copper-only connected\n";
	}

	public void dijkLowestLatencyST(NetworkGraph g) {
		// compute MST for latency using dijkstra algo from recitation

		path = new int[V][V]; //bitmap to see what connections were seen
		PrimDijkstra pd = new PrimDijkstra(g);

		// run Dijksra's algorithm from vertex 0
		int s = 0;
		DijkstraNetworkSP sp = new DijkstraNetworkSP(g, s);

		double latency1 = 0.0;
		double latency2 = 0.0;
		int connections1 = 0;
		int connections2 = 0;
		/*for (ConnectionEdge e : g.edges()) {
				path[e.getStart()][e.getEnd()] = 1;
				//check that the backedge isn't in the bitmap
				if (path[e.getEnd()][e.getStart()] != 1){
					//print the connection
					e.connectionString();
					connections1++;
					//add to the total latency
					latency1 += (e.getLatency()*1000000);
				}
		}
		System.out.printf("\n    Average latency on the path: %.4f\n", (latency1/connections1));*/

		// print shortest path
		//StdOut.println("Shortest paths from " + s);
		//StdOut.println("------------------------");
		for (int v = 0; v < g.V(); v++) {
				if (sp.hasPathTo(v)) {
						for (ConnectionEdge e : sp.pathTo(v)) {
							if (path[e.getEnd()][e.getStart()] != 1){
								e.connectionString();
								connections2++;
								latency2 += (e.getLatency()*1000000);
							}
						}
				}
				else {
						StdOut.printf("%d to %d         no path\n", s, v);
				}
		}
		System.out.printf("\n    Average latency on the path: %.4f\n", (latency2/connections2));
	}

	/**
	 * Return the edges incident to vertex v as an Iterable. To iterate over the
	 * edges incident to vertex v, use foreach notation:
	 * <tt>for (Edge e : graph.adj(v))</tt>.
	 */
	public Iterable<ConnectionEdge> adj(int v) {
			return adj[v];
	}

	/**
	 * Return all edges in this graph as an Iterable. To iterate over the edges,
	 * use foreach notation:
	 * <tt>for (Edge e : graph.edges())</tt>.
	 */
	public Iterable<ConnectionEdge> edges() {
			Bag<ConnectionEdge> list = new Bag<ConnectionEdge>();
			for (int v = 0; v < V; v++) {
					int selfLoops = 0;
					for (ConnectionEdge e : adj(v)) {
							if (e.other(v) > v) {
									list.add(e);
							} // only add one copy of each self loop
							else if (e.other(v) == v) {
									if (selfLoops % 2 == 0) {
											list.add(e);
									}
									selfLoops++;
							}
					}
			}
			return list;
	}

	public boolean connected(int start, int end, ConnectionEdge[] g) {
		ArrayList<Integer> q = new ArrayList<Integer>();
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		for (int i = 0; i < g.length; i++) {
			visited.add(i, false);
		}
		visited.set(start, true);
		visited.set(end, true);

		int free = 0;
		while (free == start || free == end) {
			free++;
		}

		q.add(free);
		while (q.size() > 0) {
			int firstIndex = q.remove(0);
			visited.set(firstIndex, true);
			ConnectionEdge sibling = g[firstIndex];
			while (sibling != null) {
				if (sibling.getStart() != start && sibling.getStart() != end && sibling.getEnd() != start && sibling.getEnd() != end && (!visited.get(sibling.getEnd()))) {
					q.add(sibling.getEnd());
				}
				sibling = sibling.getNext();
			}
		}

		int i = 0;
		while (i < visited.size()) {
			if (!visited.get(i)) {
        return false;
      }
			i++;
		}
		return true;
	}

	public String willGraphFail(ConnectionEdge[] graph) {
		for (int i = 0; i < graph.length; i++) {
				for (int j = i+1; j < graph.length; j++) {
					if (!connected(i, j, graph)) {
						return "    The graph will not remain connected if vertices " + i + " and " + j + "were to fail";
					}
				}
			}
		return "    The graph will remain connected if any 2 vertices were to fail";
	}
}
