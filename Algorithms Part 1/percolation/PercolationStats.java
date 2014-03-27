/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/5/2014
 * Last updated:  2/6/2014
 *  
 * Performs the Monte Carlo experiment. Given a grid size, N, and a number
 * of experiments to perform, T, we can estimate the percentage of sites that
 * need to open in order to be confident that a grid will have percolation.
 * 
 * Compilation:   javac PercolationStats.java
 * Execution:     java PercolationStats N T
 */
public class PercolationStats
{
    private int gridSize;        // size of the grid, aka N
    private int numExps;         // number of experiments to run, aka T
    private double[] results;    // holds the results of each experiment, size will be T
    
/**
 * perform T independent computational experiments on an N-by-N grid
 * @param n the grid size
 * @param t the number of experiments to perform
 */
    public PercolationStats(int n, int t)
    {
        // make sure everything is valid
        if (n <= 0) {
            throw new IllegalArgumentException("Grid Size must be greater than 0.");
        }
        if (t <= 0) {
            throw new IllegalArgumentException("Number of Experiments must be greater than 0.");
        }
        
        // initialize all our fields
        this.gridSize  = n;
        this.numExps   = t;
        this.results = new double[t];
        
        // only used in the constructor
        int totalSites = n * n;          
        
        for (int i = 0; i < this.numExps; i++) {
            // initialize a Percolation grid for this experiment
            Percolation perc = new Percolation(gridSize);
            
            // count the number of sites that are open when the grid percolates
            int count = 0;
            
            // open random sites until the grid percolates
            while (!perc.percolates()) {
                int x = StdRandom.uniform(1, this.gridSize + 1);
                int y = StdRandom.uniform(1, this.gridSize + 1);
            
                // we have to check if the site is open first in order
                // to properly track how many sites have been opened
                if (!perc.isOpen(x, y)) {
                    perc.open(x, y);
                    count++;
                }
            }
            
            // result is the percentage of sites opened when it percolates
            this.results[i] = (double) count / totalSites;
        }
    }

/**
 * returns lower bound of the 95% confidence interval
 * @return the lower bound
 */
    public double confidenceLo()
    {
        // math
        // save the mean and stddev in variables for readibility
        double avg  = mean();
        double sdev = stddev();
        return avg - ((1.96 * sdev) / Math.sqrt(this.numExps));
    }
   
/**
 * returns upper bound of the 95% confidence interval
 * @return the upper bound
 */
    public double confidenceHi()
    {
        // math
        // save the mean and stddev in variables for readibility
        double avg  = mean();
        double sdev = stddev();
        return avg + ((1.96 * sdev) / Math.sqrt(this.numExps));
    }
   
/**
 * sample mean of percolation threshold
 * @return mean value
 */
    public double mean()
    {
        return StdStats.mean(this.results);
    }
   
/**
 * sample standard deviation of percolation threshold
 * @return standard deviation value
 */
    public double stddev()
    {
        return StdStats.stddev(this.results);
    }

/**
 * Main method
 * @param args[] command line arguments
 */
    public static void main(String[] args)
    {
        // note i don't explicitly test whether the arguments are ints
        // we get that from parseInt(), it throws an exception if its not an int
        if (args.length != 2) {
            StdOut.println("Fail. Requires 2 integer command line arguments.");
        }
        else {
            int n = Integer.parseInt(args[0]);
            int t = Integer.parseInt(args[1]);
            
            // Stopwatch was used to get data on runtime
            //Stopwatch sw = new Stopwatch();
            
            PercolationStats percStats = new PercolationStats(n, t);
            StdOut.printf("mean                    = %f%n", percStats.mean());
            StdOut.printf("stddev                  = %f%n", percStats.stddev());
            StdOut.printf("95%% confidence interval = %f, %f%n", 
                          percStats.confidenceLo(), 
                          percStats.confidenceHi());
            
            //StdOut.println("Runtime: " + sw.elapsedTime());
        }
    }
}