/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/3/2014
 * Last updated:  2/7/2014
 * 2/7/14 : Fixed a huge bug. I was tracking i, j as the x, y position, but
 *          that was the wrong way to look at it. 
 *          Now I track i, j as row, column.
 * 2/7/14 : Improved percolates(). it is now much faster.
 *  
 * Creates an N X N grid and keeps track of which sites have
 * been opened. Uses the WeightedQuickUnionUF algorithm for keeping track of
 * connected sites.
 * 
 * Compilation:   javac Percolation.java
 * Execution:     n/a
 */
public class Percolation
{
    private int gridSize;                     // size of the grid
    private boolean[][] sites;                // tracks open sites
    private WeightedQuickUnionUF components;  // tracks components
    private int virtualTop;                   // index for the virtual top
    private int virtualBottom;                // index for the virtual bottom

/**
 * Constructor creates a new Percolation grid N X N that is fully blocked
 * @param n int for the size of the grid
 */
    public Percolation(int n)
    {
        // initialize the components. it needs to be sized to hold
        // the entire 2d array of sites as 1d array to make everything speedier
        this.components = new WeightedQuickUnionUF(n * (n + 1) + 1);
        
        // initialize the grid
        this.gridSize = n;
        this.sites    = new boolean[n+1][n+1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                this.sites[i][j] = false;
            }
        }
        
        // virtual top is a virtual top row on top of the actual top row
        // each site in the top row will be connected to the virtual top
        // this will make testing whether the grid percolates much faster
        this.virtualTop = 0;
        for (int i = 1; i <= n; i++) {
            this.components.union(this.virtualTop, translateToOneD(1, i));
        }
        
        // virtual bottom is a virtual bottom row under the actual bottom row
        // note that we don't initially connect each bottom site to the virtual
        // bottom. this is to avoid the backwash bug. i add the bottom row
        // sites to the virtual bottom when they are opened
        // used 1 as the index since its an unused index in the components
        this.virtualBottom = 1;
    }
    
/**
 * boolean method to test if the site at i, j is connected to the top
 * @param i row in the grid
 * @param j column in the grid
 * @return true if its connected to the top, false otherwise
 */
    public boolean isFull(int i, int j)
    {
        // if the site is open and valid, simply check if it is in the same
        // component as the virtual top
        validateIndices(i, j);
        if (isOpen(i, j)) {
            return this.components.connected(this.virtualTop, translateToOneD(i, j));
        }
        
        return false;
    }
    
/**
 * boolean method to test if the site at i, j has been opened
 * @param i row in the grid
 * @param j column in the grid
 * @return true if its open, false if not
 */
    public boolean isOpen(int i, int j)
    {
        validateIndices(i, j);
        return this.sites[i][j];
    }
    
/**
 * boolean method to test if the site at i, j is a valid site and is open
 * this is a separate private method than validateIndices() because
 * we don't necessarily want to throw an exception
 * @param i row in the grid
 * @param j column in the grid
 * @return true if the site is valid and open, false otherwise
 */
    private boolean isValidAndOpen(int i, int j)
    {
        if (i >= 1 && i <= this.gridSize && j >= 1 && j <= this.gridSize) {
            return this.sites[i][j];
        }
        
        return false;
    }
    
/**
 * boolean method to test if the grid percolates (top is connected to the bottom)
 * @return true if the grid percolates, false if not
 */
    public boolean percolates()
    {
        // simply check if the virtual rows are in the same component
        return this.components.connected(this.virtualTop, this.virtualBottom);
    }
    
/**
 * opens the site at i, j
 * @param i row in the grid
 * @param j column in the grid
 */
    public void open(int i, int j)
    {
        // we don't really care if the site has already been opened
        // except it may be a slight bit slower because we go ahead
        // and check each of the adjacent sites, which we could avoid doing
        // if the site were already open
        // but it is still constant time so i live with it
        validateIndices(i, j);
        this.sites[i][j] = true;
            
        if (isValidAndOpen(i - 1, j)) {
            this.components.union(translateToOneD(i, j), translateToOneD(i - 1, j));
        }
        if (isValidAndOpen(i + 1, j)) {
            this.components.union(translateToOneD(i, j), translateToOneD(i + 1, j));
        }
        if (isValidAndOpen(i, j - 1)) {
            this.components.union(translateToOneD(i, j), translateToOneD(i, j - 1));
        }
        if (isValidAndOpen(i, j + 1)) {
            this.components.union(translateToOneD(i, j), translateToOneD(i, j + 1));
        }
        
        if (i == 1) {
            this.components.union(translateToOneD(i, j), this.virtualTop);
        }
        else if (i == this.gridSize) {
            this.components.union(translateToOneD(i, j), this.virtualBottom);
        }
    }

/**
 * takes the given i, j position and returns the index in the one dimensional array
 * @param i row in the grid
 * @param j column in the grid
 * @return the index in the array
 */
    private int translateToOneD(int i, int j)
    {
        return (i * this.gridSize) + j;
    }
    
/**
 * throws an exception if the site at i, j is invalid
 * valid sites are [1, N]
 * @param i row in the grid
 * @param j column in the grid
 */
    private void validateIndices(int i, int j)
    {
        if (i < 1 || i > this.gridSize) {
            throw new IndexOutOfBoundsException("Row " + i + " Out of Bounds.");
        }
        if (j < 1 || j > this.gridSize) {
            throw new IndexOutOfBoundsException("Column " + j + " Out of Bounds.");
        }
    }
}