import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/12/2014
 * Last updated:  2/12/2014
 *  
 * Randomized Queue handles a set of unsorted objects. It uses a resizing array
 * to store the data. Items are popped off the list randomly. The iterator
 * shuffles all the objects into a random order for iteration.
 * 
 * Unit testing uses an Integer command line argument for the size of the queue
 * 
 * Compilation:   javac RandomizedQueue.java
 * Execution:     java RandomizedQueue N
 */

public class RandomizedQueue<Item> implements Iterable<Item>
{
    private int numItems;  // number of items in the queue
    private Item[] queue;  // the queue of items, a resizing array
    
/**
 * construct an empty randomized queue
 */
    public RandomizedQueue()
    {
        // initialize the queue as an array of size 2, will be resized dynamically
        numItems = 0;
        queue    = (Item[]) new Object[2];
    }
    
/**
 * is the queue empty?
 * @return true if its empty, false otherwise
 */
    public boolean isEmpty()
    {
        return numItems == 0;
    }
    
/**
 * return the number of items on the queue
 * @return number of items in the queue
 */
    public int size()
    {
        return numItems;
    }
    
/**
 * private method to resize the queue
 * @param int new size of the queue
 */
    private void resize(int capacity)
    {
        // initialize an array, sized to the new capacity
        Item[] newQueue = (Item[]) new Object[capacity];
        
        // copy over all the contents of the old queue into the new queue
        for (int i = 0; i < numItems; i++) {
            newQueue[i] = this.queue[i];
        }
        
        this.queue = newQueue;
    }
    
/**
 * add the item
 * @param Item the item to add to the queue
 * @throws NullPointerException if client tries to add null
 */
    public void enqueue(Item item)
    {
        if (item == null) {
            throw new NullPointerException("Cannot add null to RandomizedQueue.");
        }
        
        // resize the array if it is full, just double the size
        if (numItems == queue.length) {
            resize(queue.length * 2);
        }
        
        // put in the new item and increment the number of items
        queue[numItems++] = item;
    }
    
/**
 * delete and return a random item
 * @return the randomly selected item
 * @throws NoSuchElementException if queue is empty
 */
    public Item dequeue()
    {
        if (isEmpty()) {
            throw new NoSuchElementException("RandomizedQueue underflow.");
        }
        
        // pick a random number and get the item from the queue
        int randIndex = StdRandom.uniform(numItems);
        Item item     = queue[randIndex];
        
        // had a clever idea to keep runtime constant, since we don't care about
        // order simply switch the data at the random index with the data in the
        // last position. takes one array access and is much better than
        // pushing each item in the array forward an index
        queue[randIndex]  = queue[numItems-1];
        queue[--numItems] = null;  // handles loitering
        
        // when the queue is 1/4 full, resize the array to half its size
        if (numItems > 0 && numItems == queue.length / 4) {
            resize(queue.length / 2);
        }
        
        return item;
    }
    
/**
 * return (but do not delete) a random item
 * @return the randomly selected item
 * @throws NoSuchElementException if queue is empty
 */
    public Item sample()
    {
        if (isEmpty()) {
            throw new NoSuchElementException("RandomizedQueue underflow.");
        }
        
        // just pick a random index and return the item
        return queue[StdRandom.uniform(numItems)];
    }
    
/**
 * return an independent iterator over items in random order
 * @return the iterator
 */
    public Iterator<Item> iterator()
    {
        return new RandomQueueIterator();
    }
    
    // an iterator, doesn't implement remove() since it's optional
    private class RandomQueueIterator implements Iterator<Item> {
        private int current;      // current index in this iterator
        private Item[] tmpQueue;  // temporary array used in the iterator
        
        // initialize the iterator
        // will simply copy all the contents of the queue into the temp array
        // then shuffle that data using StdRandom
        public RandomQueueIterator()
        {
            current  = 0;
            tmpQueue = (Item[]) new Object[numItems];
            
            for (int i = 0; i < numItems; i++) {
                tmpQueue[i] = queue[i];
            }
            
            StdRandom.shuffle(tmpQueue);
        }
        
        // true if it has a next node, false if not
        public boolean hasNext()
        {
            return current != tmpQueue.length;
        }
        
        // this iterator doesn't support remove
        public void remove()
        {
            throw new UnsupportedOperationException("Cannot call remove in RandomizedQueue.");
        }

        // return the current item, and adjust current to the next node
        public Item next() {
            if (!hasNext())
            {
                throw new NoSuchElementException("No more elements in RandomizedQueue.");
            }
            
            return tmpQueue[current++];
        }
    }
    
/**
 * unit testing
 * @param args[] command line arguments
 */
    public static void main(String[] args)
    {
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        
        // if the command line args aren't right, just set the sample to 10
        int sampleSize = 0;
        if (args.length != 1) { sampleSize = 10; }
        else                  { sampleSize = Integer.parseInt(args[0]); }
        

        // load up a RandomizedQueue with the given # of ints
        for (int i = 0; i < sampleSize; i++) {
            rq.enqueue(i);
        }
        
        // run a random sampling of the queue
        for (int i = 0; i < sampleSize; i++) {
            StdOut.print(rq.sample() + ", ");
        }
        StdOut.println();
        StdOut.println("Size: " + rq.size());
        
        //test the iterator
        StdOut.println("Iterator: ");
        for (int i : rq) {
            StdOut.print(i + ": ");
            for (int j : rq) {
                StdOut.print(j + ", ");
            }
            StdOut.println();
        }
        StdOut.println();
        
        // dequeue half the items
        for (int i = 0; i < sampleSize / 2; i++) {
            StdOut.print(rq.dequeue() + ", ");
        }
        StdOut.println();
        StdOut.println("Size: " + rq.size());
        
        // dequeue the other half of items
        for (int i = 0; i < sampleSize / 2; i++) {
            StdOut.print(rq.dequeue() + ", ");
        }
        StdOut.println();
        StdOut.println("Size: " + rq.size());
        
        // enqueue another sampling
        for (int i = 0; i < sampleSize / 4; i++) {
            rq.enqueue(i);
        }
        StdOut.println("Size: " + rq.size());
        
        // dequeue again
        for (int i = 0; i < sampleSize / 4; i++) {
            StdOut.print(rq.dequeue() + ", ");
        }
        StdOut.println();
        StdOut.println("Size: " + rq.size());
    }
}