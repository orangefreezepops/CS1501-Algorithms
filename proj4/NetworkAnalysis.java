import java.io.*;
import java.util.*;

public class NetworkAnalysis {

  public static NetworkGraph g;
  public static ConnectionEdge[] edges;

  public static void printMenu(){
    System.out.println("\nUse the number pad to select one of the following:\n" +
      "\n    1. Find lowest latency path\n    2. Determine if graph is copper-only connected\n    3. Find lowest average latency spanning tree\n" +
      "    4. Determine whether or not the graph would fail if any two vertices are removed\n    0. Quit the program");
  }

  public static void run(){
    Scanner input = new Scanner(System.in);
    String option = input.nextLine();

    switch(option){
      case "1": //lowest latency path
        System.out.print("\n---Lowest latency path---\nEnter your first vertex: ");
        int vertex1 = Integer.parseInt(input.nextLine());
        System.out.print("\nEnter your second vertex: ");
        int vertex2 = Integer.parseInt(input.nextLine());
        if (checkVertexValidity(vertex1, vertex2, edges)){
          System.out.println(g.lowestLatencyPath(vertex1, vertex2, edges));
        } else {
          System.out.println("    Try again");
        }
        break;
      case "2": //copper-only?
        System.out.print("\n---Copper-only Connected?---\n");
        System.out.println(g.isCopperOnly(edges));
        break;
      case "3": //lowest average latency spanning tree
        System.out.print("\n---Lowest average latency spanning tree---\n");
        g.dijkLowestLatencyST(g);
        break;
      case "4": //will graph fail?
        System.out.print("\n---Will removing any 2 vertices cause the graph to fail?---\n ");
        System.out.println(g.willGraphFail(edges));
        break;
      case "0": //quit
        System.out.println("Thank you, goodbye!");
        System.exit(0);
      default: //bad input try again
        System.out.println("That isn't a recognized command. Here's the menu again...");
        printMenu();
        break;
    }
  }

  public static boolean checkVertexValidity(int v1, int v2, ConnectionEdge[] e){
    if (v1 < 0 || v1 > e.length - 1){
      System.out.println("\n    Vertex 1 (" + v1 + ") is not a valid vertex... vertices should be in the range of 0 - " + (e.length -1));
      return false;
    }
    if (v2 < 0 || v2 > e.length - 1){
      System.out.println("\n     Vertex 2 (" + v2 + ") is not a valid vertex... vertices should be in the range of 0 - " + (e.length -1));
      return false;
    }
    return true;
  }

  public static void main(String[] args) throws IOException {

    File data = new File(args[0]); //given input file

    //set up the graph given the input file
    try {
      Scanner scanner = new Scanner(data);
      int vertices = Integer.parseInt(scanner.nextLine()); //read the first line for number of vertices
      g = new NetworkGraph(vertices);                                //create the graph
      edges = new ConnectionEdge[vertices];
      //parse the lines of the file
      //and store the values to their corresponding network properties
      while(scanner.hasNextLine()){                           //read the full line
        String line = scanner.nextLine();                     //current line
        Scanner lineScan = new Scanner(line);                 //scan the current line
        lineScan.useDelimiter(" ");                           //use delimiter [space]
        int start = Integer.parseInt(lineScan.next());        //start of the edge
        int end = Integer.parseInt(lineScan.next());          //end of the edge
        String cable = lineScan.next();                       //type of cable
        int bandwidth = Integer.parseInt(lineScan.next());    //bandwith
        int length = Integer.parseInt(lineScan.next());       //length of the wire

        ConnectionEdge w1 = new ConnectionEdge(start, end, cable, bandwidth, length); //direction 1
        ConnectionEdge w2 = new ConnectionEdge(end, start, cable, bandwidth, length); //direction 2

        g.add(w1, edges);   //add both directions to the graph
        g.add(w2, edges);
      }
    } catch (FileNotFoundException e) {
      System.out.println("File not found.");
    }

    //start the fun stuff
    Scanner input = new Scanner(System.in);
    System.out.println("\nHello! welcome to the Network Analysis application! what would you like to do?\n");
    System.out.println("Menu - m\nQuit - <any other key>\n");
    String response = input.nextLine();
    while (response.equals("m")){
      printMenu();
      run();
    }

    System.out.println("Thank you, goodbye!");
    System.exit(0);
  }
}
