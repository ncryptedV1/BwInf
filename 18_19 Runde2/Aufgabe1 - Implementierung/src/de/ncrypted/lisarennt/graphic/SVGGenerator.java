package de.ncrypted.lisarennt.graphic;

import de.ncrypted.lisarennt.LisaRennt;
import de.ncrypted.lisarennt.dijkstra.PointNode;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author ncrypted
 */
public class SVGGenerator {

    public static File generateSVG(String fileName) {
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Render polygons etc into the SVG Graphics2D implementation.
        paint(svgGenerator);

        // Set viewBox
        Element root = svgGenerator.getRoot();
        root.setAttribute("viewBox", "0 0 1024 768");

        // Write SVG as a result into a file
        boolean useCSS = false; // use CSS style attributes
        String jarDir = null;
        try {
            jarDir = new File(
                    LisaRennt.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
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
        graphic.scale(1, -1);
        graphic.translate(0, -768);

        Font font = new Font("TimesRoman", Font.BOLD, 10);
        AffineTransform transform = new AffineTransform();
        transform.scale(1, -1);
        transform.translate(-5, 5);
        font = font.deriveFont(transform);
        graphic.setFont(font);

        // draw street
        graphic.setColor(Color.decode("#212121"));
        graphic.setStroke(new BasicStroke(3));
        graphic.drawLine(0, 0, 0, 768);

        // draw polygons with id
        for (PolygonAdapter polygon : LisaRennt.polygons) {
            graphic.setColor(Color.decode("#6b6b6b"));
            graphic.setStroke(new BasicStroke(2));
            graphic.fillPolygon(polygon);

            graphic.setColor(Color.decode("#212121"));
            graphic.setStroke(new BasicStroke(4));
            graphic.drawString(polygon.getName(), (int) polygon.getCenter().getX(), (int) polygon.getCenter().getY());
        }

        // draw Lisas house
        graphic.setColor(Color.decode("#f42121"));
        graphic.setStroke(new BasicStroke(1));
        int radius = 10;
        graphic.fillOval((int) LisaRennt.lisaPoint.getX() - radius, (int) LisaRennt.lisaPoint.getY() - radius,
                radius * 2,
                radius * 2);

        try {
            // draw recommended path
            graphic.setColor(Color.decode("#679067"));
            graphic.setStroke(new BasicStroke(4));
            List<PointNode> resultPoints = LisaRennt.resultPoints;
            Path2D path = new Path2D.Double();
            path.moveTo(resultPoints.get(0).getX(), resultPoints.get(0).getY());
            for (int i = 1; i < resultPoints.size(); i++) {
                PointNode current = resultPoints.get(i);
                path.lineTo(current.getX(), current.getY());
            }
            graphic.draw(path);
            radius = 5;
            // draw circles around path nodes
            for (PointNode resultPoint : LisaRennt.resultPoints) {
                graphic.setColor(Color.decode("#679067"));
                graphic.fillOval((int) resultPoint.getX() - radius, (int) resultPoint.getY() - radius,
                        radius * 2,
                        radius * 2);
                graphic.setColor(Color.decode("#212121"));
                graphic.drawString(resultPoint.getName(), (int) resultPoint.getX(), (int) resultPoint.getY());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
