package visualizer;

import algorithms.*;
public enum Algorithm {

    BFS(new BFSAlgorithm(), "Breadth-First Search"),
    DFS(new DFSAlgorithm(), "Depth-First Search"),
    DIJKSTRAS(new DijkstrasAlgorithm(), "Dijkstra's Algorithm"),
    PRIMS(new PrimsAlgorithm(), "Prim's Algorithm"),
    KRUSKALS(new KruskalsAlgorithm(), "Kruskal's Algorithm");  // NEW

    private final GraphAlgorithm algorithmInstance;
    private final String displayName;  // NEW: name shown in the status bar

    Algorithm(GraphAlgorithm algorithmInstance, String displayName) {
        this.algorithmInstance = algorithmInstance;
        this.displayName = displayName;
    }

    public GraphAlgorithm getAlgorithmInstance() {
        return algorithmInstance;
    }

    public String getDisplayName() {
        return displayName;
    }

}
