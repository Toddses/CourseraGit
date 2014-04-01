/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       03/30/2014
 * Last updated:  03/30/2014
 *  
 * SAP is an immutable object that takes a Digraph and performs shortest
 * ancestral path computations. SAP computes the distance between two vertices,
 * as well as finds their common ancestor. Can also deal with Iterable lists of vertices.
 * 
 * Compilation:   javac SAP.java
 * Execution: java SAP filename.txt
 * Dependencies: java.util.ArrayList, BreadthFirstDirectedPaths.java, Digraph.java, In.java,
 *               StdIn.java, StdOut.java
 */

import java.util.ArrayList;

public class SAP {
    
    private Digraph dG;       // local reference to the Digraph
    
    private int minLength;    // shortest path
    private int minAncestor;  // shortest path's common ancestor
    
/**
 * constructor takes a digraph (not necessarily a DAG)
 * @param Digraph valid directed graph
 */
    public SAP(Digraph G)
    {
        this.minLength   = -1;    // initialize the caches
        this.minAncestor = -1;
        
        // Digraph is mutable, so must create a deep copy to maintain immutability in SAP.
        this.dG = new Digraph(G);
    }

/**
 * helper function to calculate the shortest ancestral path and the common
 * ancestor and cache the results
 * @param int first vertex
 * @param int second vertex
 */
    private void findSAP(int v, int w)
    {
        // perform a breadth first search for each parameter.
        BreadthFirstDirectedPaths bfv = new BreadthFirstDirectedPaths(dG, v);
        BreadthFirstDirectedPaths bfw = new BreadthFirstDirectedPaths(dG, w);

        int min = -1;
        int anc = -1;
        
        // loop through each vertex in the Digraph.
        // if the current vertex has a valid path to both of the parameters,
        // calculate the distance and check if it is less than the current
        // min path.
        for (int i = 0; i < dG.V(); i++) {
            if (bfv.hasPathTo(i) && bfw.hasPathTo(i)) {
                int curLength = bfv.distTo(i) + bfw.distTo(i);
                if (curLength < min || min == -1) {
                    min = curLength;
                    anc = i;
                }
            }
        }
        
        // if we didn't find a path, set the cache to -1 as per problem spec.
        if (min == -1) {
            minLength   = -1;
            minAncestor = -1;
        }
        // otherwise add the path and common ancestor to the cache.
        else {
            minLength   = min;
            minAncestor = anc;
        }
    }
    
/**
 * helper function to calculate the shortest ancestral path and the common
 * ancestor in a list of sources
 * @param Iterable<Integer> first vertex list
 * @param Iterable<Integer> second vertex list
 */
    private void findSAP(Iterable<Integer> vList, Iterable<Integer> wList)
    {
        // perform a breadth first search for each parameter list.
        BreadthFirstDirectedPaths bfv = new BreadthFirstDirectedPaths(dG, vList);
        BreadthFirstDirectedPaths bfw = new BreadthFirstDirectedPaths(dG, wList);
        
        int min = -1;
        int anc = -1;
        
        // loop through each vertex in the Digraph.
        // if the current vertex has a valid path to at least one vertex in each list,
        // calculate the distance and check if it is less than the current
        // min path.
        for (int i = 0; i < dG.V(); i++) {
            if (bfv.hasPathTo(i) && bfw.hasPathTo(i)) {
                int curLength = bfv.distTo(i) + bfw.distTo(i);
                if (curLength < min || min == -1) {
                    min = curLength;
                    anc = i;
                }
            }
        }
        
        // if we didn't find a path, set the cache to -1 as per problem spec.
        if (min == -1) {
            minLength   = -1;
            minAncestor = -1;
        }
        // otherwise add the path and common ancestor to the cache.
        else {
            minLength   = min;
            minAncestor = anc;
        }
    }
    
/**
 * length of shortest ancestral path between v and w; -1 if no such path
 * @param int first vertex
 * @param int second vertex
 */
    public int length(int v, int w)
    {
        // validate parameters
        if (v < 0 || w < 0 || v > (dG.V() - 1) || w > (dG.V() - 1))
            throw new IndexOutOfBoundsException("length(): invalid vertex");
        
        findSAP(v, w);
        return minLength;
    }

/**
 * a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
 * @param int first vertex
 * @param int second vertex
 */
    public int ancestor(int v, int w)
    {
        // validate parameters
        if (v < 0 || w < 0 || v > (dG.V() - 1) || w > (dG.V() - 1))
            throw new IndexOutOfBoundsException("ancestor(): invalid vertex");
        
        findSAP(v, w);
        return minAncestor;
    }

/**
 * a common ancestor that participates in shortest ancestral path; -1 if no such path
 * @param Iterable<Integer> first vertex list
 * @param Iterable<Integer> second vertex list
 */
    public int length(Iterable<Integer> v, Iterable<Integer> w)
    {
        // validate both parameter lists
        for (int i : v) {
            if (i < 0 || i > (dG.V() - 1))
                throw new IndexOutOfBoundsException("length(): input contains an invalid vertex");
        }
        for (int i : w) {
            if (i < 0 || i > (dG.V() - 1))
                throw new IndexOutOfBoundsException("length(): input contains an invalid vertex");
        }
        
        findSAP(v, w);
        return minLength;
    }

/**
 * a common ancestor that participates in shortest ancestral path; -1 if no such path
 * @param Iterable<Integer> first vertex list
 * @param Iterable<Integer> second vertex list
 */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
    {
        // validate both parameter lists
        for (int i : v) {
            if (i < 0 || i > (dG.V() - 1))
                throw new IndexOutOfBoundsException("ancestor(): input contains an invalid vertex");
        }
        for (int i : w) {
            if (i < 0 || i > (dG.V() - 1))
                throw new IndexOutOfBoundsException("ancestor(): input contains an invalid vertex");
        }
        
        findSAP(v, w);
        return minAncestor;
    }

/**
 * for unit testing of this class
 */
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        //while (!StdIn.isEmpty()) {
            //int v = StdIn.readInt();
            //int w = StdIn.readInt();
            //int length   = sap.length(v, w);
            //int ancestor = sap.ancestor(v, w);
            //StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        //}
        
        ArrayList<Integer> v = new ArrayList<Integer>();
        ArrayList<Integer> w = new ArrayList<Integer>();
        while (!StdIn.isEmpty()) {
            v.add(StdIn.readInt());
            w.add(StdIn.readInt());
        }
        
        int length = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
}