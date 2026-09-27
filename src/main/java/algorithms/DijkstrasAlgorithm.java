package algorithms;

import visualizer.Edge;
import visualizer.Vertex;
import java.util.*;

public class DijkstrasAlgorithm implements GraphAlgorithm {

    // NEW: remember what happened during the last run so the GUI can animate it
    private final List<Vertex> visitOrder = new ArrayList<>();
    private final List<Edge> shortestPathEdges = new ArrayList<>();

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {
        // NEW: forget the previous run
        visitOrder.clear();
        shortestPathEdges.clear();
        visitOrder.add(start);

        // NEW: for every vertex, the last edge on the shortest path that reaches it
        Map<Vertex, Edge> previousEdge = new HashMap<>();

        // Initialize the map that will store the Vertex: Weight pairs
        Map<Vertex, Double> outputMap = new TreeMap<>();

        // Initialize distances with infinity for all vertices except the source
        Map<Vertex, Double> distances = new HashMap<>();
        for (Vertex vertex: graph.keySet()) {
            if (!vertex.equals(start)) distances.put(vertex, Double.POSITIVE_INFINITY);
        }

        // Mark all vertices except source as unprocessed
        Set<Vertex> unprocessedVertices = new HashSet<>(distances.keySet());

        // Find the edges of the start Vertex
        List<Edge> startVertexEdges = graph.get(start);

        // Update distances to unprocessed neighbors of start vertex
        for (Edge edge: startVertexEdges) {
            Vertex neighbor = edge.getVertex2();
            int weight = edge.getWeight();
            if (unprocessedVertices.contains(neighbor)) {
                double newDistance = (double) weight;
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousEdge.put(neighbor, edge);  // NEW
                }
            }
        }

        // Dijkstra's algorithm
        while (!unprocessedVertices.isEmpty()) {
            // Find the Vertex with the smallest distance
            Vertex current = findSmallestDistanceVertex(unprocessedVertices, distances);

            // Add the current vertex with its weight to the outputMap and mark it as processed
            outputMap.put(current, distances.get(current));
            unprocessedVertices.remove(current);

            // NEW: only reachable vertices are part of the animation and the shortest-path tree
            if (distances.get(current) != Double.POSITIVE_INFINITY) {
                visitOrder.add(current);
                shortestPathEdges.add(previousEdge.get(current));
            }

            // Find the edges of the current Vertex
            List<Edge> currentVertexEdges = graph.get(current);

            // Update distances to unprocessed neighbors
            for (Edge edge: currentVertexEdges) {
                Vertex neighbor = edge.getVertex2();
                double weight = (double) edge.getWeight();
                if (unprocessedVertices.contains(neighbor)) {
                    double newDistance = distances.get(current) + weight;
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        previousEdge.put(neighbor, edge);  // NEW
                    }
                }
            }
        }

        // If the graph only has the start vertex, there is nothing to report
        if (outputMap.isEmpty()) {
            return "Dijkstra : " + start.getId() + " (no other vertices)";
        }

        String shortestPaths = processDistances(outputMap);
        return shortestPaths.substring(0, shortestPaths.length() - 2);
    }

    private static Vertex findSmallestDistanceVertex(Set<Vertex> unprocessedVertices, Map<Vertex, Double> distances) {
        Vertex smallestVertex = null;
        double smallestDistance = Double.POSITIVE_INFINITY;

        for (Vertex vertex: unprocessedVertices) {
            if (distances.get(vertex) <= smallestDistance) {
                smallestVertex = vertex;
                smallestDistance = distances.get(vertex);
            }
        }

        return smallestVertex;
    }

    private String processDistances(Map<Vertex, Double> map) {
        String shortestPaths = "";
        for (Vertex vertex: map.keySet()) {
            Double weight = map.get(vertex);
            if (weight == Double.POSITIVE_INFINITY) {
                shortestPaths += vertex.getId() + "=" + weight + ", ";
            } else {
                shortestPaths += vertex.getId() + "=" + weight.intValue() + ", ";
            }
        }
        return shortestPaths;
    }

    @Override
    public List<Vertex> getVisitOrder() {
        return new ArrayList<>(visitOrder);
    }

    @Override
    public List<Edge> getResultEdges() {
        return new ArrayList<>(shortestPathEdges);
    }
}
