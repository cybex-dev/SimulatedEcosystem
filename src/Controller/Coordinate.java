package Controller;

public class Coordinate {
    private double x, y;

    Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    double getY() {
        return y;
    }

    private static Coordinate difference(Coordinate current, Coordinate other){
        return new Coordinate(current.x - other.x, current.y - other.y);
    }

    @Override
    public String toString() {
        return "{" + "x=" + x + ", y=" + y + '}';
    }
}
