package coding;

/**
 * A class for utilities.
 */
public class Util {
    /**
     * Counts the frequencies of characters in text.
     * @return an array freqs of size 129 where freqs[c] is the number of times
     * that character c appears in text for ASCII characters c in [0, 127],
     * and freqs[128] is the number of times that the end-of-file symbol appears (always 1).
     */
    public static int[] countFreqs(String text) {
        int[] freqs = new int[129];
        for (int i = 0; i < text.length(); i++) {
            freqs[text.charAt(i)]++;
        }
        freqs[128] = 1;  // end-of-file symbol
        return freqs;
    }
}
