/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       03/05/2014
 * Last updated:  
 *  
 * Solver uses the immutable data type Board to run the A* algorithm on the
 * 8 puzzle problem. We keep track of a twin board and simultaneously solve
 * both the given board and its twin. this is to quickly assess whether a board
 * is solvable - either the board is solvable or its twin is, but not both.
 * 
 * Compilation:   javac Solver.java
 * Execution: java Solver input.txt
 * Dependencies: Board.java, StdOut.java
 */

import java.util.Comparator;

public class Solver {

    private SearchNode goal;            // link to the goal board
    private boolean solvableTwin;       // does the initial board have a solvable twin?
    private MinPQ<SearchNode> pq;       // priority queue for quickly finding the mininum value
    private MinPQ<SearchNode> twinpq;   // priority queue for the twin board
    
    // inner class SearchNode for storing a board and a link to its parent
    private class SearchNode
    {
        private Board board;
        private SearchNode parent;
        private int moves;
        
        private SearchNode(Board b, SearchNode node, int m)
        {
            board  = b;
            parent = node;
            moves = m;
        }
    }
    
    // comparator for the priority queue. sort based on manhattan score.
    private final Comparator<SearchNode> MANHATTAN_PRIORITY = new Comparator<SearchNode>() {
        public int compare(SearchNode node1, SearchNode node2)
        {
            int score1 = node1.board.manhattan() + node1.moves;
            int score2 = node2.board.manhattan() + node2.moves;
            
            if      (score1 < score2)           { return -1; }
            else if (score1 > score2)           { return 1; }
            else                                { return 0; }
        }
    };
    
/**
 * find a solution to the initial board (using the A* algorithm)
 * @param Board the initial board to solve
 */
    public Solver(Board initial)
    {
        // initialize the instance fields
        // set up the queues to sort based on manhattan score
        this.solvableTwin = false;
        this.goal         = null;
        this.pq           = new MinPQ<SearchNode>(MANHATTAN_PRIORITY);
        this.twinpq       = new MinPQ<SearchNode>(MANHATTAN_PRIORITY);
        
        // initialize the current SearchNode
        SearchNode current     = null;
        SearchNode currentTwin = null;
        // get things started by inserting the initial board
        pq.insert(new SearchNode(initial, null, 0));
        twinpq.insert(new SearchNode(initial.twin(), null, 0));
        
        while (!pq.isEmpty()) {
            current     = pq.delMin();         // pop off the lowest priority SearchNode
            currentTwin = twinpq.delMin();
            
            // if its the goal we got what we need, bail out of the loop
            if (current.board.isGoal()) {
                goal = current;
                break;
            }
            else if (currentTwin.board.isGoal()) {
               solvableTwin = true;
                break;
            }
            // otherwise we need to increment moves and insert all of the 
            // current node's neighbors to the queue.
            else {
                for (Board b : current.board.neighbors()) {
                    if (current.parent == null) {
                        SearchNode node = new SearchNode(b, current, current.moves+1);
                        pq.insert(node);
                    }
                    else if (!b.equals(current.parent.board)) {
                        SearchNode node = new SearchNode(b, current, current.moves+1);
                        pq.insert(node);
                    }
                }
                for (Board b : currentTwin.board.neighbors()) {
                    if (currentTwin.parent == null) {
                        SearchNode node = new SearchNode(b, current, currentTwin.moves+1);
                        twinpq.insert(node);
                    }
                    else if (!b.equals(currentTwin.parent.board)) {
                        SearchNode node = new SearchNode(b, currentTwin, currentTwin.moves+1);
                        twinpq.insert(node);
                    }
                }
            }
        }
    }
    
/**
 * is the initial board solvable?
 * @return true if its solvable, false if not
 */
    public boolean isSolvable()
    {
        return !solvableTwin;
    }
    
/**
 * min number of moves to solve initial board; -1 if no solution
 * @return number of moves made to reach the solution
 */
    public int moves()
    {
        // make sure the puzzle is actually solvable
        if (isSolvable()) { return goal.moves; }
        else              { return -1; }
    }
    
/**
 * sequence of boards in a shortest solution; null if no solution
 * @return an Iterable object to loop through the solution chain
 */
    public Iterable<Board> solution()
    {
        // make sure the puzzle is solvable
        if (!isSolvable()) { return null; }
        
        // we're traversing backwards so we want LIFO functionality and
        // a Stack gives us just that
        Stack<Board> solution = new Stack<Board>();
        
        // follow the chain of parents until the end
        SearchNode current = goal;
        while (current.parent != null) {
            solution.push(current.board);
            current = current.parent;
        }
        
        // need to be sure to push the last board onto the Stack
        solution.push(current.board);
        
        return solution;
    }
    
/**
 * solve a slider puzzle
 */
    public static void main(String[] args)
    {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}