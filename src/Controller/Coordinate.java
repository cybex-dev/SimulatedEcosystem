package Controller;

public class Coordinate {
    private int x, y;

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Coordinate(double x, double y) {
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
    }

    int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    public void setY(int y) {
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
}
