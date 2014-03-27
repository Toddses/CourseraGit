/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/25/2014
 * Last updated:  2/25/2014
 *  
 * Fast takes an input file with a list of points and tests all the
 * points for collinearity, then returns the list of points (4 or more) that
 * are collinear. Plus it draws the points and draws the line that the points
 * lie upon. Its great!
 * 
 * Fast is a much faster version of Brute. Similar functionality but should
 * be much more useful by today's standards.
 * 
 * Compilation:   javac Fast.java
 * Execution: java Fast input.txt
 * Dependencies: StdOut.java
 */

import java.util.Arrays;
import java.util.ArrayList;

public class Fast {
    
    private class Node {
        private Point p1;
        private Point p2;
        private double slope;
        
        private Node(Point p1, Point p2, double slope)
        {
            this.p1 = p1;
            this.p2 = p2;
            this.slope = slope;
        }
    }
    
    public static void main(String[] args)
    {
       StdDraw.setXscale(0, 32768);
       StdDraw.setYscale(0, 32768);
       
       // open up the file. the first line is an int that tells us how many
       // points are in the file
       In input = new In(args[0]);
       Point[] points = new Point[input.readInt()];
       
       // read in the remainder of the file, one int at a time, and build
       // our array of points using the Point object. Also draw the points.
       for (int i = 0; i < points.length; i++) {
           points[i] = new Point(input.readInt(), input.readInt());
           points[i].draw();
       }
       
       Arrays.sort(points);
       //Arrays.sort(points, points[0].SLOPE_ORDER);
       
       ArrayList<Point> endPoints = new ArrayList<Point>();
       
       for (int i = 0; i < points.length; i++) {
           Point[] tmp = Arrays.copyOf(points, points.length);
           Arrays.sort(tmp, i, tmp.length, tmp[i].SLOPE_ORDER);

           ArrayList<Point> collinear = new ArrayList<Point>();
           for (int j = i + 1; j < (tmp.length - 2); j++) {
               double tmpSlope = tmp[i].slopeTo(tmp[j]);
               
               if (tmpSlope == tmp[i].slopeTo(tmp[j+1])) {
                   if (!collinear.contains(tmp[i]))   { collinear.add(tmp[i]); }
                   if (!collinear.contains(tmp[j]))   { collinear.add(tmp[j]); }
                   if (!collinear.contains(tmp[j+1])) { collinear.add(tmp[j+1]); }

                   int k = j + 2;
                   while (k < tmp.length
                           && tmpSlope == tmp[i].slopeTo(tmp[k])) {
                       if (!collinear.contains(tmp[k])) { collinear.add(tmp[k]); }
                       k++;
                   }
               }
           }
           if (collinear.size() >= 4 && !endPoints.contains(collinear.get(collinear.size()-1))) {
               for (Point p : collinear) {
                   StdOut.print(p.toString());
                   if (!p.equals(collinear.get(collinear.size()-1))) {
                       StdOut.print(" -> ");
                   }
               }
               
               StdOut.println();
               collinear.get(0).drawTo(collinear.get(collinear.size()-1));
               endPoints.add(collinear.get(collinear.size()-1));
           }
       }
       
       //for (int i = 0; i < points.length; i++) {
           //Arrays.sort(points, points[i].SLOPE_ORDER);
       //}
       
       //for (int i = 0; i < points.length; i++) {
           //StdOut.println(points[i].toString());
       //}
       
       //for (int p1 = 0; p1 < (points.length - 1); p1++) {
           //int i = 0;
           //double[] slopes = new double[points.length - 1 - p1];
           
           //for (int p2 = p1+1; p2 < points.length; p2++) {
               //slopes[i++] = points[p1].slopeTo(points[p2]);
           //}
           
           //StdOut.println(points[p1].toString() + ":");
           //for (int j = 0; j < slopes.length; j++) {
               //StdOut.println(slopes[j]);
           //}
       //}
   }
}