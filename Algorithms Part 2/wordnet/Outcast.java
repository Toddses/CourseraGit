/**
 * Author:        Todd Miller
 * Email:         tmiller169@hotmail.com
 * Written:       04/01/2014
 * Last updated:  04/01/2014
 *  
 * Detects an outcast in a list of valid WordNet nouns. An outcast is defined as
 * the noun that is least related to the others by semantic relatedness.
 * 
 * Compilation:   javac Outcast.java
 * Execution: java Outcast filename.txt filename.txt filename[s].txt
 * Dependencies: WordNet.java, In.java, StdOut.java
 */

public class Outcast {
    
    private WordNet wn;   // local reference to the given WordNet
    
/**
 * constructor takes a WordNet object
 * @param WordNet valid WordNet object
 */
    public Outcast(WordNet wordnet)
    {
        this.wn = wordnet;
    }

/**
 * given an array of WordNet nouns, return an outcast
 * note we assume the given array has only valid nouns
 * @param String[] array of nouns
 * @return the outcast noun
 */
    public String outcast(String[] nouns)
    {
        // initialize the max distance so it can be easily tested later
        int maxDist    = -1;
        String outcast = null;
        
        // the logic here is to loop through each noun in the array and compare
        // it to every other noun, computing the sum of their distances.
        // then check if it is the new max distance.
        for (int i = 0; i < nouns.length; i++) {
            int curDist = 0;
            
            for (int j = 0; j < nouns.length; j++) {
                // don't compare the noun to itself.
                if (i != j)
                    curDist += wn.distance(nouns[i], nouns[j]);
            }
            
            if (maxDist == -1 || curDist > maxDist) {
                maxDist = curDist;
                outcast = nouns[i];
            }
        }
            
        return outcast;
    }

/**
 * for unit testing of this class
 */
    public static void main(String[] args)
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}