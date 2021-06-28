package de.ncrypted.lisarennt.dijkstra;

import de.ncrypted.lisarennt.LisaRennt;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ncrypted
 */
public class Graph {

    private List<PointNode> nodes;
    private List<Edge> edges;

    public Graph(List<PointNode> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;

        for (Edge edge : edges) {
            edge.getEnd().getEdges().add(edge);
            edge.getStart().getEdges().add(edge);
        }
    }

    public void execute(PointNode start) {
        start.setDistanceFromSource(0);
        PointNode currentNode = start;

        while (currentNode != null) {
            // look for neighbours, which arent visited
            for (Edge edge : currentNode.getEdges()) {
                PointNode neighbour = edge.getNeighbourNode(currentNode);
                if (neighbour.isVisited()) {
                    continue;
                }
                double tentative = currentNode.getDistanceFromSource() + edge.getLength();

                if (tentative < neighbour.getDistanceFromSource()) {
                    neighbour.setDistanceFromSource(tentative);
                    neighbour.setPredecessor(currentNode);
                }
            }

            // all neighbours checked so node visited
            currentNode.setVisited(true);

            // next node must be with shortest distance
            currentNode = getNodeShortestDistanced(currentNode);
        }
    }

    private PointNode getNodeShortestDistanced(PointNode current) {
        PointNode nextNode = null;
        double nextNodeDist = Double.MAX_VALUE;

        for (PointNode node : nodes) {
            if (node.isVisited()) {
                continue;
            }
            double distance = node.getDistanceFromSource();
            if (distance < nextNodeDist) {
                nextNodeDist = distance;
                nextNode = node;
            }
        }
        LisaRennt.dijkstraWay.add(new Edge(current, nextNode));
        return nextNode;
    }

    private List<PointNode> getNeighbours(PointNode node) {
        List<PointNode> neighbours = new ArrayList<>();
        for (Edge edge : node.getEdges()) {
            neighbours.add(edge.getNeighbourNode(node));
        }
        return neighbours;
    }

    public List<PointNode> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}

