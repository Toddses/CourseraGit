/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/12/2014
 * Last updated:  2/12/2014
 *  
 * This class only contains a static main method. It takes a command line 
 * argument, k, and takes in N Strings from StdIn. Then 
 * Subset randomly chooses k Strings and prints them on StdOut.
 * 
 * Compilation:   javac Subset.java
 * Execution:     java Subset k
 *                echo AA BB CC DD | java Subset k
 *                java Subset 2 < input.txt
 * 
 */

public class Subset
{
    // the first pass at Subset is a simple solution, using the
    // RandomizedQueue Object. It basically handles all of the
    // functionality. So we just read in all the strings 1 at a time and 
    // throw them into the RandomizedQueue. Then dequeue k Strings
    // from the queue. Simple.
    // The queue will be max size N. Trying to think of how to make the queue
    // max size k...
    public static void main(String[] args)
    {
        int k                      = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        
        while (!StdIn.isEmpty()) {
            rq.enqueue(StdIn.readString());
        }
        
        for (int i = 0; i < k; i++) {
            StdOut.println(rq.dequeue());
        }
    }
}