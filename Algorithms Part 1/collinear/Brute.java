/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/25/2014
 * Last updated:  2/25/2014
 *  
 * Brute takes an input file with a list of points and tests all the
 * points for collinearity, then returns the list of points (4 or more) that
 * are collinear. Plus it draws the points and draws the line that the points
 * lie upon. Its great!
 * 
 * Brute is also not very fast. N^4 which is fairly unacceptable by
 * today's standards. But its memory usage isn't bad, which is nice.
 * 
 * Personal note there may be a flaw. I do presort the array of points in
 * order to make outputting them lexographically much easier. This may not
 * be fast enough.
 * 
 * Compilation:   javac Brute.java
 * Execution: java Brute input.txt
 * Dependencies: StdOut.java
 */

import java.util.Arrays;

public class Brute {
    
/**
 * main program logic
 * @param String[] the command line arguments
 */
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
       
       // sort the points by natural ordering
       Arrays.sort(points);
       
       // this is a bit heinous.
       // we're looping through all the points, comparing all possible
       // combinations, checking for collinearity (if they all have the same slope
       // with the first point, they're in a line!)
       for (int p1 = 0; p1 < (points.length - 3); p1++) {
           for (int p2 = p1+1; p2 < (points.length - 2); p2++) {
               for (int p3 = p2+1; p3 < (points.length - 1); p3++) {
                   // do a quick check here. if the first 3 points aren't collinear
                   // there's no point in looping to check the 4th points.
                   if (points[p1].slopeTo(points[p2]) == points[p1].slopeTo(points[p3])) {
                       for (int p4 = p3+1; p4 < points.length; p4++) {
                           
                           if (points[p1].slopeTo(points[p2]) == points[p1].slopeTo(points[p4])) {
                               StdOut.println(points[p1].toString() + " -> "
                                            + points[p2].toString() + " -> "
                                            + points[p3].toString() + " -> "
                                            + points[p4].toString());
                               
                               points[p1].drawTo(points[p4]);
                           }
                       }
                   }
               }
           }
       }
   }
}
