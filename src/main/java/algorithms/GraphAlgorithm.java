package algorithms;

import visualizer.Edge;
import visualizer.Vertex;
import java.util.*;

public interface GraphAlgorithm {
    String run(Map<Vertex, List<Edge>> graph, Vertex start);

    default String processVertex(Vertex vertex) {
        return vertex.getId() + " -> ";
    }

    /*
    NEW: The vertices in the order the algorithm reached them during the last run.
    The GUI uses this list to animate the algorithm step by step.
     */
    default List<Vertex> getVisitOrder() {
        return new ArrayList<>();
    }

    /*
    NEW: The edges that make up the result of the last run:
    the traversal tree for BFS/DFS, the shortest-path tree for Dijkstra, and the MST for Prim/Kruskal.
    The GUI colours these edges on the screen.
     */
    default List<Edge> getResultEdges() {
        return new ArrayList<>();
    }
}
