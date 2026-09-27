package algorithms;

import visualizer.Edge;
import visualizer.Vertex;

import java.util.*;

/*
NEW: Kruskal's Algorithm for the Minimum Spanning Tree (MST).

Idea: sort all edges from the lowest to the highest weight and add them one by one,
skipping any edge that would create a cycle. A Union-Find (disjoint set) structure
tells us quickly whether two vertices are already connected in the tree.

To be comparable with Prim's Algorithm, only the part of the graph that can be
reached from the chosen start vertex is used.

Output example: Kruskal : A-B(1), B-D(2), C-D(2) | Total weight = 5
 */
public class KruskalsAlgorithm implements GraphAlgorithm {

    private final List<Vertex> visitOrder = new ArrayList<>();
    private final List<Edge> edgesOfMST = new ArrayList<>();

    @Override
    public String run(Map<Vertex, List<Edge>> graph, Vertex start) {
        // Forget the previous run
        visitOrder.clear();
        edgesOfMST.clear();
        visitOrder.add(start);

        // If the chosen vertex doesn't have any edges, then just return it
        if (graph.get(start).isEmpty()) {
            return "Kruskal : " + start.getId() + " (no edges)";
        }

        // Only use the vertices that are connected to the start vertex
        Set<Vertex> component = PrimsAlgorithm.findConnectedVertices(graph, start);

        // Every undirected edge is stored twice (A->B and B->A), so keep only one copy of each
        List<Edge> candidateEdges = new ArrayList<>();
        for (Vertex vertex: component) {
            for (Edge edge: graph.get(vertex)) {
                if (edge.getVertex1().getId().compareTo(edge.getVertex2().getId()) < 0) {
                    candidateEdges.add(edge);
                }
            }
        }

        // Sort by weight; equal weights are sorted alphabetically so the result is always the same
        candidateEdges.sort(Comparator.comparingInt(Edge::getWeight)
                .thenComparing(edge -> edge.getVertex1().getId())
                .thenComparing(edge -> edge.getVertex2().getId()));

        // Union-Find: at the start every vertex is its own tree
        Map<Vertex, Vertex> parent = new HashMap<>();
        for (Vertex vertex: component) {
            parent.put(vertex, vertex);
        }

        int totalWeight = 0;

        for (Edge edge: candidateEdges) {
            Vertex root1 = find(parent, edge.getVertex1());
            Vertex root2 = find(parent, edge.getVertex2());

            // If both ends are already in the same tree, this edge would create a cycle -> skip it
            if (root1 != root2) {
                parent.put(root1, root2);  // join the two trees
                edgesOfMST.add(edge);
                totalWeight += edge.getWeight();

                if (!visitOrder.contains(edge.getVertex1())) visitOrder.add(edge.getVertex1());
                if (!visitOrder.contains(edge.getVertex2())) visitOrder.add(edge.getVertex2());

                // A spanning tree of n vertices has exactly n - 1 edges
                if (edgesOfMST.size() == component.size() - 1) break;
            }
        }

        return processEdgesOfMST(edgesOfMST, totalWeight);
    }

    /*
    Finds the root of the tree that contains the vertex.
    Path compression makes later look-ups faster.
     */
    private static Vertex find(Map<Vertex, Vertex> parent, Vertex vertex) {
        while (parent.get(vertex) != vertex) {
            parent.put(vertex, parent.get(parent.get(vertex)));
            vertex = parent.get(vertex);
        }
        return vertex;
    }

    private static String processEdgesOfMST(List<Edge> edges, int totalWeight) {
        StringBuilder output = new StringBuilder("Kruskal : ");

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            output.append(edge.getVertex1().getId())
                    .append("-")
                    .append(edge.getVertex2().getId())
                    .append("(").append(edge.getWeight()).append(")");
            if (i < edges.size() - 1) output.append(", ");
        }

        output.append(" | Total weight = ").append(totalWeight);
        return output.toString();
    }

    @Override
    public List<Vertex> getVisitOrder() {
        return new ArrayList<>(visitOrder);
    }

    @Override
    public List<Edge> getResultEdges() {
        return new ArrayList<>(edgesOfMST);
    }
}
