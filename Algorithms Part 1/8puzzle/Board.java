/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       03/04/2014
 * Last updated:  03/05/2014
 * 03/05/2014 : commented this thing
 * 03/05/2014 : converted 2d array to 1d array to save memory. not trivial...
 *  
 * Board is an immutable data type for use in solving the famous 8 puzzle problem.
 * It stores an N dimensional square board with split into N tiles. Each tile has
 * an int value, and 0 represents the empty space.
 * 
 * Compilation:   javac Board.java
 * Execution: java Board input.txt
 * Dependencies: StdOut.java
 */

public class Board {
    private int N;               // dimension of this board
    private int[] tiles;         // array containing each tile
    
    private int emptyIndex;      // index of the empty tile
    
    private int hammingScore;    // store the hamming score for quicker access
    private int manhattanScore;  // store the manhattan score for quicker access
    
/**
 * construct a board from an N-by-N array of blocks
 * (where blocks[i][j] = block in row i, column j)
 * note that we have to convert the given 2d array to a 1d array
 * @param int[][] d2 int array representing each tile on the board
 */
    public Board(int[][] blocks)
    {
        this.hammingScore   = -1;    // initialize for future recalculation
        this.manhattanScore = -1;    // initialize for future recalculation
        
        // initialize and fill the board
        this.N     = blocks.length;
        this.tiles = new int[N*N];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int newIndex = (i * N) + j;
                tiles[newIndex] = blocks[i][j];
                
                if (tiles[newIndex] == 0) {
                    this.emptyIndex = newIndex;
                }
            }
        }
    }

/**
 * private constructor for constructing a board with a 1d array
 * @param int[] the board to construct
 */
    private Board(int[] blocks)
    {
        this.hammingScore = -1;
        this.manhattanScore = -1;
        
        this.N = (int) Math.sqrt(blocks.length);
        this.tiles = new int[N*N];
        
        for (int i = 0; i < (N*N); i++) {
            tiles[i] = blocks[i];
            
            if (tiles[i] == 0) {
                this.emptyIndex = i;
            }
        }
    }
    
/**
 * helper method swaps two values at the given indices.
 * @param i index of this item
 * @param j index of that item
 * @return false if the swap is impossible (because it would swap off the board)
 *         or true if the swap was successful
 */
    private boolean swap(int i, int j)
    {
        // first verify that the swap is even possible
        // test that index for being out of bounds in the array
        if (j < 0 || j >= (N*N))                       { return false; }
        // then make sure that index is either in the same row or same column
        if ((i / N) != (j / N) && (i % N) != (j % N))  { return false; }
        
        // snag the values at the given indices
        int thisValue = tiles[i];
        int thatValue = tiles[j];

        // if we're swapping the empty tile, swap those instance fields too
        if (thisValue == 0) {
            emptyIndex = j;
        }
        else if (thatValue == 0) {
            emptyIndex = i;
        }
        
        // finally swap the actual values
        tiles[i] = thatValue;
        tiles[j] = thisValue;
        
        return true;
    }
    
/**
 * board dimension N
 * @return the dimension of the board
 */
    public int dimension()
    {
        return N;
    }
    
/**
 * number of blocks out of place
 * @return either the cached hamming score or the newly recalculated score
 */
    public int hamming()
    {
        if (hammingScore >= 0) { return hammingScore; }
        
        // the logic here is to loop through each tile in the board
        // and figure out what int should be in the current tile.
        // if its not correct we add one to the hamming
        hammingScore = 0;
        
        for (int i = 0; i < (N*N); i++) {
            int value = tiles[i];
            
            if (value != 0) {
                int expValue = i + 1;
                
                if (expValue == N * N) { expValue = 0; }
                if (expValue != value) { hammingScore++; }
            }
        }
        
        return hammingScore;
    }
    
/**
 * sum of Manhattan distances between blocks and goal
 * @return either the cached manhattan score or the newly recalculated score
 */
    public int manhattan()
    {
        if (manhattanScore >= 0) { return manhattanScore; }
        
        // the logic here is to loop through each tile on the board
        // and for each value, we find out where that value should be, then
        // calculate how many moves it is away from the correct tile, adding
        // that distance to the running total
        manhattanScore = 0;
        
        for (int i = 0; i < (N*N); i++) {
            int value = tiles[i];
            
            if (value != 0) {
                int expRow = (value - 1) / N;
                int expCol = (value - 1) % N;
                int actualRow = i / N;
                int actualCol = i % N;
                int distance = Math.abs(expRow - actualRow) + Math.abs(expCol - actualCol);
                manhattanScore += distance;
            }
        }
        
        return manhattanScore;
    }
    
/**
 * is this board the goal board?
 * @return true if it is the goal, false if not
 */
    public boolean isGoal()
    {
        // loop through each tile and ensure that the value in that tile
        // is the expected value for the goal board
        for (int i = 0; i < (N*N); i++) {
            int value    = tiles[i];
            int expValue = i + 1;
            
            if (expValue == (N * N)) { expValue = 0; }
            if (expValue != value)   { return false; }
        }
        
        return true;
    }
    
/**
 * a board obtained by exchanging two adjacent blocks in the same row, but not
 * including the empty space
 * @return a valid twin for this board
 */
    public Board twin()
    {
        // simply test if the empty space is in the top row, if not
        // swap those first two values.
        // if it is swap the two in the next row
        Board twin = new Board(tiles);
        
        if (twin.tiles[0] != 0 && twin.tiles[1] != 0) {
            twin.swap(0, 1);
        }
        else {
            twin.swap(N, N+1);
        }
        
        return twin;
    }
    
/**
 * does this board equal y?
 * @param Object the Object (Board) to compare to
 * @return true if they are the same board, false otherwise
 */
    public boolean equals(Object y)
    {
        if (y == this)                       { return true; }
        if (y == null)                       { return false; }
        if (y.getClass() != this.getClass()) { return false; }

        Board that = (Board) y;
        
        // verify the two boards have the same dimension
        if (that.dimension() != this.dimension()) { return false; }
        
        // loop through each tile and see if the values are the same throughout
        for (int i = 0; i < (N*N); i++) {
            if (this.tiles[i] != that.tiles[i]) { return false; }
        }
        
        return true;
    }
    
/**
 * all neighboring boards
 * @return an Iterable object to loop through all the neighbor boards
 */
    public Iterable<Board> neighbors()
    {
        Queue<Board> neighBoards = new Queue<Board>();
        
        Board boardA = new Board(tiles);
        Board boardB = new Board(tiles);
        Board boardC = new Board(tiles);
        Board boardD = new Board(tiles);
        
        if (boardA.swap(emptyIndex, emptyIndex - N)) {
            neighBoards.enqueue(boardA);
        }
        if (boardB.swap(emptyIndex, emptyIndex + N)) {
            neighBoards.enqueue(boardB);
        }
        if (boardC.swap(emptyIndex, emptyIndex - 1)) {
            neighBoards.enqueue(boardC);
        }
        if (boardD.swap(emptyIndex, emptyIndex + 1)) {
            neighBoards.enqueue(boardD);
        }
        
        return neighBoards;
    }
    
/**
 * string representation of the board
 * @return the string representation of the board
 */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(N + "\n");
        
        for (int i = 0; i < (N*N); i++) {
            sb.append(String.format("%2d ", tiles[i]));
            if (((i + 1) % N) == 0) { sb.append("\n"); }
        }
        
        return sb.toString();
    }
    
/**
 * unit testing
 * @param String[] command line arguments
 */
    public static void main(String[] args)
    {
        // read in the initial board from the given file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board initial = new Board(blocks);
        
        StdOut.print(initial);
        StdOut.println("Hamming: " + initial.hamming());
        StdOut.println("Manhattan: " + initial.manhattan());
        
        Board twin = initial.twin();
        StdOut.println();
        StdOut.println(twin);
        //StdOut.println("Hamming: " + twin.hamming());
        //StdOut.println("Manhattan: " + twin.manhattan());
        
        //for (Board b : initial.neighbors()) {
            //StdOut.println();
            //StdOut.print(b.toString());
        //}
    }
}