/*************************************************************************
 *  Compilation:  javac DijkstraSP.java
 *  Execution:    java DijkstraSP V E
 *  Dependencies: EdgeWeightedDigraph.java IndexMinPQ.java Stack.java DirectedEdge.java
 *
 *  Dijkstra's algorithm. Computes the shortest path tree.
 *  Assumes all weights are nonnegative.
 *

 //edited for Network analysis project
 *************************************************************************/

public class DijkstraNetworkSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private ConnectionEdge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    public DijkstraNetworkSP(NetworkGraph G, int s) {
        distTo = new double[G.V()];
        edgeTo = new ConnectionEdge[G.V()];
        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (ConnectionEdge e : G.adj(v))
                relax(e);
        }

        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(ConnectionEdge e) {
        int v = e.getStart(), w = e.getEnd();
        if (distTo[w] > distTo[v] + e.getLatency()) {
            distTo[w] = distTo[v] + e.getLatency();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.change(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    // length of shortest path from s to v
    public double distTo(int v) {
        return distTo[v];
    }

    // is there a path from s to v?
    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    // shortest path from s to v as an Iterable, null if no such path
    public Iterable<ConnectionEdge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<ConnectionEdge> path = new Stack<ConnectionEdge>();
        for (ConnectionEdge e = edgeTo[v]; e != null; e = edgeTo[e.getStart()]) {
            path.push(e);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(NetworkGraph G, int s) {

        // check that edge weights are nonnegative
        for (ConnectionEdge e : G.edges()) {
            if (e.getLatency() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (ConnectionEdge e : G.adj(v)) {
                int w = e.getEnd();
                if (distTo[v] + e.getLatency() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            ConnectionEdge e = edgeTo[w];
            int v = e.getStart();
            if (w != e.getEnd()) return false;
            if (distTo[v] + e.getLatency() != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }
}
