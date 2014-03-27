import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       2/10/2014
 * Last updated:  2/12/2014
 * 2/12/2014 : No real functional changes, just cleaned up some code and comments.
 *  
 * Deque is a double-ended queue. This is a special queue that can be pushed
 * and popped from either the front or the end of the queue. This implementation
 * is generic and will work with any object. It also implements the Iterable
 * interface and allows the client to iterate through all the objects from the
 * front of the list to the end. I used a doubly-linked list to accomplish
 * constant-time access and minimal memory usage.
 * 
 * Unit testing uses an Integer command line argument for the size of the queue
 * 
 * Compilation:   javac Deque.java
 * Execution:     java Deque N
 */

public class Deque<Item> implements Iterable<Item>
{
    private int numItems;   // number of items currently in the list
    private Node sentinel;  // virtual node to point to the first and last nodes
    
    // helper class, a doubly-linked list
    private class Node {
        // sentinel.next will point to the first node, and sentinel.prev will
        // point to the last node
        private Item item;  // the object that's being stored
        private Node next;  // next node in the list
        private Node prev;  // previous node in the list
    }
    
/**
 * construct an empty deque
 */
    public Deque()
    {
        numItems = 0;
        
        // initialize the sentinel node as an empty list pointing to itself
        sentinel      = new Node();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        sentinel.item = null;
    }
   
/**
 * is the deque empty?
 * @return true if the list has 0 items, false otherwise
 */
    public boolean isEmpty()
    {
        return numItems == 0;
    }
    
/**
 * return the number of items on the deque
 * @return number of items in the deque
 */
    public int size()
    {
        return numItems;
    }
    
/**
 * insert the item at the front
 * @param Item the item to add
 * @throws NullPointerException if attempt to add null to the deque
 */
    public void addFirst(Item item)
    {
        if (item == null) {
            throw new NullPointerException("Cannot add null to Deque.");
        }
        
        Node oldFirst = sentinel.next;
        
        Node n = new Node();
        n.item = item;
        n.next = oldFirst;
        n.prev = sentinel;
        
        oldFirst.prev = n;
        sentinel.next = n;
        
        numItems++;
    }
    
/**
 * insert the item at the end
 * @param Item the item to add
 * @throws NullPointerException if attempt to add null to the deque
 */
    public void addLast(Item item)
    {
        if (item == null) {
            throw new NullPointerException("Cannot add null to Deque.");
        }
        
        Node oldLast = sentinel.prev;
        
        Node n = new Node();
        n.item = item;
        n.next = sentinel;
        n.prev = oldLast;
        
        oldLast.next  = n;
        sentinel.prev = n;

        numItems++;
    }
    
/**
 * delete and return the item at the front
 * @return the first item
 * @throws NoSuchElementException if attempt to remove from an empty deque
 */
    public Item removeFirst()
    {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow.");
        }
        
        Node oldFirst = sentinel.next;
        Item item     = oldFirst.item;
        
        sentinel.next      = oldFirst.next;
        sentinel.next.prev = sentinel;
        
        oldFirst = null;
        numItems--;
        return item;
    }
    
/**
 * delete and return the item at the end
 * @return the last item
 * @throws NoSuchElementException if attempt to remove from an empty deque
 */
    public Item removeLast()
    {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow.");
        }
        
        Node oldLast = sentinel.prev;
        Item item    = oldLast.item;
        
        sentinel.prev      = oldLast.prev;
        sentinel.prev.next = sentinel;
        
        oldLast = null;
        numItems--;
        return item;
    }
    
/**
 * return an iterator over items in order from front to end
 * @return the Iterator object
 */
    public Iterator<Item> iterator()
    {
        return new DequeIterator();  
    }

    // an iterator, doesn't implement remove() since it's optional
    private class DequeIterator implements Iterator<Item> {
        // start the iterator at the first node
        private Node current = sentinel.next;
        
        // true if it has a next node, false if not
        public boolean hasNext()
        {
            return current != sentinel;
        }
        
        // this iterator doesn't support remove
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        // return the current item, and adjust current to the next node
        public Item next() {
            if (!hasNext())
            {
                throw new NoSuchElementException("Deque underflow.");
            }
            
            Item item = current.item;
            current   = current.next; 
            return item;
        }
    }
    
/**
 * unit testing
 * @param args[] command line arguments
 */
    public static void main(String[] args)
    {
        // if the command line args aren't right, just set the sample size to 10
        int sampleSize = 0;
        if (args.length != 1) { sampleSize = 10; }
        else                  { sampleSize = Integer.parseInt(args[0]); }
            
        Deque<Integer> dq = new Deque<Integer>();
        
        // add integers 0 to N
        for (int i = 0; i < sampleSize; i++) {
            dq.addFirst(i);
        }

        StdOut.println("Size: " + dq.size());

        // test the iterator
        StdOut.println("Iteration: ");
        for (int i : dq) {
            StdOut.print(i + ": ");
            for (int j : dq) {
                StdOut.print(j + " ");
            }
            StdOut.println();
        }
        StdOut.println();
        
        // test removal
        // need to grab the size first. if you set the loop to use dq.size()
        // for termination each removal adjusts the size, so it changes during
        // the loop, and fails to hit every item
        int s = dq.size();
        for (int i = 0; i < s; i++) {
            int x = -1;
            if (i % 2 == 0) { x = dq.removeFirst(); }
            else            { x = dq.removeLast(); }
            StdOut.print(x + " ");
        }
        StdOut.println();
        StdOut.println("Size: " + dq.size());
        
        // test adding more items after deque has been emptied
        for (int i = 0; i < sampleSize; i = i + 2) {
            dq.addFirst(i);
        }
        
        StdOut.println("Size: " + dq.size());
        s = dq.size();
        for (int i = 0; i < s; i++) {
            StdOut.print(dq.removeFirst() + " ");
        }
        StdOut.println();
        StdOut.println("Size: " + dq.size());
    }
}