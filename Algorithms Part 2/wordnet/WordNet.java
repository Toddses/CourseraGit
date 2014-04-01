/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       03/31/2014
 * Last updated:  04/01/2014
 * 04/01/2014 : Fixed a couple bugs and added the rooted DAG validation
 *  
 * WordNet takes in a file of synsets (which contains nouns) and a file of hypernyms.
 * These file formats can be translated into a list of vertices and a list of edges,
 * from which one can build a Directed Graph. We can test the semantic relatedness of nouns
 * based on this Digraph, using the SAP object.
 * 
 * Compilation:   javac WordNet.java
 * Execution: java WordNet filename.txt filename.txt
 * Dependencies: java.util.ArrayList, RedBlackBST.java, Digraph.java, SAP.java, In.java,
 *               StdIn.java, StdOut.java
 */

import java.util.ArrayList;

public class WordNet {
    
    private ArrayList<String> sets;                   // an integer keyed list of synsets
    private RedBlackBST<String, Bag<Integer>> nouns;  // a noun keyed tree of synset id lists
    private SAP pather;                               // the shortest ancestral path object
    
/**
 * constructor takes the name of the two input files
 * @param String synsets file
 * @param String hypernyms file
 */
    public WordNet(String synsets, String hypernyms)
    {
        // chose a couple data structures to handle all this data.
        // using an ArrayList to store the synsets allows access keyed on id
        // integers and gives constant time access.
        // using a binary search tree keyed on the individual nouns gives
        // logarithmic access, which is nice.
        this.sets  = new ArrayList<String>();
        this.nouns = new RedBlackBST<String, Bag<Integer>>();
        
        // set up a reuseable input stream.
        In in = null;
        
        // initialize a numV value, which will be used for:
        // - the current id.
        // - after synsets it will have the number of vertices - 1
        // - after initializing the Digraph, will count the number of root vertices
        int numV = -1;
        
        // open up and parse the given synsets file.
        in = new In(synsets);        
        while (!in.isEmpty()) {
            // expected format:
            // id, nounA [nounB nounC ..], gloss
            String[] line = in.readLine().split(",");
            numV         = Integer.parseInt(line[0]);
            String[] s    = line[1].split(" ");
            
            // add the synset to the array list
            sets.add(line[1]);
            // for each word in the synset
            for (String word : s) {
                Bag<Integer> bag = null;
                
                // if its already in the WordNet, add the new id to the id list.
                if (nouns.contains(word)) {
                    bag = nouns.get(word);
                    bag.add(numV);
                }
                // if it isn't in the WordNet, create a new entry for it.
                else {
                    bag = new Bag<Integer>();
                    bag.add(numV);
                    nouns.put(word, bag);
                }
            }
        }
        
        // build a new Digraph with size based on MaxID.
        Digraph dG = new Digraph(numV + 1);
        
        // open up and parse the given hypernyms file.
        in = new In(hypernyms);
        while (!in.isEmpty()) {
            // expected format:
            // id, edgeToID1[, edgeToID2, edgeToID3 ..]
            String[] line = in.readLine().split(",");
            int v         = Integer.parseInt(line[0]);
            
            // check if this is a root vertex (has no outgoing edges).
            // if not we want to decrement the counter.
            if (line.length > 1)
                numV--;
            
            // for each id in the edge list, create a new edge in the Digraph.
            for (int i = 1; i < line.length; i++) {
                int w = Integer.parseInt(line[i]);
                dG.addEdge(v, w);
            }
        }
        
        // validate the newly built Digraph to ensure it is a valid DAG
        Topological t = new Topological(dG);
        if (!t.hasOrder() || numV > 0)
            throw new IllegalArgumentException("Given input does not construct a rooted DAG.");
        
        // finally, load up the SAP object based on the Digraph.
        this.pather = new SAP(dG);
    }

/**
 * the set of nouns (no duplicates), returned as an Iterable
 * @return an iterable list of the nouns in the WordNet
 */
    public Iterable<String> nouns()
    {
        return nouns.keys();
    }

/**
 * is the word a WordNet noun?
 * @param String the noun to test
 * @return true if its in the WordNet, false otherwise
 */
    public boolean isNoun(String word)
    {
        return nouns.contains(word);
    }

/**
 * distance between nounA and nounB
 * @param String first noun
 * @param String second noun
 * @return length of the shortest ancestral path between the given nouns
 */
    public int distance(String nounA, String nounB)
    {
        // validate parameters
        if (!isNoun(nounA))
            throw new IllegalArgumentException(nounA + " is not a valid noun.");
        if (!isNoun(nounB))
            throw new IllegalArgumentException(nounB + " is not a valid noun.");
        
        // first get the list of ids associated with each noun.
        // then let the SAP do the work.
        Bag<Integer> v = nouns.get(nounA);
        Bag<Integer> w = nouns.get(nounB);
        return pather.length(v, w);
    }

/**
 * a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
 * in a shortest ancestral path
 * @param String first noun
 * @param String second noun
 * @return the full synset of the common ancestor
 */
    public String sap(String nounA, String nounB)
    {
        // validate parameters
        if (!isNoun(nounA))
            throw new IllegalArgumentException(nounA + " is not a valid noun.");
        if (!isNoun(nounB))
            throw new IllegalArgumentException(nounB + " is not a valid noun.");
        
        // first get the list of ids associated with each noun.
        // then let the SAP do the work.
        Bag<Integer> v = nouns.get(nounA);
        Bag<Integer> w = nouns.get(nounB);
        int anc = pather.ancestor(v, w);
        return sets.get(anc);
    }

/**
 * for unit testing of this class
 */
    public static void main(String[] args)
    {
        WordNet wn = new WordNet(args[0], args[1]);
        
        while (!StdIn.isEmpty()) {
            //String word = StdIn.readLine();
            //if (wn.isNoun(word))
                //StdOut.println(word + " : Now that's a noun");
            //else
                //StdOut.println("Cannot find " + word);
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            StdOut.println(wn.sap(nounA, nounB) + " : " + wn.distance(nounA, nounB));
        }
    }
}

