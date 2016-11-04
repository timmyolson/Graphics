import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class Morph extends Frame {
    public static final int FRAME_SIZE_X = 600;
    public static final int FRAME_SIZE_Y = 600;

    public static void main(String[] args) {
        int stages = 0;

        if (args.length == 1) {
            try {
                stages = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.out.println("Input value must be an integer");
                System.exit(1);
            }
        }
        else {
            System.out.println("You must specify stages to show in morph\n" +
                               "Paremeters: <stages>\n" +
                               "Example:    java Morph 8");
            System.exit(1);
        }

        new Morph(stages);
    }

   Morph(int stages) {
        super("Polygon Morphing");
        addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
        setSize(FRAME_SIZE_X, FRAME_SIZE_Y);
        setResizable(false);
        setLocationRelativeTo(null);
        add("Center", new CvMorph());
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        show();
   }
}

class CvMorph extends Canvas {
    Vector poly1 = new Vector();
    Vector poly2 = new Vector();

    Point2D center = new Point2D(0,0);
    Point2DUnit u  = new Point2DUnit(0,0);
    Point2DUnit uM = new Point2DUnit(0,0);

    // define the center of the star shaped poly.
    boolean centerDef = false,
            poly1Fin  = false,
            poly2Fin  = false;

    CvMorph() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                int xPoint = evt.getX();
                int yPoint = evt.getY();

                if (!centerDef) {
                    center.x = xPoint;
                    center.y = yPoint;
                    centerDef = true;
                }
                else if (!poly1Fin) {
                    Point2D pt = new Point2D(xPoint, yPoint);
                    if (poly1.size() > 0) {
                        Point2D origPt = (Point2D)(poly1.elementAt(0));
                        double xComp   = Math.pow(origPt.x - pt.x, 2);
                        double yComp   = Math.pow(origPt.y - pt.y, 2);
                        float  dist    = (float) Math.sqrt(xComp + yComp);
                        if (dist < 16) {
                            pt = origPt;
                            poly1Fin = true;
                        }
                    }
                    if (poly1.size() >= 1) {
                        // Calculate unit vector of last point.
                        Point2D lastPt = (Point2D)(poly1.lastElement());
                        double xCompU   = Math.pow(lastPt.x - center.x, 2);
                        double yCompU   = Math.pow(lastPt.y - center.y, 2);
                        float  distU    = (float) Math.sqrt(xCompU + yCompU);
                        u = new Point2DUnit((pt.x - center.x)/distU, (pt.y - center.y)/distU);
                        // Calculate unit vector uM "unit Mouse" to mouse new point.
                        double xCompUM   = Math.pow(pt.x - center.x, 2);
                        double yCompUM   = Math.pow(pt.y - center.y, 2);
                        float  distUM    = (float) Math.sqrt(xCompUM + yCompUM);
                        uM = new Point2DUnit((xPoint - center.x)/distUM, (yPoint - center.y)/distUM);

                        // Find dot product and angle.
                        float dotUandUM = (u.uX * uM.uX) + (u.uY * uM.uY);
                        double cosUandUM = Math.acos(dotUandUM);
                        System.out.println("Unit vector check: " + u.uX + " " + u.uY);
                        System.out.println("Unit vector check: " + uM.uX + " " + uM.uY);
                        System.out.println("Theta: " + cosUandUM);
                    }
                    poly1.addElement(pt);
                }
                else if (!poly2Fin && poly1Fin) {
                    Point2D pt = new Point2D(xPoint, yPoint);
                    if (poly2.size() > 0) {
                        Point2D origPt = (Point2D)(poly2.elementAt(0));
                        double xComp   = Math.pow(origPt.x - pt.x, 2);
                        double yComp   = Math.pow(origPt.y - pt.y, 2);
                        double dist    = Math.sqrt(xComp + yComp);
                        if (dist < 16) {
                            pt = origPt;
                            poly2Fin = true;
                        }
                    }
                    poly2.addElement(pt);
                }
                repaint();
            }
        });
    }

    int conv(int y) {
        Dimension dim = getSize();
        int maxY = dim.height;
        return maxY - y;
    }

    public void paint(Graphics g) {

        // Establish center point of polygons.
        if (centerDef) {
            g.drawRect(center.x - 2, center.y - 2, 4, 4);
        }

        // Get the sie of the vector try to draw.
        int poly1Size = poly1.size();

        // Not yet defiend, dont draw.
        if (poly1Size == 0) return;
        g.setColor(Color.red);

        Point2D a = (Point2D)(poly1.elementAt(0));
        // Draw red rect to guide user.
        if (poly1Size == 1) g.drawRect(a.x - 2, a.y - 2, 4, 4);

        // Draw the polygon.
        for (int i=1; i<=poly1Size; i++) {
            if (i == poly1Size) break;
            Point2D b = (Point2D)(poly1.elementAt(i % poly1Size));
            g.drawLine(a.x, a.y, b.x, b.y);
            a = b;
        }

        // Draw a line to guide the user on how to make a star polygon.
        if (!poly1Fin) {
            Point2D lastPt = (Point2D)(poly1.lastElement());
            double xCompU   = Math.pow(lastPt.x - center.x, 2);
            double yCompU   = Math.pow(lastPt.y - center.y, 2);
            float  distU    = (float) Math.sqrt(xCompU + yCompU);

            // find the unit vector connecting the origin and last mapped point.
            u = new Point2DUnit((lastPt.x - center.x)/distU, (lastPt.y - center.y)/distU);
            // System.out.println("Unit vector check: " + u.uX + " " + u.uY);
            int scaleX = Math.round(1000 * u.uX);
            int scaleY = Math.round(1000 * u.uY);

            // Draw the lines.
            g.setColor(Color.magenta);
            g.drawLine(center.x, center.y, center.x + scaleX, center.y + scaleY);
            g.drawLine(center.x, center.y, center.x - scaleX, center.y - scaleY);
        } // End draw help

        int poly2Size = poly2.size();
        if (poly2Size == 0) return;
        g.setColor(Color.blue);

        a = (Point2D)(poly2.elementAt(0));
        if (poly2Size == 1) g.drawRect(a.x - 2, a.y - 2, 4, 4);

        for (int i=1; i<=poly2Size; i++) {
            if (i == poly2Size) break;
            Point2D b = (Point2D)(poly2.elementAt(i % poly2Size));
            g.drawLine(a.x, a.y, b.x, b.y);
            a = b;
        }

        // Draw a line to guide the user on how to make a star polygon.
        if (!poly2Fin) {
            Point2D lastPt = (Point2D)(poly2.lastElement());
            double xCompU   = Math.pow(lastPt.x - center.x, 2);
            double yCompU   = Math.pow(lastPt.y - center.y, 2);
            float  distU    = (float) Math.sqrt(xCompU + yCompU);

            // find the unit vector connecting the origin and last mapped point.
            u = new Point2DUnit((lastPt.x - center.x)/distU, (lastPt.y - center.y)/distU);
            // System.out.println("Unit vector check: " + u.uX + " " + u.uY);
            int scaleX = Math.round(1000 * u.uX);
            int scaleY = Math.round(1000 * u.uY);

            // Draw the lines.
            g.setColor(Color.magenta);
            g.drawLine(center.x, center.y, center.x + scaleX, center.y + scaleY);
            g.drawLine(center.x, center.y, center.x - scaleX, center.y - scaleY);
        } // End draw help
        g.setColor(Color.black);

    }
}

class Point2D
{  int x, y;
   Point2D(int x, int y){this.x = x; this.y = y;}
}

class Point2DUnit
{  float uX, uY;
   Point2DUnit(float uX, float uY){this.uX = uX; this.uY = uY;}
}










































// as
