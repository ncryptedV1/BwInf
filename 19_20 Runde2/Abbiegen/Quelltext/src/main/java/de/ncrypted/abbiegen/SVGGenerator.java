package de.ncrypted.abbiegen;

import de.ncrypted.abbiegen.geometry.PointInt;
import javafx.util.Pair;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ncrypted
 */
public class SVGGenerator {

    public static int maxX, maxY;

    public static File generateSVG(String fileName) {
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Render polygons etc into the SVG Graphics2D implementation.
        paint(svgGenerator);

        // Set viewBox
        Element root = svgGenerator.getRoot();
        root.setAttribute("viewBox", "-20 -20 " + (maxX+40) + " " + (maxY+40));
        root.setAttribute("width", maxX + "");
        root.setAttribute("height", maxY + "");

        // Write SVG as a result into a file
        boolean useCSS = false; // use CSS style attributes
        String jarDir = null;
        try {
            jarDir = new File(
                    Abbiegen.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
                    .getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File file = new File(jarDir + File.separator + fileName);
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
            svgGenerator.stream(root, writer, useCSS, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static void paint(SVGGraphics2D graphic) {
        graphic.translate(0, maxY);
        graphic.scale(1, -1);

        Font font = new Font("TimesRoman", Font.BOLD, 16);
        AffineTransform transform = new AffineTransform();
        transform.scale(1, -1);
        transform.translate(-5, 5);
        font = font.deriveFont(transform);
        graphic.setFont(font);

        // draw nodes
        graphic.setColor(Color.BLACK);
        graphic.setStroke(new BasicStroke(3));
        for (int i = 0; i < Abbiegen.nodes.size(); i++) {
            PointInt node = Abbiegen.nodes.get(i);
            drawCircle(graphic, node, 3);
            drawString(graphic, node.getX(), node.getY(), i + "");
        }

        // draw start and end node in different color
        PointInt startPoint = Abbiegen.nodes.get(Abbiegen.startNode);
        PointInt endPoint = Abbiegen.nodes.get(Abbiegen.endNode);
        graphic.setColor(Color.RED);
        drawCircle(graphic, startPoint, 3);
        graphic.setColor(Color.GREEN);
        drawCircle(graphic, endPoint, 3);

        // draw edges
        graphic.setColor(Color.BLACK);
        graphic.setStroke(new BasicStroke(1));
        for (int i = 0; i < Abbiegen.nodes.size(); i++) {
            PointInt u = Abbiegen.nodes.get(i);
            for (Pair<Integer, Double> street : Abbiegen.streets.get(i)) {
                PointInt v = Abbiegen.nodes.get(street.getKey());
                drawLine(graphic, u, v, Color.BLACK);
            }
        }

        // draw shortest path
        List<Integer> shortestPath = Abbiegen.shortestPath;
        for (int i = 1; i < shortestPath.size(); i++) {
            PointInt u = Abbiegen.nodes.get(shortestPath.get(i - 1));
            PointInt v = Abbiegen.nodes.get(shortestPath.get(i));
            drawLine(graphic, u, v, Color.RED);
        }

        // draw best possible path with least turns
        ArrayList<Integer> path = Abbiegen.turnPath;
        for (int i = 1; i < path.size(); i++) {
            PointInt u = Abbiegen.nodes.get(path.get(i-1));
            PointInt v = Abbiegen.nodes.get(path.get(i));
            drawLine(graphic, u, v, Color.BLUE, 2);
        }
    }

    private static void drawString(SVGGraphics2D graphics, float x, float y, String str) {
        graphics.drawString(str, x * 100+12, y * 100 +5);
    }

    private static void drawLine(SVGGraphics2D graphics, PointInt u, PointInt v, Color color) {
        drawLine(graphics, u, v, color, 0);
    }

    private static void drawLine(SVGGraphics2D graphics, PointInt u, PointInt v, Color color, int offset) {
        Color prevColor = graphics.getColor();
        graphics.setColor(color);
        graphics.drawLine(u.getX() * 100, u.getY() * 100 +offset, v.getX() * 100, v.getY() * 100 + offset);
        graphics.setColor(prevColor);
    }

    private static void drawCircle(SVGGraphics2D graphics, PointInt center, int radius) {
        graphics.drawOval(center.getX() * 100, center.getY() * 100, radius * 2, radius * 2);
    }

}
