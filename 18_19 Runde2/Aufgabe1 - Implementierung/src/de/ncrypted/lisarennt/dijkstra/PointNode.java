package de.ncrypted.lisarennt.dijkstra;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ncrypted
 */
public class PointNode extends Point2D.Double {

    private String name;
    private double distanceFromSource = java.lang.Double.MAX_VALUE;
    private PointNode predecessor;
    private boolean visited;
    private List<Edge> edges = new ArrayList<>();

    public PointNode(String name, double x, double y) {
        super(x, y);
        this.name = name;
    }

    public double getDistanceFromSource() {
        return distanceFromSource;
    }

    public void setDistanceFromSource(double distanceFromSource) {
        this.distanceFromSource = distanceFromSource;
    }

    public PointNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(PointNode predecessor) {
        this.predecessor = predecessor;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public String getName() {
        return name;
    }

    public LinkedList<PointNode> getPath(PointNode target) {
        LinkedList<PointNode> path = new LinkedList<>();
        PointNode step = target;
        // check if target is starting point
        if(target == this) {
            path.add(this);
            return path;
        }
        // check if a path exists
        if (step.getPredecessor() == null) {
            return null;
        }
        path.add(step);
        while (step.getPredecessor() != null) {
            step = step.getPredecessor();
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointNode other = (PointNode) obj;
        if(getName() == null || other.getName() == null) {
            return false;
        }
        if (!getName().equals(other.getName()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getName() + ": " + x + "|" + y;
    }
}