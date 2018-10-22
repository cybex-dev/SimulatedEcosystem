package MotionSimulatorPackage;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import Controller.Coordinate;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return Double.compare(state.x, x) == 0 &&
                Double.compare(state.y, y) == 0 &&
                Double.compare(state.a, a) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, a);
    }
}
