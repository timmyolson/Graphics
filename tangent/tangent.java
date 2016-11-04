import java.awt.*;
import java.awt.event.*;

public class tangent extends Frame {
    public static void main(String[] args) {
        float oX     = 0, oY    = 0,
              pX     = 0, pY    = 0,
              rad    = 0;

        int frameSize = 500;

        if (args.length == 5) {
            try {
                oX    = Float.parseFloat(args[0]);
                oY    = Float.parseFloat(args[1]);
                rad   = Float.parseFloat(args[2]);
                pX    = Float.parseFloat(args[3]);
                pY    = Float.parseFloat(args[4]);
            }
            catch (NumberFormatException e) {
                System.out.println("Input values must be floats");
                System.exit(1);
            }
        }
        else {
            System.out.println("You must specify 2 (X, Y) points and a radus\n" +
                               "Paremeters: <oX>, <oY>, <radius>, <pX>, <pY>\n" +
                               "Example:    java tangent 20 30 35 80 40");
            System.exit(1);
        }

        new tangent(frameSize, oX, oY, pX, pY, rad);
    }

   tangent(int frameSize, float oX, float oY, float pX, float pY, float rad) {
        super("Map Drawing Problem");
        addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
        setSize(frameSize, frameSize);
        setResizable(false);
        setLocationRelativeTo(null);
        add("Center", new Cvtangent(oX, oY, pX, pY, rad));
        show();
   }
}

class Cvtangent extends Canvas {
    int maxX    = 0, maxY = 0,
        addX    = 0, addY = 0;

    int boundry = 100;
    int halfBoundry = Math.round(boundry/2);

    boolean showTri    = false;
    int     showTriCNT = 0;

    float pixelSize = 0, pixelSizeMin = 0,
          xPoint    = 0, yPoint = 0,
          oX        = 0, oY     = 0,
          pX        = 0, pY     = 0,
          qX        = 0, qY     = 0,
          rad       = 0;

    Color gridColor = new Color(255, 241, 204);

    Cvtangent(float oX, float oY, float pX, float pY, float rad) {
        this.oX    = oX;
        this.oY    = oY;
        this.pX    = pX;
        this.pY    = pY;
        this.rad   = rad;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (showTriCNT % 2 == 0) {
                    showTri = false;
                }
                else {
                    showTri = true;
                }
                showTriCNT++;
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_LEFT) {
                    addX -= 1;
                }

                if (key == KeyEvent.VK_RIGHT) {
                    addX += 1;
                }

                if (key == KeyEvent.VK_UP) {
                    addY -= 1;
                }

                if (key == KeyEvent.VK_DOWN) {
                    addY += 1;
                }
                repaint();

            }
        });
    }


    int iX(float x) {
        return Math.round(x * pixelSize);
    }
    int iY(float y) {
        return maxY - Math.round(y * pixelSize);
    }
    float fx(int X) {
        return (X / pixelSize);
    }
    float fy(int Y) {
        return Y / pixelSize;
    }

    public void paint(Graphics g) {
        Dimension dim = getSize();
        maxX = dim.width;
        maxY = dim.height;
        pixelSize = Math.min(maxX/boundry, maxY/boundry);
        drawGrid(g);
        g.setColor(Color.white);
        g.fillRect(8, 2, 225, 41);

        g.setColor(Color.black);
        g.drawString("Click mouse to show or hide triangle.", 10, 15);
        g.drawString("UP   and DOWN  - increase/decrease qX", 10, 27);
        g.drawString("LEFT and RIGHT - increase/decrease qY", 10, 39);


        String pointP = "(" + pX + "," + pY + ")";
        g.drawString(pointP, iX(pX) + 7, iY(pY) - 7);
        g.drawRect(iX(pX) - 2, iY(pY) - 2, 4, 4);

        // Draw point (Px, Py)
        String pointQ = "(" + oX + "," + oY + ")";
        g.drawString(pointQ, iX(oX) + 7, iY(oY) - 7);
        g.drawRect(iX(oX) - 2, iY(oY) - 2, 4, 4);

        // g.drawLine(iX(pX), iY(pY), iX(oX), iY(oY));
        int screenRad  = Math.round(rad * pixelSize);
        int screenDiam = screenRad * 2;
        g.drawOval(iX(oX - rad), iY(oY + rad), screenDiam, screenDiam);

        //   distOP:
        //    ____________________________           _____________
        //   √ (oX - pX)^2 + (oY - pY)^2      ->    √ xOP2 + yOP2
        float xOP  = oX - pX;
        float xOP2 = (float) Math.pow(xOP, 2);

        float yOP  = oY - pY;
        float yOP2 = (float) Math.pow(yOP, 2);

        float distOP = (float) Math.sqrt(xOP2 + yOP2);
        float distOP2 = (float) Math.pow(distOP, 2);

        float rad2 = (float) Math.pow(rad, 2);

        float alpha = (float) Math.sqrt(distOP2 - rad2);
        float alpha2 = (float) Math.pow(alpha, 2);

        System.out.println("Length OP    = " + distOP);
        System.out.println("Length alpha = " + alpha);
        System.out.println("Length rad   = " + rad);
        // eq 1: ax + by = c
        // eq 2: dx + ey = f        eliminate x by multiplying eq1 by d/a
        //       ===========
        // eq 3: jx + hy = i
        double a, b, c, d, e, f, j, h, i;
        a = rad;
        b = alpha;
        c = pX - oX;

        System.out.println("eq 1: " + a + "x + " + b + "y = " + c);

        d = -alpha;
        e = rad;
        f = pY - oY;

        System.out.println("eq 2: " + d + "x + " + e + "y = " + f);

        double elimX;
        if (d < 0) {
            elimX = -d/a;
        }
        else {
            elimX = d/a;
        }
        a = a * elimX;
        b = b * elimX;
        c = c * elimX;

        j = a + d;
        h = b + e;
        i = c + f;

        System.out.println("eq 3: " + j + "x + " + h + "y = " + i);
        System.out.println("elimX = " + elimX);

        double y = i/h;
        double x = (f - (e * y))/(d);

        float uX = (float) x;
        float uY = (float) y;

        float scaleduX = rad*uX;
        float scaleduY = rad*uY;

        qX = oX + scaleduX;
        qY = oY + scaleduY;

        int displayqX = iX(qX) + addX;
        int displayqY = iY(qY) + addY;

        if (showTri == true) {
            g.setColor(Color.green);
            g.drawLine(iX(oX), iY(oY), iX(qX), iY(qY));
            g.setColor(Color.blue);
            g.drawLine(iX(oX), iY(oY), iX(pX), iY(pY));
            g.setColor(Color.red);
            g.drawLine(iX(pX), iY(pY), iX(qX), iY(qY));
        }

        g.setColor(Color.black);
        g.drawLine(iX(pX), iY(pY), displayqX, displayqY);
        // g.drawLine(iX(), iY(), iX(), iY());
    }

    public void drawGrid(Graphics g) {
        g.setColor(gridColor);

        for (int i = 0; i < boundry; i += 1) {
            g.drawLine(iX(i), 0, iX(i), maxY);
        }
        for (int i = 0; i < boundry; i += 1) {
            g.drawLine(0, iY(i), maxX, iY(i));
        }

        // set back to default
        g.setColor(Color.black);
    }
}
