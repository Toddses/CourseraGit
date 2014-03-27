/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       03/07/2014
 * Last updated:  
 *  
 * PointSET uses a SET datatype (which is a red-black BST) to construct a set
 * of points of type Point2D. PointSET uses a brute force algorithm to find 
 * the nearest point to a given point or find all points within a given 
 * rectangle of type RectHV. Its pretty great.
 * 
 * Compilation:   javac PointSET.java
 * Execution: java PointSET
 * Dependencies: SET.java, Point2D.java, RectHV.java
 */

public class PointSET {
    private SET<Point2D> points;     // the tree of points
    
/**
 * construct an empty set of points
 */
    public PointSET()
    {
        this.points = new SET<Point2D>();
    }
    
/**
 * is the set empty?
 * @return true if it is empty, false otherwise
 */
    public boolean isEmpty()
    {
        return points.isEmpty();
    }

/**
 * number of points in the set
 * @return the number of points in the tree
 */
    public int size()
    {
        return points.size();
    }
    
/**
 * add the point p to the set (if it is not already in the set)
 * @param Point2D the point to add
 * @throws NullPointerException if the given point is null
 */
    public void insert(Point2D p)
    {
        if (p == null) { throw new NullPointerException("called insert() with a null key"); }
        points.add(p);
    }
    
/**
 * does the set contain the point p?
 * @return true if the given point is in the set, false otherwise
 * @throws NullPointException if the given point is null
 */
    public boolean contains(Point2D p)
    {
        if (p == null) { throw new NullPointerException("called contains() with a null key"); }
        return points.contains(p);
    }
    
/**
 * draw all of the points to standard draw
 * @throws NullPointerException if the tree is empty
 */
    public void draw()
    {
        if (isEmpty()) { throw new NullPointerException("called draw() on an empty tree"); }
        for (Point2D p : points) {
            p.draw();
        }
    }
    
/**
 * all points in the set that are inside the rectangle
 * @return an Iterable object containing all the points inside the given rectangle
 * @throws NullPointerException if the given rectangle is null
 * @throws NullPointerException if the tree is empty
 */
    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null) { throw new NullPointerException("called range() with a null RectHV"); }
        if (isEmpty())    { throw new NullPointerException("called range() on an empty tree"); }
        
        Queue<Point2D> q = new Queue<Point2D>();
        
        for (Point2D currentPoint : points) {
            if (rect.contains(currentPoint)) {
                q.enqueue(currentPoint);
            }
        }
        
        return q;
    }
    
/**
 * a nearest neighbor in the set to p; null if set is empty
 * @return the closest point to the given point
 * @throws NullPointerException if the given point is null
 * @throws NullPointerException if the tree is empty
 */
    public Point2D nearest(Point2D p)
    {
        if (p == null) { throw new NullPointerException("called nearest() with a null key"); }
        if (isEmpty()) { throw new NullPointerException("called nearest() on an empty tree"); }
        
        Point2D nearest = points.min();
        double dSquared = p.distanceTo(nearest);
        
        for (Point2D currentPoint : points) {
            if (p.distanceSquaredTo(currentPoint) < dSquared) {
                nearest = currentPoint;
                dSquared = p.distanceSquaredTo(currentPoint);
            }
        }
        
        return nearest;
    }
    
/**
 * unit testing
 */
    public static void main(String[] args)
    {
        PointSET ps = new PointSET();
        ps.insert(new Point2D(0.1, 0.1));
        ps.insert(new Point2D(0.2, 0.1));
        ps.insert(new Point2D(0.3, 0.1));
        ps.insert(new Point2D(0.25, 0.15));
        ps.insert(new Point2D(0.6, 0.6));
        ps.insert(new Point2D(0.6, 0.7));
        
        if (ps.contains(new Point2D(0.2, 0.21))) {
            StdOut.println("Contains");
        }
        else {
            StdOut.println("Doesn't contain");
        }
        
        StdOut.println(ps.size());
        
        StdDraw.setPenRadius(.03);
        ps.draw();
        
    }
}