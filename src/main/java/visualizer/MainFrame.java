package visualizer;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    protected static final int WIDTH = 800;
    protected static final int HEIGHT = 600;
    protected static final Color BACKGROUND_COLOR = Color.black;
    private JLabel modeLabel;
    private static final JLabel algorithmDisplayLabel;
    private Graph graphPanel;
    protected static Mode mode = Mode.ADD_A_VERTEX;
    protected static Algorithm algorithm = null;

    static {
        algorithmDisplayLabel = new JLabel();
        algorithmDisplayLabel.setName("Display");
        algorithmDisplayLabel.setText("Please choose a starting vertex");
        algorithmDisplayLabel.setForeground(MainFrame.BACKGROUND_COLOR);
        algorithmDisplayLabel.setBackground(Color.white);
        algorithmDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        algorithmDisplayLabel.setVerticalAlignment(SwingConstants.CENTER);
        algorithmDisplayLabel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        algorithmDisplayLabel.setVisible(false);
    }


    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
        setLayout(new BorderLayout());
        setResizable(false);

        setModeJLabel();
        this.add(algorithmDisplayLabel, BorderLayout.SOUTH);
        setJMenu();

        add(this.graphPanel = new Graph(), BorderLayout.CENTER);

        setVisible(true);

    }

    private void setJMenu() {
        // Creating the menuBar
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // Creating the menu "File"
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Creating the menu "Mode"
        JMenu modeMenu = new JMenu("Mode");
        menuBar.add(modeMenu);

        // Creating the menu "Algorithms"
        JMenu algorithmsMenu = new JMenu("Algorithms");
        menuBar.add(algorithmsMenu);

        // Creating the five menu items for "Mode"
        JMenuItem addAVertex = new JMenuItem("Add a Vertex");
        addAVertex.setName("Add a Vertex");
        JMenuItem addAnEdge = new JMenuItem("Add an Edge");
        addAnEdge.setName("Add an Edge");
        JMenuItem none = new JMenuItem("None");
        none.setName("None");
        JMenuItem removeAVertex = new JMenuItem("Remove a Vertex");
        removeAVertex.setName("Remove a Vertex");
        JMenuItem removeAnEdge = new JMenuItem("Remove an Edge");
        removeAnEdge.setName("Remove an Edge");

        modeMenu.add(addAVertex);
        modeMenu.add(addAnEdge);
        modeMenu.add(removeAVertex);
        modeMenu.add(removeAnEdge);
        modeMenu.add(none);

        // Creating the two menu items for "File"
        JMenuItem newReset = new JMenuItem("New");
        newReset.setName("New");
        JMenuItem exit = new JMenuItem("Exit");
        exit.setName("Exit");

        fileMenu.add(newReset);
        fileMenu.add(exit);

        // Creating the menu items for "Algorithms"
        JMenuItem DFS = new JMenuItem("Depth-First Search");
        DFS.setName("Depth-First Search");
        JMenuItem BFS = new JMenuItem("Breadth-First Search");
        BFS.setName("Breadth-First Search");
        JMenuItem Dijkstras = new JMenuItem("Dijkstra's Algorithm");
        Dijkstras.setName("Dijkstra's Algorithm");
        JMenuItem Prims = new JMenuItem("Prim's Algorithm");
        Prims.setName("Prim's Algorithm");
        JMenuItem Kruskals = new JMenuItem("Kruskal's Algorithm");  // NEW
        Kruskals.setName("Kruskal's Algorithm");

        algorithmsMenu.add(DFS);
        algorithmsMenu.add(BFS);
        algorithmsMenu.add(Dijkstras);
        algorithmsMenu.add(Prims);
        algorithmsMenu.add(Kruskals);

        // Add event listeners to the five menu items of "Mode"
        // (REFACTOR: the repeated code is now in the switchMode() method)
        addAVertex.addActionListener(e -> switchMode(Mode.ADD_A_VERTEX));
        addAnEdge.addActionListener(e -> switchMode(Mode.ADD_AN_EDGE));
        none.addActionListener(e -> switchMode(Mode.NONE));
        removeAVertex.addActionListener(e -> switchMode(Mode.REMOVE_A_VERTEX));
        removeAnEdge.addActionListener(e -> switchMode(Mode.REMOVE_AN_EDGE));

        // Add event listeners to the two menu items of "File"
        newReset.addActionListener(e -> {
            // Stop any running animation of the old graph
            this.graphPanel.resetHighlights();

            this.remove(this.graphPanel);

            // Clear all the static containers
            Edge.edges.clear();
            Vertex.vertices.clear();
            Graph.edgeVertices.clear();
            Graph.availableEdges.clear();

            this.add(this.graphPanel = new Graph(), BorderLayout.CENTER);
            this.revalidate();
            this.repaint();

            switchMode(Mode.ADD_A_VERTEX);
        });
        exit.addActionListener(e -> {
            this.dispose();
        });

        // Add event listeners to the menu items of "Algorithms"
        DFS.addActionListener(e -> selectAlgorithm(Algorithm.DFS));
        BFS.addActionListener(e -> selectAlgorithm(Algorithm.BFS));
        Dijkstras.addActionListener(e -> selectAlgorithm(Algorithm.DIJKSTRAS));
        Prims.addActionListener(e -> selectAlgorithm(Algorithm.PRIMS));
        Kruskals.addActionListener(e -> selectAlgorithm(Algorithm.KRUSKALS));
    }

    /*
    NEW (refactor): all mode menu items used to repeat the same 6 lines of code.
     */
    private void switchMode(Mode newMode) {
        // Change the mode
        mode = newMode;
        // Change text for label
        changeTextForModeLabel("Current Mode -> " + mode.getDescription());
        // Remove response to previous vertex clicks
        Graph.edgeVertices.clear();
        // Remove the colours of a previous algorithm run or edge selection
        graphPanel.resetHighlights();
        // Switch algorithmDisplayLabel visibility to false and text
        algorithmDisplayLabel.setVisible(false);
        algorithmDisplayLabel.setText("Please choose a starting vertex");
    }

    /*
    NEW (refactor): shared code of the algorithm menu items.
     */
    private void selectAlgorithm(Algorithm chosenAlgorithm) {
        switchMode(Mode.NONE);
        // Change the algorithm
        algorithm = chosenAlgorithm;
        // Show which algorithm is selected and switch algorithmDisplayLabel visibility to true
        algorithmDisplayLabel.setText(chosenAlgorithm.getDisplayName() + "  -  please choose a starting vertex");
        algorithmDisplayLabel.setVisible(true);
    }

    private void changeTextForModeLabel(String newText) {
        this.modeLabel.setText(newText);
        modeLabel.setSize(modeLabel.getPreferredSize());
        this.modeLabel.setLocation(MainFrame.WIDTH - modeLabel.getWidth() - 20, 0);
    }

    private void setModeJLabel() {
        modeLabel = new JLabel();
        this.add(modeLabel, BorderLayout.NORTH);
        modeLabel.setName("Mode");
        modeLabel.setText("Current Mode -> Add a Vertex");
        modeLabel.setOpaque(true);
        modeLabel.setForeground(Vertex.VERTEX_COLOR);
        modeLabel.setBackground(MainFrame.BACKGROUND_COLOR);
        modeLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
        modeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    }

    public static JLabel getAlgorithmDisplayLabel() {
        return algorithmDisplayLabel;
    }

}
