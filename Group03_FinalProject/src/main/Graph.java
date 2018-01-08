package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.princeton.cs.algs4.Bag;

/**
 * This abstract class represents and constructs a graph of the cities in
 * Ontario. The vertices are cities represented by an integer which coincides
 * with an indexed bag of adjacent vertices. The edges between adjacent vertices
 * represents cities that are connected by a difference in latitude and
 * longitude of 0.75. A very shallow breadth first search is performed to get
 * the adjacent cities to a given city. A static reference list of city names is
 * used to map integer values to city names throughout the program. The code is
 * inspired and sourced from the algorithms 4 textbook and the 2C03 lectures. It
 * is to be noted that this algorithm was designed to work for all the cities in
 * Canada however due to the sparse clusters of cities in other provinces within
 * our datasets the scope for just this section was limited to Ontario.
 * 
 * @author Abdul Moiz
 */
public class Graph {
	private static int V;
	private static Bag<Integer>[] adj;
	private static ArrayList<String[]> information = new ArrayList<String[]>();
	private static ArrayList<String> referenceList = new ArrayList<String>();

	/**
	 * This method must be called at the start of the program, it pulls all the
	 * data from the dataset containing cities and their positions in Ontario.
	 * it stores that data in an array list and the names in the reference list
	 * array list. It then also initializes the bags for the indexed adjacency
	 * lists for each vertex. Finally it calls the buildGraph method to actually
	 * create the edges between the vertices in the graph.
	 */
	public static void init() {
		try {
			Scanner input = new Scanner(new File("data/Graph/GraphAlgFile.csv"));
			while (input.hasNextLine()) {
				String current = input.nextLine();
				String[] splitString = current.split(",");
				information.add(splitString);
				referenceList.add(splitString[2]);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		V = information.size();
		adj = (Bag<Integer>[]) new Bag[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new Bag<Integer>();
		}
		buildGraph();
	}

	/**
	 * This method takes each vertex in the graph and connects it to its
	 * surrounding cities. This is done by checking the proximity of a city to
	 * all the other cities. Since the edges are undirected and two ways once a
	 * city has been checked it does not need to be ever checked again. Hence
	 * why the inner loop starts at i + 1.
	 */
	private static void buildGraph() {
		for (int i = 0; i < information.size(); i++)
			for (int j = i + 1; j < information.size() - 1; j++) {
				if (checkProximity(i, j)) {
					addEdge(i, j);
				}
			}
	}

	/**
	 * This method takes two integer values and find the city names that
	 * correspond with them from the referenceList. It then checks whether the
	 * cities fall within a 0.75 difference in longitude and latitude of each
	 * other using a series of if statements.
	 * 
	 * @param a
	 *            - The index from the reference list of the first city.
	 * @param b
	 *            - The index from the reference list of the second city.
	 * @return - A boolean is returned indicating whether these two cities are
	 *         deemed conencted.
	 */
	private static boolean checkProximity(int a, int b) {
		return ((Double.parseDouble(information.get(b)[0]) - 0.75 <= Double.parseDouble(information.get(a)[0]))
				&& (Double.parseDouble(information.get(a)[0]) <= Double.parseDouble(information.get(b)[0]) + 0.75)
				&& (Double.parseDouble(information.get(b)[1]) - 0.75 <= Double.parseDouble(information.get(a)[1]))
				&& (Double.parseDouble(information.get(a)[1]) <= Double.parseDouble(information.get(b)[1]) + 0.75));
	}

	/**
	 * A edge is created between two vertices by inserting each vertex in the
	 * other's adjacency list.
	 * 
	 * @param v
	 *            - The index value of the first vertex.
	 * @param w
	 *            - The index value of the second vertex.
	 */
	public static void addEdge(int v, int w) {
		adj[v].add(w);
		adj[w].add(v);
	}

	/**
	 * The adjacent cities to a given city represented by its name. This is done
	 * by going through that selected city's adjacency list and for each value
	 * referencing to the city name in the reference list. These city names are
	 * then added to a temporary iterable list which is then finally returned.
	 * 
	 * @param s
	 *            - The name of the city who's connected cities are being found.
	 * @return - The iterable list of connected cities is returned.
	 */
	public static Iterable<String> adj(String s) {
		Bag<String> temp = new Bag<String>();
		int v = referenceList.indexOf(s);
		for (int i : adj[v]) {
			temp.add(referenceList.get(i));
		}
		return temp;

	}

	public static void main(String[] args) {
		Graph.init();
		ArrayList<String> closeCities = new ArrayList<String>();
		String s = "Oshawa";
		for (String i : adj(s)) {
			closeCities.add(i);
		}
		System.out.println(closeCities.toString());
	}

}
