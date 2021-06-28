package de.ncrypted.dreiecke;

import de.ncrypted.dreiecke.geometry.Angle;
import de.ncrypted.dreiecke.geometry.Triangle;
import de.ncrypted.dreiecke.geometry.TrianglePath;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.URISyntaxException;

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
        // original x 820
        root.setAttribute("viewBox", "-200 0 1500 620");

        // Write SVG as a result into a file
        boolean useCSS = false; // use CSS style attributes
        String jarDir = null;
        try {
            jarDir = new File(
                    Dreiecke.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
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
        graphic.translate(0, -600);

        Font font = new Font("TimesRoman", Font.BOLD, 10);
        AffineTransform transform = new AffineTransform();
        transform.scale(1, -1);
        transform.translate(-5, 5);
        font = font.deriveFont(transform);
        graphic.setFont(font);

        // draw ground
        graphic.setColor(Color.decode("#000000"));
        graphic.drawLine(0, 0, 1300, 0);

        // draw TrianglePairs
        double xShift = 0;
        Point2D safePoint = null;
        for (Triangle.AnglePair pair : Dreiecke.algorithmPairs) {
            Triangle triangle1 = pair.getAngle1().getTriangle();
            Triangle triangle2 = pair.getAngle2().getTriangle();
            Angle angle1 = pair.getAngle1();
            Angle angle2 = pair.getAngle2();

            // calculate side values for first triangle
            double shortSideLength1 = triangle1.getSideFromType(Triangle.getShortAdjacentSide(angle1));
            double longSideLength1 = triangle1.getSideFromType(triangle1.getLongestSide());
            double angleValue1 = angle1.getDegrees();
            double xOff1 = Math.cos(Math.toRadians(angleValue1)) * longSideLength1;
            double yOff1 = Math.sin(Math.toRadians(angleValue1)) * longSideLength1;

            // calculate side values for second triangle
            double longSideLength2 = triangle2.getSideFromType(triangle2.getLongestSide());
            double xOff2 = Math.cos(Math.toRadians(angleValue1)) * longSideLength2;
            double yOff2 = Math.sin(Math.toRadians(angleValue1)) * longSideLength2;
            double shortSideLength2 = triangle2.getSideFromType(Triangle.getShortAdjacentSide(angle2));
            double angleValue2 = 180 - angleValue1 - angle2.getDegrees();
            double xOff2_1 = Math.cos(Math.toRadians(angleValue2)) * shortSideLength2;
            double yOff2_1 = Math.sin(Math.toRadians(angleValue2)) * shortSideLength2;

            if (safePoint != null) {
                double newStart = -1;
                // move the pair a bit further right if the first triangle would intersect with the previous pair
                Point2D absoluteSafePoint;
                if (safePoint.getY() > yOff1) {
                    absoluteSafePoint = new Point2D.Double(xShift + safePoint.getX(), yOff1);
                } else {
                    absoluteSafePoint = new Point2D.Double(xShift + safePoint.getX(), safePoint.getY());
                }
                double safeStart = calcXWantCutY(absoluteSafePoint.getY(), xShift, safePoint.getY() / safePoint.getX(),
                        yOff1 / (shortSideLength1 - xOff1));
                if (safeStart > xShift) {
                    newStart = safeStart;
                }
                // move the pair a bit further right if the second triangle would intersect with the previous pair
                if (safePoint.getY() > yOff2) {
                    absoluteSafePoint = new Point2D.Double(xShift + safePoint.getX(), yOff2);
                } else {
                    absoluteSafePoint = new Point2D.Double(xShift + safePoint.getX(), safePoint.getY());
                }
                safeStart = calcXWantCutY(absoluteSafePoint.getY(), xShift, safePoint.getY() / safePoint.getX(),
                        yOff2 / -xOff2) - shortSideLength1;
                if (safeStart > xShift && safeStart > newStart) {
                    newStart = safeStart;
                }

                if (newStart > 0) {
                    xShift = newStart;
                }
            }

            Path2D path1 = new Path2D.Double();
            // first triangles starting point
            path1.moveTo(xShift, 0);
            // first triangles shortest side
            path1.lineTo(xShift + shortSideLength1, 0);
            xShift += shortSideLength1;
            // first triangles last point (longest side)
            path1.lineTo(xShift - xOff1, yOff1);
            path1.closePath();
            graphic.setColor(Color.decode("#6b6b6b"));
            graphic.setStroke(new BasicStroke(2));
            graphic.draw(path1);
            TrianglePath trianglePath1 = new TrianglePath(triangle1.getName(), path1);
            Dreiecke.finalTriangles.add(trianglePath1);
            // draw triangle1 name
            graphic.setColor(Color.decode("#212121"));
            graphic.setStroke(new BasicStroke(4));
            graphic.drawString(trianglePath1.getName(), (int) trianglePath1.getCenter().getX(),
                    (int) trianglePath1.getCenter().getY());

            // check whether last pair is the spare triangle
            if (triangle2 == triangle1) {
                break;
            }

            Path2D path2 = new Path2D.Double();
            // second triangles bottom point
            path2.moveTo(xShift, 0);
            // second triangles side, that's aligning with the hypotenuse of the first one
            path2.lineTo(xShift - xOff2, yOff2);
            // second triangles short angle side
            path2.lineTo(xShift + xOff2_1, yOff2_1);
            path2.closePath();
            graphic.setColor(Color.decode("#6b6b6b"));
            graphic.setStroke(new BasicStroke(2));
            graphic.draw(path2);
            TrianglePath trianglePath2 = new TrianglePath(triangle2.getName(), path2);
            Dreiecke.finalTriangles.add(trianglePath2);

            safePoint = new Point2D.Double(xOff2_1, yOff2_1);
            // draw triangle2 name
            graphic.setColor(Color.decode("#212121"));
            graphic.setStroke(new BasicStroke(4));
            graphic.drawString(trianglePath2.getName(), (int) trianglePath2.getCenter().getX(),
                    (int) trianglePath2.getCenter().getY());
        }
    }

    private static double calcXWantCutY(double cutY, double xZero1, double m1, double m2) {
        // safe function: f(x)=m1*x+n1
        // calculate n1: 0=m1*xZero1+n1 -> n1=-m1*xZero1
        double n1 = -m1 * xZero1;
        // calc cutX: f(x)=cutY -> cutY=m1*cutX+n1 -> cutX=(cutY-n1)/m1
        double cutX = (cutY - n1) / m1;

        // new function: g(x)=m2*x+n2
        // calc n2 for which func runs through the cut point: cutY=m2*cutX+n2 -> n2=cutY-m2*cutX
        double n2 = cutY - m2 * cutX;

        // calculate at which point on the x axis the line (g(x)) has to start -> calculating zero point g(x)=0
        // 0=m2*xZero+n2 -> x=-n2/m2
        double xZero = -n2 / m2;
        return xZero;
    }
}
