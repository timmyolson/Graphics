import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class pythagTree extends Frame {
    public static final int FRAME_SIZE_X = 1300;
    public static final int FRAME_SIZE_Y = 750;

    public static void main(String[] args) {
        int radius = 50;

        new pythagTree(radius);
    }

   pythagTree(int radius) {
        super("Pythagoras Tree");
        addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
        setSize(FRAME_SIZE_X, FRAME_SIZE_Y);
        setResizable(false);
        add("Center", new CvpythagTree());
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        show();
   }
}

class CvpythagTree extends Canvas {
    Vector v = new Vector();
    Stack vStack = new Stack();
    int limit = 5;

    CvpythagTree() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (v.size() < 2) {
                    int xPoint = evt.getX();
                    int yPoint = evt.getY();
                    if (xPoint < 650) {
                        v.addElement(new Point2D(xPoint, yPoint));
                    }
                    repaint();
                }
            }
        });
    }

    int convert(int y) {
        Dimension dim = getSize();
        int maxY = dim.height;
        return maxY - y;
    }

    public void paint(Graphics g) {
        Dimension dim = getSize();
        int maxX = dim.width/2;
        int maxY = dim.height;

        g.drawLine(maxX, 0, maxX, maxY);
        g.drawRect(0, 0, maxX*2-1, maxY-1);

        if (v.size() < 2) {
            g.drawString("Click 2 points, the second should be to the right of the first.", 200, maxY/2 + 15);
            g.drawString("      For the best results, draw a 'small' line and stay on the", 200, maxY/2 + 39);
            g.drawString("      left half of the canvas, 2 trees will be drawn.", 200, maxY/2 + 51);
            g.drawString("      Left tree => Depth First     Right Tree => Breadth First", 200, maxY/2 + 63);
            if (v.size() == 1) {
                Point2D a = (Point2D)(v.elementAt(0));
                g.drawRect(a.x - 2, a.y - 2, 4, 4);
            }
            return;
        }

        Point2D a = (Point2D)(v.elementAt(0));
        Point2D b = (Point2D)(v.elementAt(1));

        a.y = convert(a.y);
        b.y = convert(b.y);

        Point2D aOffset = new Point2D(a.x + maxX, a.y);
        Point2D bOffset = new Point2D(b.x + maxX, b.y);
        g.setFont(new Font("TimesRoman", Font.BOLD, 48));
        g.drawString("Stack (DFS)", 100, maxY - 50);
        g.drawString("Queue (BFS)", maxX + 100, maxY - 50);

        pythagStack(g, a, b, limit);
        pythagQueue(g, aOffset, bOffset, limit);


    }
    public void pythagStack(Graphics g, Point2D a, Point2D b, int limit) {
        float set1 = (float) Math.pow(b.x - a.x, 2);
        float set2 = (float) Math.pow(b.y - a.y, 2);
        float dist = (float) Math.sqrt(set1 + set2);

        if (dist < limit) return;

        Point2D ab = new Point2D(b.x - a.x, b.y - a.y);

        Point2D d = new Point2D(a.x + -ab.y, a.y + ab.x);
        Point2D c = new Point2D(d.x + ab.x, d.y + ab.y);
        Point2D e = new Point2D(d.x + (ab.x + -ab.y)/2, d.y + (ab.y + ab.x)/2);

        _drawHouse(g, a, b, c, d, e);
        pythagStack(g, d, e, limit);
        pythagStack(g, e, c, limit);
    }

    public void pythagQueue(Graphics g, Point2D a, Point2D b, int limit) {
        Vector vQueue = new Vector();
        float set1 = (float) Math.pow(b.x - a.x, 2);
        float set2 = (float) Math.pow(b.y - a.y, 2);
        float dist = (float) Math.sqrt(set1 + set2);

        vQueue.addElement(new Point2D(a.x, a.y));
        vQueue.addElement(new Point2D(b.x, b.y));

        if (dist < limit) return;

        while (!vQueue.isEmpty()) {
            a = (Point2D)(vQueue.elementAt(0));
            vQueue.remove(0);

            b = (Point2D)(vQueue.elementAt(0));
            vQueue.remove(0);

            Point2D ab = new Point2D(b.x - a.x, b.y - a.y);

            Point2D d = new Point2D(a.x + -ab.y, a.y + ab.x);
            Point2D c = new Point2D(d.x + ab.x, d.y + ab.y);
            Point2D e = new Point2D(d.x + (ab.x + -ab.y)/2, d.y + (ab.y + ab.x)/2);

            _drawHouse(g, a, b, c, d, e);

            vQueue.addElement(new Point2D(d.x, d.y));
            vQueue.addElement(new Point2D(e.x, e.y));
            vQueue.addElement(new Point2D(e.x, e.y));
            vQueue.addElement(new Point2D(c.x, c.y));

            set1 = (float) Math.pow(b.x - a.x, 2);
            set2 = (float) Math.pow(b.y - a.y, 2);
            dist = (float) Math.sqrt(set1 + set2);

            if (dist < limit) return;
        }
    }

    public void _drawHouse(Graphics g, Point2D a, Point2D b, Point2D c, Point2D d, Point2D e) {
        try {
            Thread.sleep(5);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        int squarePointsX[] = {a.x, b.x, c.x, d.x};
        int squarePointsY[] = {convert(a.y), convert(b.y), convert(c.y), convert(d.y)};
        int re = (int)(Math.random()*256);
        int gr = (int)(Math.random()*256);
        int bl = (int)(Math.random()*256);
        Color color = new Color(re, gr, bl);
        g.setColor(color);
        g.fillPolygon(squarePointsX, squarePointsY, 4);

        int triPointsX[] = {d.x, e.x, c.x};
        int triPointsY[] = {convert(d.y), convert(e.y), convert(c.y)};
        re = (int)(Math.random()*256);
        gr = (int)(Math.random()*256);
        bl = (int)(Math.random()*256);
        Color color2 = new Color(re, gr, bl);
        g.setColor(color2);
        g.fillPolygon(triPointsX, triPointsY, 3);

        g.setColor(Color.black);
    }
}

class Point2D
{  int x, y;
   Point2D(int x, int y){this.x = x; this.y = y;}
}










































// as
