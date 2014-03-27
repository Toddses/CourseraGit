/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/18/2014
 * Last updated:  2/25/2014
 * 2/25/2014 : Finished the compareTo() method
 *  
 * An immutable data type for points in the plane.
 * 
 * Unit testing uses an Integer command line argument for an input file
 * 
 * Compilation:   javac Point.java
 * Execution: java Point input.txt
 * Dependencies: StdDraw.java, StdOut.java
 */

import java.util.Comparator;

public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER = new Comparator<Point>() {
        public int compare(Point p1, Point p2)
        {
            double p1Slope = slopeTo(p1);
            double p2Slope = slopeTo(p2);
            
            if      (p1Slope < p2Slope) { return -1; }
            else if (p1Slope > p2Slope) { return 1; }
            else                        { return 0; }
        }
    };

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

/**
 * create the point at (x, y)
 * @param int x coordinate
 * @param int y coordinate
 */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

/**
 * plot this point to standard drawing
 */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

/**
 * draw line between this point and that point to standard drawing
 * @param Point that point to draw to
 */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

/**
 * slope between this point and that point
 * @param Point that point to find the slope to
 * @return the slope from this to that
 */
    public double slopeTo(Point that) {
        if (that.y == this.y && that.x == this.x) { 
            return Double.NEGATIVE_INFINITY;
        }
        
        if      (Math.abs(that.y - this.y) == 0) { return 0.0; }
        else if (Math.abs(that.x - this.x) == 0) { return Double.POSITIVE_INFINITY; }
        else { 
            return (double) (that.y - this.y) / (that.x - this.x);
        }
    }

/**
 * is this point lexicographically smaller than that one?
 * comparing y-coordinates and breaking ties by x-coordinates
 * @param Point that point to compare this to
 * @return -1 if this is less, 1 if this is greater, 0 if equal
 */
    public int compareTo(Point that) {
        if      (this.y < that.y) { return -1; }
        else if (this.y > that.y) { return 1; }
        else if (this.x < that.x) { return -1; }
        else if (this.x > that.x) { return 1; }
        else                      { return 0; }
    }

/**
 * return string representation of this point
 * @return the String "(x, y)"
 */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

/**
 * unit test
 * @param String[] command line arguments
 */
    public static void main(String[] args) {
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        
        // build the array of points
        In input = new In(args[0]);
        Point[] points = new Point[input.readInt()];
        
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(input.readInt(), input.readInt());
        }
        
        // draw the points and print the point to StdOut
        for (int i = 0; i < points.length; i++) {
            points[i].draw();
            StdOut.println(points[i].toString());
        }
        
        // draw a line from the first point to each successive point
        // and print that slope to StdOut
        for (int i = 1; i < points.length; i++) {
            points[0].drawTo(points[i]);
            StdOut.println(points[0].slopeTo(points[i]));
        }
    }
}