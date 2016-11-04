import java.awt.*;
import java.awt.event.*;

public class h1q2 extends Frame {
    public static final int FRAME_SIZE = 600;
    public static final int MARGIN     = 25;

    public static void main(String[] args) {
        int radius = 50;

        new h1q2(radius);
    }

   h1q2(int radius) {
        super("Homework 1 Question 2: Hex Pattern");
        addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
        setSize(FRAME_SIZE, FRAME_SIZE);
        add("Center", new Cvh1q2(radius, MARGIN));
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        show();
   }
}

class Cvh1q2 extends Canvas {
    int maxX; int maxY;
    int radius = 0;
    int margin = 0;
    int xPoint = 0;
    int yPoint = 0;

    float pixelWidth, pixelHeight, rWidth = 10.0F, rHeight = 7.5F,
          xP = -1, yP;
    Cvh1q2 (int radius, int margin) {
        this.radius = radius;
        this.margin = margin;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                xPoint = evt.getX();
                yPoint = evt.getY();
                repaint();
            }
        });
    }
    void initgr()
    {
        Dimension d = getSize();
        maxX = d.width - 1; maxY = d.height - 1;
        pixelWidth  = rWidth/maxX; pixelHeight = rHeight/maxY;
    }

    int iX(float x){return Math.round(x/pixelWidth);}
    int iY(float y){return maxY - Math.round(y/pixelHeight);}
    float fx(int X){return X * pixelWidth;}
    float fy(int Y){return (maxY - Y) * pixelHeight;}

    public void paint(Graphics g) {
        initgr();
        if (xPoint == 0 && yPoint == 0) {
            g.drawString("Current Radius [" + radius + "] Click in frame to change radius!", 15, 15);
        }
        else {
            g.drawString("Current Radius [" + radius + "] Selected point: " + xPoint + " " + yPoint, 15, 15);
            double xPointS = Math.pow(xPoint, 2);
            double yPointS = Math.pow(yPoint, 2);
            double dDistance = Math.sqrt(xPointS + yPointS);
            radius = (int) dDistance;
        }

        int pX = margin;
        int pY = margin;
        int qX = maxX - margin;
        int qY = maxY - margin;
        int zX = qX - margin;
        int zY = qY - margin;

        int halfRadius  = radius / 2;
        int halfRadiusS = halfRadius * halfRadius;
        int radiusS     = radius * radius;

        double dheight = Math.sqrt(radiusS - halfRadiusS);
        int height = (int) dheight;

        int doubleHeight = height * 2;
        int diameter     = radius * 2;

        int startX = pX + radius;
        int startY = pY + height;

        int numOfColumns = zX / diameter;
        int numOfRows    = zY / doubleHeight;

        int currentRow = 0;
        int currentCol = 0;

        if ((diameter < zX) && (doubleHeight < zY)) {
            for (int i = startX; i <= zX; i += diameter) {
                for (int j = startY; j <= zY; j += doubleHeight) {
                    if (currentCol % 2 == 0) {
                        drawHex(g, i, j, radius);
                    }
                    else {
                        if ((j + height) < zY) {
                            drawHex(g, i - halfRadius, j + height, radius);
                        }
                    }
                    currentRow += 1;
                }

                if (currentCol % 2 == 1) {
                    i -= radius;
                }

                currentCol += 1;
                currentRow = 0;
            }
        }
        else {
            g.drawString("Warning! The radius you have selected is too big and cannot be drawn in frame!\n", 15, 35);
            g.drawString("Resize your frame or pick a new radius.\n", 15, 50);
        }

        // DEBUG: Useful log messages.
        // System.out.println("Mod ####");
        // System.out.println("pX = " + pX);
        // System.out.println("pY = " + pY);
        // System.out.println("qX = " + qX);
        // System.out.println("qY = " + qY);
        // System.out.println("zX = " + zX);
        // System.out.println("zY = " + zY);
        // System.out.println("maxX = " + maxX);
        // System.out.println("maxY = " + maxY);
        // System.out.println("radius = " + radius);
        // System.out.println("height = " + height);
        // System.out.println("startX = " + startX);
        // System.out.println("startY = " + startY);
        // System.out.println("numOfRows    = " + numOfRows);
        // System.out.println("numOfColumns = " + numOfColumns);

    }

    public static void drawHex(Graphics g, int x, int y, int radius) {
    //                      hexNW ______  hexNE                /|
    //                           /      \                     / |
    //                          /        \                 c /  | b
    //                    hexW /          \ hexE            /___|
    //                         \          /                  a
    //                          \        /
    //                     hexSW \______/  hexSE
        int hexWx = 0, hexNWx = 0, hexNEx = 0, hexEx = 0, hexSEx = 0, hexSWx = 0;
        int hexWy = 0, hexNWy = 0, hexNEy = 0, hexEy = 0, hexSEy = 0, hexSWy = 0;
        int a  = 0, c  = 0;
        double da2 = 0, db2 = 0, dc2 = 0, db = 0;

        c = radius;
        a = radius / 2;

        da2 = Math.pow(a, 2);
        dc2 = Math.pow(c, 2);

        db2 = dc2 - da2;
        db = Math.sqrt(db2);

        int a2 = (int) da2;
        int b2 = (int) db2;
        int c2 = (int) dc2;
        int b  = (int) db;

        hexWx = x - radius;
        hexWy = y;

        hexNWx = x - a;
        hexNWy = y + b;

        hexSWx = x - a;
        hexSWy = y - b;

        hexEx = x + radius;
        hexEy = y;

        hexNEx = x + a;
        hexNEy = y + b;

        hexSEx = x + a;
        hexSEy = y - b;

        g.setColor(Color.black);
        g.drawLine(hexWx, hexWy, hexNWx, hexNWy);
        g.drawLine(hexNWx, hexNWy, hexNEx, hexNEy);
        g.drawLine(hexNEx, hexNEy, hexEx, hexEy);
        g.drawLine(hexEx, hexEy, hexSEx, hexSEy);
        g.drawLine(hexSEx, hexSEy, hexSWx, hexSWy);
        g.drawLine(hexSWx, hexSWy, hexWx, hexWy);


        // DEBUG: Useful log messages.
        // System.out.println("hexWx = " + hexWx);
        // System.out.println("hexWy = " + hexWy);
        // System.out.println("hexNWx = " + hexNWx);
        // System.out.println("hexNWy = " + hexNWy);
        // System.out.println("hexSWx = " + hexSWx);
        // System.out.println("hexSWy = " + hexSWy);
        // System.out.println("hexEx = " + hexEx);
        // System.out.println("hexEy = " + hexEy);
        // System.out.println("hexNEx = " + hexNEx);
        // System.out.println("hexNEy = " + hexNEy);
        // System.out.println("hexSEx = " + hexSEx);
        // System.out.println("hexSEy = " + hexSEy);
    }
}
