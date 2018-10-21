package Controller;

public interface PathFunction<T> {
    /**
     * Defines the function which the robot will travel. Given an X value of the function, returns a coordinate pair where
     *
     * <ul>
     *     <li>Key = x</li>
     *     <li>Value = y</li>
     * </ul>
     * @param x x value in [x,y]
     * @return coordinate containing position as [x,y]
     */
    Coordinate function(double x);

    /**
     * Evaluates a set of movements
     * @param t movements list
     * @return fitness value
     */
    double evaluate(T t);
}
