package coding;

import java.util.List;

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

    /**
     * Converts a list of 1s and 0s to a byte array
     * where the first 8 bits form the first byte, the second 8 bits form the second byte, etc.
     * 0s are padded at the end.
     * @param nums a nonempty list of 1s and 0s
     */
    public static byte[] toByteArray(List<Integer> nums) {
        byte[] bytes = new byte[(int)Math.ceil(nums.size() / 8.0)];
        for (int i = 0; i < bytes.length; i++) {
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                if (i * 8 + j < nums.size()) {
                    b += (byte) (nums.get(i * 8 + j) << (7 - j));
                }
            }
            bytes[i] = b;
        }
        return bytes;
    }
}
