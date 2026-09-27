package visualizer;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

import algorithms.*;

public class Graph extends JPanel implements MouseListener {

    protected static List<Vertex> edgeVertices = new ArrayList<>();
    protected static List<List<String>> availableEdges = new ArrayList<>();

    // NEW: colours and speed used for the step-by-step animation
    protected static final Color START_COLOR = new Color(46, 204, 113);     // green  = start vertex
    protected static final Color VISITED_COLOR = new Color(255, 165, 0);    // orange = visited vertex
    protected static final Color SELECTED_COLOR = new Color(52, 152, 219);  // blue   = first vertex of a new edge
    private static final int ANIMATION_DELAY_MS = 700;

    // NEW: the timer that is currently animating an algorithm (null if nothing is running)
    private Timer animationTimer;

    public Graph() {
        setName("Graph");
        setBackground(MainFrame.BACKGROUND_COLOR);
        setLayout(null);
        setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
        setLocation(0, 0);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (MainFrame.mode == Mode.ADD_A_VERTEX) {
            addVertexAt(e.getX(), e.getY());
        } else if (MainFrame.mode == Mode.ADD_AN_EDGE) {
            selectVertexForEdge(e.getX(), e.getY());
        } else if (MainFrame.mode == Mode.REMOVE_A_VERTEX) {
            // Check if a vertex was clicked
            Vertex vertex = clickedOnVertex(e.getX(), e.getY());

            if (vertex != null) {
                // Remove from static container vertices
                String id = vertex.getId();
                Vertex.vertices.remove(id);

                List<Edge> edges = new ArrayList<>();

                // Remove edges associated with this vertex
                for (Edge edge: Edge.edges) {
                    Vertex vertex1 = edge.getVertex1();
                    Vertex vertex2 = edge.getVertex2();
                    if (vertex.equals(vertex1) || vertex.equals(vertex2)) {
                        // Remove the edge's label from this Graph, if it exists
                        if (edge.getLabel() != null) this.remove(edge.getLabel());
                        // Remove the edge from this Graph
                        this.remove(edge);
                        // Remove the couple of vertices from the availableEdges static container
                        removeEdgeFromStaticList(edge);
                    } else {  // This creates a new edges list that doesn't contain the edges associated with Vertex
                        edges.add(edge);
                    }
                }

                // Update the edges static container of Edge class
                Edge.edges = edges;

                //Remove the vertex from this Graph
                this.remove(vertex);

                this.repaint();

            }
        } else if (MainFrame.mode == Mode.REMOVE_AN_EDGE) {
                Edge edge = clickedOnEdge(e.getX(), e.getY());

                if (edge != null) {

                    // Remove the edge from the edges static container of Edge class
                    // as well as itself and its label from the Graph
                    List<Edge> edges = new ArrayList<>();
                    List<Edge> edgesToBeExcluded = new ArrayList<>();

                    for (Edge otherEdge: Edge.edges) {
                        if (edge.equals(otherEdge)) {
                            this.remove(otherEdge);
                            if (otherEdge.getLabel() != null) {
                                this.remove(otherEdge.getLabel());
                            }
                            edgesToBeExcluded.add(otherEdge);
                        } else {
                            edges.add(otherEdge);
                        }
                    }

                    // Update the edges static container of Edge class
                    Edge.edges = edges;

                    // Remove the excluded edges from the availableEdges static container
                    for (Edge excludedEdge: edgesToBeExcluded) removeEdgeFromStaticList(excludedEdge);

                    this.repaint();
                }
        } else if (MainFrame.mode == Mode.NONE && MainFrame.getAlgorithmDisplayLabel().isVisible()) {
            // Check if a vertex was clicked
            Vertex vertex = clickedOnVertex(e.getX(), e.getY());

            if (vertex != null) {
                runAlgorithm(vertex);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /*
    NEW (validation): Adds a vertex and explains to the user what went wrong instead of silently ignoring the click.
     */
    private void addVertexAt(int x, int y) {
        if (!validPlacementForVertex(x, y)) {
            showError("You cannot place a vertex here.\n" +
                    "It is too close to another vertex or on top of an edge.");
            return;
        }

        while (true) {
            String input = JOptionPane.showInputDialog(this, "Enter the Vertex ID (Should be 1 char):",
                    "Vertex", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return;  // the user pressed Cancel

            input = input.trim();
            if (input.length() != 1) {
                showError("The vertex ID must be exactly one character, for example A.");
            } else if (!validVertexID(input)) {
                showError("A vertex with the ID '" + input + "' already exists.\nPlease choose another ID.");
            } else {
                int xValue = x - Vertex.SIZE / 2;
                int yValue = y - Vertex.SIZE / 2;
                createVertex(xValue, yValue, input);
                return;
            }
        }
    }

    /*
    NEW (validation): Handles the two clicks needed to create an edge.
    The first selected vertex is shown in blue, and invalid edges get an error message.
     */
    private void selectVertexForEdge(int x, int y) {
        // Check if a vertex was clicked
        Vertex vertex = clickedOnVertex(x, y);
        if (vertex == null) return;

        edgeVertices.add(vertex);

        // First click: remember the vertex and colour it
        if (edgeVertices.size() == 1) {
            vertex.setHighlightColor(SELECTED_COLOR);
            return;
        }

        // Second click: we now have two vertices
        Vertex vertex1 = edgeVertices.get(0);
        Vertex vertex2 = edgeVertices.get(1);
        edgeVertices.clear();
        vertex1.setHighlightColor(null);

        if (vertex1.equals(vertex2)) {
            showError("An edge cannot connect a vertex to itself.\nPlease select two different vertices.");
            return;
        }

        if (edgeExists(vertex1.getId(), vertex2.getId())) {
            showError("An edge between " + vertex1.getId() + " and " + vertex2.getId() + " already exists.");
            return;
        }

        // Only remember the edge if the user actually entered a weight (BUG FIX: previously the pair was
        // stored even when the user pressed Cancel, so that edge could never be created again)
        if (drawEdge(vertex1, vertex2)) {
            List<String> newIdCouple = new ArrayList<>();
            newIdCouple.add(vertex1.getId());
            newIdCouple.add(vertex2.getId());
            availableEdges.add(newIdCouple);
        }
    }

    /*
    NEW (visualisation): Runs the selected algorithm and then shows the result step by step.
    Every tick of the timer colours the next vertex; an edge of the result is coloured as soon as
    both of its vertices are coloured. At the end the text result is shown in the status bar.
     */
    private void runAlgorithm(Vertex start) {
        resetHighlights();

        GraphAlgorithm algorithmInstance = MainFrame.algorithm.getAlgorithmInstance();
        AlgorithmSetter algorithmSetter = new AlgorithmSetter();
        algorithmSetter.setAlgorithm(algorithmInstance);

        // Create the graph data structure
        Map<Vertex, List<Edge>> graph = createGraphDataStructure();

        // Run the algorithm
        String result = algorithmSetter.execute(graph, start);
        List<Vertex> visitOrder = algorithmInstance.getVisitOrder();
        List<Edge> resultEdges = algorithmInstance.getResultEdges();

        String name = MainFrame.algorithm.getDisplayName();
        JLabel display = MainFrame.getAlgorithmDisplayLabel();

        int[] step = {0};  // array so that the lambda below can change it

        animationTimer = new Timer(ANIMATION_DELAY_MS, event -> {
            if (step[0] < visitOrder.size()) {
                Vertex current = visitOrder.get(step[0]);
                current.setHighlightColor(step[0] == 0 ? START_COLOR : VISITED_COLOR);

                // Colour every result edge whose two vertices are now both coloured
                for (Edge edge: resultEdges) {
                    if (edge.getVertex1().getHighlightColor() != null
                            && edge.getVertex2().getHighlightColor() != null) {
                        highlightEdge(edge);
                    }
                }

                step[0]++;
                display.setText(name + "  -  step " + step[0] + "/" + visitOrder.size()
                        + ": reached " + current.getId());
            } else {
                // Animation finished: make sure all result edges are coloured and show the result
                for (Edge edge: resultEdges) highlightEdge(edge);
                display.setText(result);
                ((Timer) event.getSource()).stop();
            }
            repaint();
        });
        animationTimer.setInitialDelay(0);
        animationTimer.start();
    }

    /*
    NEW: Stops a running animation and puts all vertices and edges back to their normal colour.
     */
    public void resetHighlights() {
        if (animationTimer != null) animationTimer.stop();
        for (Vertex vertex: Vertex.vertices.values()) vertex.setHighlightColor(null);
        for (Edge edge: Edge.edges) edge.setHighlighted(false);
        repaint();
    }

    /*
    Each connection is stored as two Edge objects (A->B and B->A) drawn on top of each other,
    so both of them are highlighted. Edge.equals() ignores the direction.
     */
    private static void highlightEdge(Edge edge) {
        for (Edge otherEdge: Edge.edges) {
            if (otherEdge.equals(edge)) otherEdge.setHighlighted(true);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid action", JOptionPane.ERROR_MESSAGE);
    }

    private static boolean edgeExists(String id1, String id2) {
        for (List<String> verticesOfAnEdge: availableEdges) {
            if (verticesOfAnEdge.contains(id1) && verticesOfAnEdge.contains(id2)) return true;
        }
        return false;
    }

    private static Vertex clickedOnVertex(int x, int y) {

        for (var entry: Vertex.vertices.entrySet()) {
            Vertex vertex = entry.getValue();
            int xLocation = vertex.getXLocation();
            int yLocation = vertex.getYLocation();

            if (x >= xLocation && x <= xLocation + Vertex.SIZE
                    && y >= yLocation && y <= yLocation + Vertex.SIZE) {
                return vertex;
            }
        }

        return null;
    }

    private static Edge clickedOnEdge(int x, int y) {

        for (Edge edge: Edge.edges) {
            // Get the coordinates of the two points that are forming the edge
            int x1 = edge.getX();
            int y1 = edge.getY() + (edge.getTopEqualsLeft() ? 0 : edge.getHeight());
            int x2 = edge.getX() + edge.getWidth();
            int y2 = edge.getY() + (edge.getTopEqualsLeft() ? edge.getHeight() : 0);

            // Determine the distance from the point of coordinates (x, y) and the line
            double dist = java.awt.geom.Line2D.ptLineDistSq((double) x1, (double) y1,
                    (double) x2, (double) y2,
                    (double) x, (double) y);

            if (dist < 5) return edge;

        }

        return null;

    }

    private static boolean validPlacementForVertex(int x, int y) {

        // Check if an edge was clicked
        Edge edge = clickedOnEdge(x, y);
        if (edge != null) return false;

        for (Vertex vertex: Vertex.vertices.values()) {
            int xLocation = vertex.getXLocation();
            int yLocation = vertex.getYLocation();

            if (x >= xLocation - Vertex.SIZE / 2 && x <= xLocation + Vertex.SIZE + Vertex.SIZE / 2
                    && y >= yLocation - Vertex.SIZE / 2 && y <= yLocation + Vertex.SIZE + Vertex.SIZE / 2) {
                return false;
            }
        }

        return true;
    }

    private static boolean validVertexID(String userInput) {

        for (String id: Vertex.vertices.keySet()) {
            if (userInput.equals(id)) return false;
        }

        return true;
    }

    private void createVertex(int xValue, int yValue, String id) {
        JPanel vertex = new Vertex(xValue, yValue, id);
        this.add(vertex);
        vertex.repaint();
    }

    /*
    Asks for the weight and draws the edge.
    NEW (validation): returns true if the edge was drawn, false if the user pressed Cancel,
    and shows a clear error message for text, negative numbers and numbers that are too large.
     */
    private boolean drawEdge(Vertex vertex1, Vertex vertex2) {
        while (true) {
            String input = JOptionPane.showInputDialog(this,
                    "Enter the weight of the edge " + vertex1.getId() + " - " + vertex2.getId()
                            + "\n(a whole number, 0 or greater):",
                    "Input", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return false;  // the user pressed Cancel

            input = input.trim();

            if (!input.matches("-?\\d+")) {
                showError("The weight must be a whole number, for example 5.");
                continue;
            }
            if (input.startsWith("-")) {
                showError("Negative weights are not allowed.\n" +
                        "Dijkstra's algorithm only gives correct results when all weights are 0 or greater.");
                continue;
            }

            int weight;
            try {
                weight = Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                showError("The weight is too large. Please enter a smaller number.");
                continue;
            }

            Edge edge1 = new Edge(vertex1, vertex2, weight);
            Edge edge2 = new Edge(vertex2, vertex1, weight);

            this.add(edge1);
            this.add(edge2);
            this.add(edge1.getLabel());

            this.repaint();
            return true;
        }
    }

    private static void removeEdgeFromStaticList(Edge edge) {
        String id1 = edge.getVertex1().getId();
        String id2 = edge.getVertex2().getId();

        List<List<String>> newEdgesList = new ArrayList<>();

        for (List<String> otherEdge: availableEdges) {
            if ((!(otherEdge.contains(id1) && otherEdge.contains(id2)))) {
                newEdgesList.add(otherEdge);
            }
        }

        availableEdges = newEdgesList;
    }

    private static Map<Vertex, List<Edge>> createGraphDataStructure() {
        Map<Vertex, List<Edge>> output = new HashMap<>();

        // Place all available vertices as keys with values as empty lists
        for (Vertex vertex: Vertex.vertices.values()) {
            output.put(vertex, new ArrayList<>());
        }

        // Populate the lists of each Vertex with edges that have them as source Vertex
        for (Edge edge: Edge.edges) {
            output.get(edge.getVertex1()).add(edge);
        }

        return output;
    }

}
