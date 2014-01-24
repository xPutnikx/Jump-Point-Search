package v.hudnitsky.testjps.jps2;

/**
 * @author v.hudnitsky on 16.01.14.
 */
public class Heuristic {
    /**
     * Manhattan distance.
     * @param dx {int}- Difference in x.
     * @param dy {int}- Difference in y.
     * @return {number} dx + dy
     */
    public static int manhattan(int dx, int dy) {
        return dx + dy;
    }

    /**
     * Euclidean distance.
     * @param dx {int}- Difference in x.
     * @param dy {int}- Difference in y.
     * @return {number} sqrt(dx * dx + dy * dy)
     */
    public static double euclidean(int dx, int dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Chebyshev distance.
     * @param dx {int}- Difference in x.
     * @param dy {int}- Difference in y.
     * @return {number} max(dx, dy)
     */
    public static int chebyshev(int dx,int dy){
        return Math.max(dx,dy);
    }
}
