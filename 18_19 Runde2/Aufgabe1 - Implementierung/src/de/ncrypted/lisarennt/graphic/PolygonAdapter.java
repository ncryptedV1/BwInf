package de.ncrypted.lisarennt.graphic;

import de.ncrypted.lisarennt.Utils;
import de.ncrypted.lisarennt.dijkstra.PointNode;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ncrypted
 */
public class PolygonAdapter extends java.awt.Polygon {

    private String name;
    private List<PointNode> corners;
    private List<Line2D> lines = new ArrayList<>();

    public PolygonAdapter(String name, List<PointNode> corners) {
        if (corners.size() < 3) {
            throw new IllegalArgumentException("You need at least 3 Corners");
        }
        this.name = name;
        this.corners = corners;

        for (PointNode corner : corners) {
            addPoint((int) corner.getX(), (int) corner.getY());
        }
        for (int i = 0; i < corners.size() - 1; i++) {
            lines.add(new Line2D.Double(xpoints[i], ypoints[i], xpoints[i + 1], ypoints[i + 1]));
        }
        lines.add(new Line2D.Double(xpoints[corners.size() - 1], ypoints[corners.size() - 1], xpoints[0], ypoints[0]));
    }

    public String getName() {
        return name;
    }

    public List<PointNode> getCorners() {
        return corners;
    }

    public List<Line2D> getLines() {
        return lines;
    }

    public PointNode getCenter() {
        double sumX = 0;
        double sumY = 0;
        for (PointNode corner : corners) {
            sumX += corner.getX();
            sumY += corner.getY();
        }
        return new PointNode(null, sumX / corners.size(), sumY / corners.size());
    }

    public String toString() {
        String cornersStr = "";
        for (int i = 0; i < corners.size(); i++) {
            cornersStr += cornerToString(i);
        }
        return name + ":" + cornersStr;
    }

    public String cornerToString(int id) {
        return " Ecke" + (id + 1) + "=" + Utils.round(corners.get(id).getX(), 2) + "|" +
                Utils.round(corners.get(id).getY(), 2);
    }
}
