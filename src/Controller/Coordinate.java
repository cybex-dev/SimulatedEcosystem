package Controller;

public class Coordinate {
    private double x, y;

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the difference as a new coordinate. Current coordinate value, less the 'that' coordinate.
     * @param that other coordinate
     * @return difference coordinate
     */
    Coordinate difference(Coordinate that) {
        return difference(this, that);
    }

    private static Coordinate difference(Coordinate current, Coordinate other){
        return new Coordinate(current.x - other.x, current.y - other.y);
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
