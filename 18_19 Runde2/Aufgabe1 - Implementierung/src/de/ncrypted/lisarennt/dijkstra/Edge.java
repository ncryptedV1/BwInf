package de.ncrypted.lisarennt.dijkstra;


/**
 * @author ncrypted
 */
public class Edge {

    private PointNode start;
    private PointNode end;

    public Edge(PointNode start, PointNode end) {
        this.start = start;
        this.end = end;
    }

    public PointNode getStart() {
        return start;
    }

    public PointNode getEnd() {
        return end;
    }

    public double getLength() {
        double deltaX = Math.abs(start.getX() - end.getX());
        double deltaY = Math.abs(start.getY() - end.getY());
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    // determines the neighbouring node of a supplied node, based on the two nodes connected by this edge
    public PointNode getNeighbourNode(PointNode node) {
        if (this.start.equals(node)) {
            return this.end;
        } else {
            return this.start;
        }
    }
}
