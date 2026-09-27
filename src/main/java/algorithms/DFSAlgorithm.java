package algorithms;

import visualizer.Edge;
import visualizer.Vertex;

import java.util.*;

public class DFSAlgorithm implements GraphAlgorithm {

    // NEW: remember what happened during the last run so the GUI can animate it
    private final List<Vertex> visitOrder = new ArrayList<>();
    private final List<Edge> treeEdges = new ArrayList<>();

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {
        // NEW: forget the previous run
        visitOrder.clear();
        treeEdges.clear();

        // If the chosen vertex doesn't have any edges, then just return it
        if (graph.get(start).isEmpty()) {
            visitOrder.add(start);
            return "DFS -> " + start.getId();
        }

        // Initialize the string that will contain the traversal
        String traversalPath = "DFS : ";

        // Initialize a set to keep track of visited Vertices
        Set<Vertex> visited = new HashSet<>();

        // Call helper on Start Vertex
        traversalPath += helper(graph, start, visited);

        // Iterate through all other nodes in the graph
        for (Vertex vertex: graph.keySet()) {
            if (!visited.contains(vertex) && !graph.get(vertex).isEmpty()) {
                traversalPath += helper(graph, vertex, visited);
            }
        }

        return traversalPath.substring(0, traversalPath.length() - 4);
    }

    private String helper(Map<Vertex, List<Edge>> graph, Vertex vertex, Set<Vertex> visited) {
        // The output string
        String output = "";

        // Mark the current Vertex as visited
        visited.add(vertex);

        // Process the current Vertex
        output += processVertex(vertex);
        visitOrder.add(vertex);  // NEW

        // Find the Edges of the current Vertex
        List<Edge> currentVertexEdges = graph.get(vertex);

        // Sort the edges from the lowest weight (first to traverse) to the highest weight (last to traverse)
        Collections.sort(currentVertexEdges);

        // Recursively visit all unvisited neighbors of the current Vertex
        for (Edge edge: currentVertexEdges) {
            Vertex neighbor = edge.getVertex2();
            if (!visited.contains(neighbor)) {
                treeEdges.add(edge);  // NEW: this edge is how DFS reached the neighbor
                output += helper(graph, neighbor, visited);
            }
        }

        return output;
    }

    @Override
    public List<Vertex> getVisitOrder() {
        return new ArrayList<>(visitOrder);
    }

    @Override
    public List<Edge> getResultEdges() {
        return new ArrayList<>(treeEdges);
    }
}
