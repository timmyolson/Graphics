import java.awt.*;
import java.awt.event.*;

public class map extends Frame {
    public static void main(String[] args) {
        int xSize  = 0, ySize = 0,
            m1     = 0, c1    = 0,
            m2     = 0, c2    = 0;

        int frameSize = 400;

        if (args.length == 6) {
            try {
                m1    = Integer.parseInt(args[0]);
                c1    = Integer.parseInt(args[1]);
                m2    = Integer.parseInt(args[2]);
                c2    = Integer.parseInt(args[3]);
                xSize = Integer.parseInt(args[4]);
                ySize = Integer.parseInt(args[5]);
            }
            catch (NumberFormatException e) {
                System.out.println("Input numbers must be integers.");
                System.exit(1);
            }
        }
        else {
            System.out.println("You must specify two lines and a define a rectangular plane!\n" +
                               "Paremeters: <slope1> <intercept1> <slope1> <intercept1> <xSize> <ySize>\n" +
                               "Example:    java map 3 1 -2 3 200 200");
            System.exit(1);
        }

        new map(frameSize, m1, c1, m2, c2, xSize, ySize);
    }

   map(int frameSize, int m1, int c1, int m2, int c2, int xSize, int ySize) {
        super("Map Drawing Problem");
        addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
        setSize(frameSize, frameSize);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        add("Center", new Cvmap(m1, c1, m2, c2, xSize, ySize));
        show();
   }
}

class Cvmap extends Canvas {
    int boxStartX        = 0, boxStartY     = 0,
        boxEndX          = 0, boxEndY       = 0,
        newCenX          = 0, newCenY       = 0,
        centerScreenX    = 0, centerScreenY = 0,
        xPoint           = 0, yPoint        = 0,
        boxDim           = 0, clicks        = 0,
        xSize            = 0, ySize         = 0,
        m1               = 0, c1            = 0,
        m2               = 0, c2            = 0;

    float pixelSize      = 0,
          startX         = 0, startY         = 0,
          pixelXEstimate = 0, pixelYEstimate = 0,
          fxStart        = 0, fxEnd          = 0,
          fyStart        = 0, fyEnd          = 0,
          fxMid          = 0, fyMid          = 0,
          panX           = 0, panY           = 0;

    boolean allPointsDefined = false, boxStartPoints = false,
            firstTime        = true,  drawFirstBox   = true,
            notTopRight      = false, enablePan      = false;

    Cvmap(int m1, int c1, int m2, int c2, int xSize, int ySize) {
        this.m1    =  m1;
        this.c1    =  c1;
        this.m2    =  m2;
        this.c2    =  c2;
        this.xSize =  xSize;
        this.ySize =  ySize;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                xPoint = evt.getX();
                yPoint = evt.getY();
                clicks += 1;
                repaint();
            }
        });
    }

    int iX(float x) {
        return Math.round((x - startX)/pixelSize + boxStartX);
    }

    int iY(float y) {
        return Math.round((y - startY)/pixelSize + boxEndY);
    }

    float fx(int X) {
        return (X - boxStartX) * pixelSize + startX;
    }

    float fy(int Y) {
        return (Y - boxEndY) * pixelSize + startY;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        int maxX = d.width, maxY = d.height;

        // In a xSize 10, and a ySize 10, the origin at (-5,-5)
        startX = -xSize / 2;
        startY = -ySize / 2;

        // This conditional sets up all the nessicary steps
        if (!allPointsDefined) {
            if (firstTime) {
                firstTime = false;
                g.drawString("Please select two points in the Canvas. Start with the lower left.", 15, 35);
                g.drawString("You specified xSize = " + xSize + " and ySize = " + ySize, 15, 50);
            }
            else if (!boxStartPoints) {
                boxStartX = xPoint;
                boxStartY = yPoint;
                boxStartPoints = true;
                g.drawRect(boxStartX - 2, boxStartY - 2, 4, 4);
                g.drawString("Lower left point selected. Now select top right.", 15, 35);
            }
            else {
                if ((xPoint < boxStartX) || (yPoint > boxStartY)) {
                    notTopRight = true;
                    g.drawRect(boxStartX - 2, boxStartY - 2, 4, 4);
                    g.drawString("The second point must be the top right. Try again.", 15, 50);
                }
                else {
                    notTopRight = false;
                    boxEndX = xPoint;
                    boxEndY = yPoint;
                    allPointsDefined = true;
                    repaint();
                }
            }
        }

        if (allPointsDefined) {
            if (clicks > 3) {
                panX = fxMid - fx(xPoint);
                panY = fyMid - fy(yPoint);
            }

            fxStart = fx(boxStartX);
            fxEnd   = fx(boxEndX);
            fyStart = fy(boxStartY);
            fyEnd   = fy(boxEndY);

            fxMid = (fxEnd   - fxStart)/2;
            fyMid = (fyStart - fyEnd)/2;

            fyEnd = fyMid + fyMid;
            fyStart = 0;

            pixelXEstimate = (boxEndX - boxStartX)/(xSize - 0);
            pixelYEstimate = (boxStartY - boxEndY)/(ySize - 0);

            pixelSize = Math.max(pixelXEstimate, pixelYEstimate);

            drawViewGrid(g, fxStart, fyStart, fxEnd, fyEnd, fxMid, fyMid, panX, panY);
            drawViewRect(g, fxStart, fyStart, fxEnd, fyEnd);
            drawGraph(g, m1, c1, boxDim, fxMid, fyMid, panX, panY);
            drawGraph(g, m2, c2, boxDim, fxMid, fyMid, panX, panY);
        }
    }

    public void drawViewRect(Graphics g, float fxStart, float fyStart, float fxEnd, float fyEnd) {
        //           (fxStart, fyEnd)            (fxEnd, fyEnd)
        //         (boxStartX, boxEndY)         (boxEndX, boxEndY)
        //                    +----------------------+
        //                    |                      |
        //                    |                      |
        //                    |                      |
        //                    |                      |
        //                    +----------------------+
        //         (boxStartX, boxStartY)       (boxEndX, boxStartY)
        //           (fxStart, fyStart)          (fxEnd, fyStart)

        g.setColor(Color.black);
        g.drawLine(iX(fxStart), iY(fyStart), iX(fxEnd), iY(fyStart)); // bottom
        g.drawLine(iX(fxStart), iY(fyStart), iX(fxStart), iY(fyEnd)); // left
        g.drawLine(iX(fxStart), iY(fyEnd), iX(fxEnd), iY(fyEnd));     // top
        g.drawLine(iX(fxEnd), iY(fyEnd), iX(fxEnd), iY(fyStart));     // right
    }

    public void drawViewGrid(Graphics g, float fxStart, float fyStart, float fxEnd, float fyEnd, float fxMid, float fyMid, float panX, float panY) {
        float xSpots = (boxEndX - boxStartX)/xSize;
        float ySpots = (boxStartY - boxEndY)/ySize;
        float minMaxSpots = Math.min(xSpots, ySpots);
        float running_total = 0;

        int offset = 0;
        boxDim  = Math.round(minMaxSpots);

        Color betterBlue = new Color(0,191,255);
        g.setColor(betterBlue);

        // DRAW X
        running_total = fxMid - panX;
        while(running_total < fxEnd)  {
            offset = iX(running_total) + boxDim;
            running_total = fx(offset);
            // System.out.println("running_total = " + running_total);
            if (running_total > fxEnd) {
                break;
            }

            if (running_total > fxStart) {
                g.drawLine(offset, iY(fyStart), offset, iY(fyEnd));
            }
        }

        offset = 0;
        running_total = fxMid - panX;
        while(running_total > fxStart)  {
            offset = iX(running_total) - boxDim;
            running_total = fx(offset);
            if (running_total < fxStart) {
                break;
            }

            if (running_total < fxEnd) {
                g.drawLine(offset, iY(fyStart), offset, iY(fyEnd));
            }
        }

        // DRAW Y
        running_total = fyMid - panY;
        offset = 0;
        while(running_total < fyEnd)  {
            offset = iY(running_total) + boxDim;
            running_total = fy(offset);
            if (running_total > fyEnd) {
                break;
            }

            if (running_total > fyStart) {
                g.drawLine(iX(fxStart), offset, iX(fxEnd), offset);
            }
        }

        // DEBUG COLOR
        // g.setColor(Color.yellow);
        running_total = fyMid - panY;
        offset = 0;
        while(running_total > fyStart)  {
            offset = iY(running_total) - boxDim;
            running_total = fy(offset);
            if (running_total < fyStart) {
                break;
            }

            if (running_total < fyEnd) {
                g.drawLine(iX(fxStart), offset, iX(fxEnd), offset);
            }
        }

        g.setColor(Color.black);
        if (fxMid - panX > fxStart && fxMid - panX < fxEnd) {
            g.drawLine(iX(fxMid - panX), iY(fyStart), iX(fxMid - panX), iY(fyEnd));
        }

        if (fyMid - panY > fyStart && fyMid - panY < fyEnd) {
            g.drawLine(iX(fxStart), iY(fyMid - panY), iX(fxEnd), iY(fyMid - panY));
        }
    }

    public void drawGraph(Graphics g, float m, float c, int boxDim, float fxMid, float fyMid, float panX, float panY) {
        int offsetX = iX(fxMid - panX);
        int offsetY = iY(fyMid - panY);
        int newX = 0, newY = 0;

        int boxDimY1 = boxDim * (int) m;
        int boxDimX1 = boxDim;
        int boxInt1  = boxDim * -((int) c);
        int offsetY1 = offsetY + boxInt1;

        // Draw horizontal lines.
        if (m == 0) {
            if (offsetY1 <= iY(fyEnd) && offsetY1 >= iY(fyStart)) {
                g.setColor(Color.red);
                g.drawLine(iX(fxStart), offsetY1, iX(fxEnd), offsetY1);
                return;
            }
        }

        int distYT = offsetY - iY(fyStart) + boxInt1;
        int distYB = offsetY - iY(fyEnd) + boxInt1;
        float canFitYT = (float) distYT / (float) boxDimY1;
        float canFitYB = (float) distYB / (float) boxDimY1;
        int distanceYT = Math.round(boxDimY1*canFitYT);
        int distanceYB = Math.round(boxDimY1*canFitYB);

        float stretchXYT = (float) distanceYT / boxDimY1;
        int xYT = Math.round(stretchXYT * boxDimX1);

        float stretchXYB = (float) distanceYB / boxDimY1;
        int xYB = Math.round(stretchXYB * boxDimX1);

        // Origin is (offsetX, offsetY)
        // Top bound of box (topX, topY)
        // Bottom bound of box (botX, botY)
        int topX = offsetX + xYT;
        int topY = offsetY1 - distanceYT;

        int botX = offsetX + xYB;
        int botY = offsetY1 - distanceYB;

        // Handle extreme positive X for tops
        if (topX > iX(fxEnd)) {
            newX = iX(fxEnd) - botX;
            newY = iY(fyEnd) - Math.round(m * newX);
            topX = iX(fxEnd);
            topY = newY;
        }

        // Handle extreme negative X for tops
        if (topX < iX(fxStart)) {
            if (topX < iX(fxStart)) {
                // Avoid matmatical glitches by having both points unable to render.
                return;
            }
            newX = Math.abs(iX(fxStart) - botX);
            newY = iY(fyEnd) + Math.round(m * newX);
            topX = iX(fyStart);
            topY = newY;
        }

        // Handle extreme positive X for bots
        if (botX > iX(fxEnd)) {
            newX = iX(fxEnd) - botX;
            newY = iY(fyEnd) - Math.round(m * newX);
            botX = iX(fxEnd);
            botY = newY;
        }

        // // Handle extreme negative X for bots
        if (botX < iX(fxStart)) {
            if (topX < iX(fxStart)) {
                // Avoid matmatical glitches by having both points unable to render.
                return;
            }
            newX = Math.abs(iX(fxStart) - botX);
            newY = iY(fyEnd) - Math.round(m * newX);
            botX = iX(fxStart);
            botY = newY;
        }

        g.setColor(Color.red);
        g.drawLine(botX, botY, topX, topY);
    }
}
