package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Vertex extends JPanel implements Comparable<Vertex> {

    protected static final int SIZE = 50;
    protected static final Color VERTEX_COLOR = Color.white;
    protected static final Map<String, Vertex> vertices = new HashMap<>();
    private String id;
    private JLabel label;
    private int xLocation;
    private int yLocation;
    // NEW: colour used while an algorithm is being shown (null = normal white vertex)
    private Color highlightColor = null;

    public Vertex(int x, int y, String id) {
        this.id = id;
        this.xLocation = x;
        this.yLocation = y;
        vertices.put(this.id, this);
        this.label = new JLabel();
        this.label.setName("VertexLabel " + this.id);
        this.setLabel();

        this.setName("Vertex " + this.id);
        this.setBackground(MainFrame.BACKGROUND_COLOR);
        this.setOpaque(false);
        this.setLayout(null);
        this.setBounds(x, y, Vertex.SIZE, Vertex.SIZE);
    }

    public int getXLocation() {
        return this.xLocation;
    }

    public int getYLocation() {
        return this.yLocation;
    }

    public String getId() {
        return this.id;
    }

    public Color getHighlightColor() {
        return this.highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // NEW: use the highlight colour if the vertex is part of an algorithm result
        g2.setColor(highlightColor != null ? highlightColor : Vertex.VERTEX_COLOR);
        g2.fillOval(0, 0, Vertex.SIZE, Vertex.SIZE);
    }

    private void setLabel() {
        this.label.setText(this.id);
        this.label.setVerticalAlignment(JLabel.CENTER);
        this.label.setHorizontalAlignment(JLabel.CENTER);
        this.label.setSize(Vertex.SIZE, Vertex.SIZE);
        this.add(this.label);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Vertex)) return false;
        Vertex otherVertex = (Vertex) other;

        return Objects.equals(this.id, (otherVertex).getId()) &&
                this.xLocation == otherVertex.getXLocation() &&
                this.yLocation == otherVertex.getYLocation();
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + (id == null ? 0 : id.hashCode());
        result = 31 * result + xLocation;
        result = 31 * result + yLocation;

        return result;
    }

    @Override
    public int compareTo(Vertex otherVertex) {
        return String.valueOf(this.id).compareTo(otherVertex.getId());
    }

}
