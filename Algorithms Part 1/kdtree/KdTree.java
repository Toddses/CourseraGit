/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       03/07/2014
 * Last updated:  03/12/2014
 *  
 * KdTree is a mutable object for efficiently storing and searching points in
 * a 2d plane. Orientation of each node determines which coordinates to compare,
 * either x or y.
 * 
 * Compilation:   javac KdTree.java
 * Execution: java KdTree
 * Dependencies: Point2D.java, RectHV.java
 */

public class KdTree {
    
    // instance variables
    private Node root;   // pointer to the root node in the tree
    private int size;    // number of nodes in the tree
    
    // helper class to represent a node in the tree
    private class Node {
        private Point2D p;    // the point this node represents
        private RectHV rect;  // the rectangle created by this node
        private Node left;    // the left/bottom child
        private Node right;   // the right/top child
        
        private Node(Point2D p, RectHV rect, Node left, Node right)
        {
            this.p  = p;
            this.rect = rect;
            this.left = left;
            this.right = right;
        }
    }
    
/**
 * construct an empty set of points
 */
    public KdTree()
    {
        this.root = null;
        this.size = 0;
    }
    
/**
 * is the set empty?
 * @return true if its empty, false if not
 */
    public boolean isEmpty()
    {
        return size == 0;
    }

/**
 * number of points in the set
 * @return number of points in the tree
 */
    public int size()
    {
        return size;
    }
    
/**
 * add the point p to the set (if it is not already in the set)
 * orienation is a simple boolean value, true for vertical and false for horizontal
 * we just flip it for each level of the tree
 * @param Point2D the point to insert
 */
    public void insert(Point2D p)
    {
        if (p == null)    { throw new NullPointerException("called insert() with a null key"); }
        if (!contains(p)) { root = insert(root, null, p, true); }
    }
    
    // this is where the real insertion magic happens
    // recursively search the tree for the correct open spot and then return
    // the nodes all the way back up to the root
    private Node insert(Node n, Node parent, Point2D p, boolean orientation)
    {
        // if the node is null we know we've reached our insertion spot,
        // so build the rectangle, create a new Node, and return it
        if (n == null) {
            RectHV r = null;
            
            // if there was no parent, we are at the root node
            if (parent == null) {
                r = new RectHV(0, 0, 1, 1);
            }
            else {
                // start with the parent's rectangle
                double xmin = parent.rect.xmin();
                double xmax = parent.rect.xmax();
                double ymin = parent.rect.ymin();
                double ymax = parent.rect.ymax();
                
                // adjust the rectangle depending on orientation and
                // the position of the new point
                if (orientation) {
                    if (p.y() < parent.p.y()) { ymax = parent.p.y(); }
                    else                      { ymin = parent.p.y(); }
                }
                else {
                    if (p.x() < parent.p.x()) { xmax = parent.p.x(); }
                    else                      { xmin = parent.p.x(); }
                }
                
                r = new RectHV(xmin, ymin, xmax, ymax);
            }
            
            size++;
            return new Node(p, r, null, null);
        }
        // if we haven't found spot for the new node, continue on down the tree,
        // based on the position of the new point
        else {
            boolean isCoordLessThan = false;
            if (orientation) { isCoordLessThan = p.x() < n.p.x(); }
            else             { isCoordLessThan = p.y() < n.p.y(); }
            
            if (isCoordLessThan) { n.left = insert(n.left, n, p, !orientation); }
            else                 { n.right = insert(n.right, n, p, !orientation); }
            
            return n;
        }
    }
    
/**
 * does the set contain the point p?
 * @return true if the given point is in the tree, false otherwise
 * @throws NullPointerException if the given point is null
 */
    public boolean contains(Point2D p)
    {
        if (p == null) { throw new NullPointerException("called contains() with a null key"); }
        
        return contains(root, p, true);
    }
    
    // where the real contains() magic happens
    // recursively search the tree, based on the position of the given point,
    // and return true all the way back up if we find it
    private boolean contains(Node n, Point2D p, boolean orientation)
    {
        if (n == null)          { return false; }
        if (n.p.equals(p))      { return true; }
        
        boolean isCoordLessThan = false;
        if (orientation) { isCoordLessThan = p.x() < n.p.x(); }
        else             { isCoordLessThan = p.y() < n.p.y(); }
        
        if (isCoordLessThan) { return contains(n.left, p, !orientation); }
        else                 { return contains(n.right, p, !orientation); }
    }
    
/**
 * draw all of the points and intersection lines to standard draw
 */
    public void draw()
    {
        if (isEmpty()) { throw new NullPointerException("called draw() on an empty tree"); }
        
        root.rect.draw();
        draw(root, true);
    }
    
    // where the real draw() action happens
    // recursively draw all the points and lines
    private void draw(Node n, boolean orientation)
    {
        if (n != null) {
            
            StdDraw.setPenRadius();
            if (orientation) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
            }
            else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
            }

            StdDraw.setPenRadius(.01);
            StdDraw.setPenColor(StdDraw.BLACK);
            n.p.draw();
            
            draw(n.left, !orientation);
            draw(n.right, !orientation);
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
        //if (isEmpty())    { return null; }
        
        Queue<Point2D> points = new Queue<Point2D>();
        return range(root, rect, points);
    }
    
    // the magic of range()
    // fairly simple recursive method to search the tree for any points
    // that lie within the query rectangle.
    // prune the search tree by checking if the query rectangle intersects with
    // the current node's rectangle. if they don't, no need to search that
    // subtree
    private Queue<Point2D> range(Node n, RectHV rect, Queue<Point2D> points)
    {
        if (n == null)                { return points; }
        if (!rect.intersects(n.rect)) { return points; }
        
        if (rect.contains(n.p)) { points.enqueue(n.p); }
        
        range(n.left, rect, points);
        range(n.right, rect, points);
        return points;
    }
    
/**
 * a nearest neighbor in the set to p; null if set is empty
 * @return the point closest to the given point
 * @throws NullPointerException if the given point is null
 * @throws NullPointerException if the tree is empty
 */
    public Point2D nearest(Point2D p)
    {
        if (p == null) { throw new NullPointerException("called nearest() with a null key"); }
        if (isEmpty()) { return null; }
        
        Node closest = root;
        return nearest(root, closest, p, true).p;
    }

    // the real magic of nearest()
    // recursively search the tree. we can prune subtrees by checking if the
    // node's rectangle is closer than the current minimum distance
    // if it isn't, we don't need to search that subtree
    private Node nearest(Node n, Node closest, Point2D p, boolean orientation)
    {
        Node c = closest;
        
        if (n == null) { return c; }
        
        double minDistance  = p.distanceSquaredTo(c.p);
        double thisDistance = p.distanceSquaredTo(n.p);
        
        if (n.rect.distanceSquaredTo(p) >= minDistance) { return c; }
        
        if (thisDistance < minDistance) { c = n; }
        
        // we choose the point that is on the same side of the splitting line
        // to search first, as it will help prune more parts of the tree
        boolean isCoordLessThan = false;
        if (orientation) { isCoordLessThan = p.x() < n.p.x(); }
        else             { isCoordLessThan = p.y() < n.p.y(); }
        
        if (isCoordLessThan) {
            c = nearest(n.left, c, p, !orientation);
            c = nearest(n.right, c, p, !orientation);
        }
        else {
            c = nearest(n.right, c, p, !orientation);
            c = nearest(n.left, c, p, !orientation);
        }
        
        return c;
    }
    
/**
 * unit testing
 */
    public static void main(String[] args)
    {
        KdTree kd = new KdTree();
        kd.insert(new Point2D(0.1, 0.1));
        kd.insert(new Point2D(0.2, 0.2));
        kd.insert(new Point2D(0.3, 0.3));
        kd.insert(new Point2D(0.25, 0.15));
        kd.insert(new Point2D(0.6, 0.6));
        kd.insert(new Point2D(0.8, 0.7));
        kd.insert(new Point2D(.05, .02));
        
        if (kd.contains(new Point2D(0.2, 0.1))) {
            StdOut.println("contains");
        }
        
        /*String filename = args[0];
        In in = new In(filename);

        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        
        kdtree.draw();
        
        Point2D p = new Point2D(0.25, 0.45);
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setPenRadius(0.01);
        p.draw();
        StdDraw.setPenColor(StdDraw.RED);
        kdtree.nearest(p).draw();*/
    }
}