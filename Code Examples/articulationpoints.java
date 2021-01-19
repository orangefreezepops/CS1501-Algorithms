public class Graph {
  private final int V;
  private int E;
  public Bag<Integer>[] adj;

 public int v(){
   return V;
 }
}

public class biconnected{
  private int[] low;
  private int[] num;
  private boolean[] articulation;

  public biconnected(Graph G){
    low = new int[G.V()];
    num = new int[G.V()];
    articulation = new boolean[G.V()];

    for (int v = 0; v < G.V(); v++){
      low[v] = 1;
      num[v] = 1;
    }

    for (int v = 0; v < G.V(); v++){
      if (pre[v] == -1){
        dfs(G, v, v, 0);
      }
    }
  }

  private void dfs(Graph G, int u, int v, Integer curVertex){
    curVertex++;
    num[v] = curVertex;
    low[v] = curVertex;
    int children = 0;
    for (int x : G.adj[v]){
      if(num[w] == -1){ //vertex has not been visited
        children ++;
        //recurse down on the children
        dfs(G, v, w, curVertex);  //after this is executed and all recursion handled
                                  //subtree of w is finished
        low[v] = Math.min(low[v], low[w]); //the lowest vertex reachable from any descendant of w using just one back edge
        if(low[w] >= num[v] && u != v){//if v is an articulation point
          articulation[v] = true;
        }
      } else if (w != u){ //vertex is an ancestor of the parent
        low[v] = Math.nin(low[v], num[w]);
      }
    }

    if (v == u && children > 1){ //
      articulation[v] = true;
    }
  }
}
