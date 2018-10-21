package MotionSimulatorPackage;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class State {
    double x;
    double y;
    double a;

    public State(double xin, double yin, double ain) {
        this.x = xin;
        this.y = yin;
        this.a = ain;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getA() {
        return a;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setA(double a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return "[" +
                x + "," +
                y +
                "]" +
                " (" + a + ")";
    }
}
