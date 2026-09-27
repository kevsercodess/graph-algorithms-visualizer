package algorithms;

import visualizer.Edge;
import visualizer.Vertex;

import java.util.*;

public class BFSAlgorithm implements GraphAlgorithm {

    // NEW: remember what happened during the last run so the GUI can animate it
    private final List<Vertex> visitOrder = new ArrayList<>();
    private final List<Edge> treeEdges = new ArrayList<>();

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {
        // NEW: forget the previous run
        visitOrder.clear();
        treeEdges.clear();

        // Initialize the string that will contain the traversal
        String traversalPath = "BFS : ";

        // Initialize a set to keep track of visited Vertices
        Set<Vertex> visited = new HashSet<>();

        // Initialize a queue for BFS
        Queue<Vertex> queue = new LinkedList<>();

        // Mark the start Vertex as visited and enqueue it
        visited.add(start);
        queue.offer(start);

        // BFS traversal
        while (!queue.isEmpty()) {
            // Dequeue a Vertex from the queue
            Vertex current = queue.poll();

            // Process the current Vertex
            traversalPath += processVertex(current);
            visitOrder.add(current);  // NEW

            // Find the Edges of the current Vertex
            List<Edge> currentVertexEdges = graph.get(current);

            // Sort the edges from the lowest weight (first to traverse) to the highest weight (last to traverse)
            Collections.sort(currentVertexEdges);

            // Visit all unvisited neighbors of the current Vertex
            for (Edge edge: currentVertexEdges) {
                Vertex neighbor = edge.getVertex2();
                if (!visited.contains(neighbor)) {
                    // Mark neighbor as visited and enqueue
                    visited.add(neighbor);
                    queue.offer(neighbor);
                    // NEW: this edge is how BFS discovered the neighbor
                    treeEdges.add(edge);
                }
            }
        }

        return traversalPath.substring(0, traversalPath.length() - 4);
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
