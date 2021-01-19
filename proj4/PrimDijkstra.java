// CS 1501 Fall 2020
// // Modification of Sedgewick Eager Prim and Dijkstra Algorithm to show detailed trace

/******************************************************************************
 *  Compilation:  javac PrimDijkstra.java
 *  Execution:    java PrimMST V E
 *  Dependencies: NetworkGraph.java ConnectionEdge.java Queue.java IndexMinPQ.java
 *                UF.java
 *
 *  Prim's algorithm to compute a minimum spanning forest.
 *
 ******************************************************************************/

public class PrimDijkstra {
    private ConnectionEdge[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private double[] distTo;      // distTo[v] = weight of shortest such edge
    private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
    private IndexMinPQ<Double> pq;

    public PrimDijkstra(NetworkGraph G) {
        edgeTo = new ConnectionEdge[G.V()];
        distTo = new double[G.V()];
        marked = new boolean[G.V()];
        pq = new IndexMinPQ<Double>(G.V());
        for (int v = 0; v < G.V(); v++) distTo[v] = Double.POSITIVE_INFINITY;

        for (int v = 0; v < G.V(); v++)      // run from each vertex to find
            if (!marked[v]) dijkstra(G, v);      // minimum spanning forest

        // check optimality conditions
        assert check(G);
    }

	  // run Dijkstra's algorithm in graph G, starting from vertex s
    private void dijkstra(NetworkGraph G, int s) {
        distTo[s] = 0.0;
        pq.insert(s, distTo[s]);
        //showPQ(pq);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            //System.out.println("	Next Vertex (Weight): " + v + " (" + distTo[v] + ")");
            scanD(G, v);
            //showPQ(pq);
        }
    }

	  // scan vertex v Dijkstra
    private void scanD(NetworkGraph G, int v) {
        marked[v] = true;
        //System.out.println("	Checking neighbors of " + v);
        for (ConnectionEdge e : G.adj(v)) {
            int w = e.other(v);
            //System.out.print("		Neighbor " + w);
            if (marked[w])
            {
            	//System.out.println(" is in the tree ");
            	continue;         // v-w is obsolete edge
            }
            if (distTo[v] + e.getLatency() < distTo[w]) {
            	//System.out.print(" OLD latency: " + distTo[w]);
                distTo[w] = distTo[v] + e.getLatency();
                edgeTo[w] = e;
                //System.out.println(" NEW latency: " + distTo[w]);
                if (pq.contains(w))
                {
                		pq.change(w, distTo[w]);
                		//System.out.println("			PQ changed");
                }
                else
                {
                		pq.insert(w, distTo[w]);
                		//System.out.println("			Inserted into PQ");
                }
            }
            else{
              //System.out.println(" latency " + distTo[w] + " NOT CHANGED");
            }
        }
    }

    private void showPQ(IndexMinPQ<Double> pq)
    {
    	System.out.print("PQ Contents: ");
    	for (Integer i : pq) {
            System.out.print("(V: " + i + ", E: " + distTo[i] + ") ");
        }
        System.out.println();
    }

    // return iterator of edges in MST
    public Iterable<ConnectionEdge> edges() {
        Bag<ConnectionEdge> mst = new Bag<ConnectionEdge>();
        for (int v = 0; v < edgeTo.length; v++) {
            ConnectionEdge e = edgeTo[v];
            if (e != null) {
                mst.add(e);
            }
        }
        return mst;
    }


    // return weight of MST
    public double latency() {
        double totLatency = 0.0;
        for (ConnectionEdge e : edges())
            totLatency += e.getLatency();
        return totLatency;
    }


    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(NetworkGraph G) {
      // check weight
      double totLatency = 0.0;
      for (ConnectionEdge e : edges()) {
          totLatency += e.getLatency();
      }
      double EPSILON = 1E-12;
      if (Math.abs(totLatency - latency()) > EPSILON) {
          //System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", totLatency, latency());
          return false;
      }

      // check that it is acyclic
      UF uf = new UF(G.V());
      for (ConnectionEdge e : edges()) {
          int v = e.either(), w = e.other(v);
          if (uf.connected(v, w)) {
              //System.err.println("Not a forest");
              return false;
          }
          uf.union(v, w);
      }

      // check that it is a spanning forest
      for (ConnectionEdge e : edges()) {
          int v = e.either(), w = e.other(v);
          if (!uf.connected(v, w)) {
              System.err.println("Not a spanning forest");
              return false;
          }
      }

      // check that it is a minimal spanning forest (cut optimality conditions)
      for (ConnectionEdge e : edges()) {
          int v = e.either(), w = e.other(v);

          // all edges in MST except e
          uf = new UF(G.V());
          for (ConnectionEdge f : edges()) {
              int x = f.either(), y = f.other(x);
              if (f != e) uf.union(x, y);
          }

          // check that e is min weight edge in crossing cut
          for (ConnectionEdge f : G.edges()) {
              int x = f.either(), y = f.other(x);
              if (!uf.connected(x, y)) {
                  if (f.getLatency() < e.getLatency()) {
                      //System.err.println("Edge " + f + " violates cut optimality conditions");
                      return false;
                  }
              }
          }

      }
      return true;
  }
}
