package MotionSimulatorPackage;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class VisualFrame extends Frame implements Runnable, WindowListener {
    private Thread t;
    private Image buffer;
    private Graphics bg;
    private int x = 0;
    private int y = 0;
    private int angle = 0;
    private int Scale = 1;
    private int BaseUnit = 400;
    private boolean done = false;
    private int pointer = 0;
    private ArrayList<State> path;

    public  VisualFrame(ArrayList<State> path) {
        this.path = path;
        this.addWindowListener(this);
        this.setBounds(50, 50, 700, 700);
        this.setVisible(true);
        this.t = new Thread(this);
        this.t.start();
    }

    public void run() {
        for(; !this.done; this.repaint()) {
            try {
                Thread.sleep(400L);
            } catch (Exception var2) {
                System.out.println("Error");
            }

            if (this.pointer < this.path.size()) {
                ++this.pointer;
                this.setTitle(this.pointer + "");
            }
        }
    }

    public void paint(Graphics g) {
        this.buffer = this.createImage(this.getWidth(), this.getHeight());
        this.bg = this.buffer.getGraphics();
        this.bg.setColor(Color.GRAY);
        this.bg.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
        this.bg.drawLine(this.getWidth() / 2, 0, this.getWidth() / 2, this.getHeight());
        this.bg.drawString("0", this.getWidth() / 2, this.getHeight() / 2);

        int prevx;
        for(prevx = 1; prevx * (this.BaseUnit / this.Scale) < this.getWidth() / 2; ++prevx) {
            this.bg.drawLine(this.getWidth() / 2 + prevx * (this.BaseUnit / this.Scale), this.getHeight() / 2 + 3, this.getWidth() / 2 + prevx * (this.BaseUnit / this.Scale), this.getHeight() / 2 - 3);
            this.bg.drawString(prevx * 100 + "", this.getWidth() / 2 + prevx * (this.BaseUnit / this.Scale), this.getHeight() / 2);
        }

        for(prevx = 1; prevx * (this.BaseUnit / this.Scale) < this.getWidth() / 2; ++prevx) {
            this.bg.drawLine(this.getWidth() / 2 - prevx * (this.BaseUnit / this.Scale), this.getHeight() / 2 + 3, this.getWidth() / 2 - prevx * (this.BaseUnit / this.Scale), this.getHeight() / 2 - 3);
            this.bg.drawString(-1 * prevx * 100 + "", this.getWidth() / 2 - prevx * (this.BaseUnit / this.Scale), this.getHeight() / 2);
        }

        for(prevx = 1; prevx * (this.BaseUnit / this.Scale) < this.getHeight() / 2; ++prevx) {
            this.bg.drawLine(this.getWidth() / 2 - 3, this.getHeight() / 2 + prevx * (this.BaseUnit / this.Scale), this.getWidth() / 2 + 3, this.getHeight() / 2 + prevx * (this.BaseUnit / this.Scale));
            this.bg.drawString(-1 * prevx * 100 + "", this.getWidth() / 2, this.getHeight() / 2 + prevx * (this.BaseUnit / this.Scale));
        }

        for(prevx = 1; prevx * (this.BaseUnit / this.Scale) < this.getHeight() / 2; ++prevx) {
            this.bg.drawLine(this.getWidth() / 2 - 3, this.getHeight() / 2 - prevx * (this.BaseUnit / this.Scale), this.getWidth() / 2 + 3, this.getHeight() / 2 - prevx * (this.BaseUnit / this.Scale));
            this.bg.drawString(prevx * 100 + "", this.getWidth() / 2, this.getHeight() / 2 - prevx * (this.BaseUnit / this.Scale));
        }

        prevx = 0;
        int prevy = 0;
        this.bg.setColor(new Color(0.0F, 0.0F, 1.0F, 0.5F));

        for(int k = 0; k < this.pointer; ++k) {
            State s = this.path.get(k);
            this.x = (int)s.x / this.Scale * this.BaseUnit / 100;
            this.y = (int)s.y / this.Scale * this.BaseUnit / 100;
            this.angle = (int)s.a;
            if (k == 0) {
                prevx = this.x;
                prevy = this.y;
            }

            this.bg.drawLine(prevx + this.getWidth() / 2, -1 * prevy + this.getHeight() / 2, this.x + this.getWidth() / 2, -1 * this.y + this.getHeight() / 2);
            prevx = this.x;
            prevy = this.y;
        }

        if (Math.abs(this.x) > this.getWidth() / 2 || Math.abs(this.y) > this.getHeight() / 2) {
            ++this.Scale;
        }

        this.bg.setColor(Color.red);
        this.bg.fillOval(this.x - 5 + this.getWidth() / 2, -1 * this.y - 5 + this.getHeight() / 2, 10, 10);
        this.bg.drawLine(this.x + this.getWidth() / 2, -1 * this.y + this.getHeight() / 2, (int)((double)(this.x + this.getWidth() / 2) + 40.0D / (double)this.Scale * Math.cos((double)this.angle * 1.0D / 360.0D * 2.0D * 3.141592653589793D)), (int)((double)(-1 * this.y + this.getHeight() / 2) - 40.0D / (double)this.Scale * Math.sin((double)this.angle * 1.0D / 360.0D * 2.0D * 3.141592653589793D)));
        g.drawImage(this.buffer, 0, 0, null);
    }

    public void update(Graphics g) {
        this.paint(g);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        this.done = true;
        this.setVisible(false);
        this.dispose();
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {

    }
//
//    public BufferedImage getImage() {
//        return image;
//    }
}
