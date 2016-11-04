import java.awt.*;
import java.awt.event.*;

public class h1q1 extends Frame {
    public static void main(String[] args) {
        int count = 0, margin = 0, frame = 0;

        if (args.length == 3) {
            try {
                count  = Integer.parseInt(args[0]);
                margin = Integer.parseInt(args[1]);
                frame  = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e) {
                System.out.println("Input values must be integers");
                System.exit(1);
            }
        }
        else {
            System.out.println("You must specify a <count>, <margin>, and <framesize>");
            System.out.println("Example: java h1q1 10 15 200");
            System.exit(1);
        }

        new h1q1(count, margin, frame);
    }

   h1q1(int count, int margin, int frame) {
        super("Homework 1 Question 1: Checker Pattern");
        addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
        setSize(frame, frame);
        add("Center", new Cvh1q1(margin, count));
        show();
   }
}

class Cvh1q1 extends Canvas {
    int margin = 0;
    int count = 0;
    Cvh1q1 (int margin, int count) {
        this.margin = margin;
        this.count = count;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        int maxX = d.width, maxY = d.height;

        int squareXorY = 0;

        int pX = margin;
        int pY = margin;
        int qX = maxX - margin;
        int qY = maxY - margin;
        int zX = qX - margin;
        int zY = qY - margin;

        g.setColor(Color.lightGray);
        g.fillRect(0, 0, maxX, maxY);

        if (maxX < maxY) {
            squareXorY = zX / count;
        }
        else {
            squareXorY = zY / count;
        }

        for (int row = 0; row < count; row++) {
            for(int col = 0; col < count; col++) {
                g.setColor((row + count - col) % 2 == 0 ?
                   Color.blue : Color.red);
                g.fillRect(pX + (row * squareXorY), pY + (col * squareXorY), squareXorY, squareXorY);
            }
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

        // DEBUG: Show one large blue Rect.
        //g.setColor(Color.blue);
        //g.fillRect(pX, pY, zX, zY);
    }
}
