import java.util.ArrayList;
import java.util.List;

/**
 * A class for arithmetic encoding with fixed probabilities.
 */
public class FixedProbEncoder implements Encoder {
    private final double[] probs;  // cumulative probabilities
    // The probability that character c appears is probs[c] - probs[c - 1]
    // (for c = 0, it's probs[0])
    // The probability that the stop character appears is 1 - probs[probs.length - 1]

    /**
     * Assigns equal probability to the range of printable ASCII characters (32-126, inclusive)
     * and the end-of-text symbol
     */
    public FixedProbEncoder() {
        this.probs = new double[127];
        double delta = 1.0 / (126 - 32 + 2);
        double sum = 0;
        for (int i = 32; i <= 126; i++) {
            sum += delta;
            probs[i] = sum;
        }
        System.out.println(probs[126]);
    }

    /**
     * Creates a fixed probability encoder with the given cumulative probabilities.
     * Requires: probs is weakly monotonically increasing
     * and probs[probs.length - 1] < 1
     * The probability that character c appears is probs[c + 1] - probs[c]
     * The probability that the stop character appears is 1 - probs[probs.length - 1]
     * @param probs cumulative probabilities
     */
    public FixedProbEncoder(double[] probs) {
        this.probs = probs.clone();
    }

    @Override
    public byte[] encode(String text) {
        List<Integer> ans = new ArrayList<>();  // 1s and 0s
        char[] chars = text.toCharArray();
        double low = 0;
        double high = 1;
        for (char c : chars) {
            if (c >= probs.length) {
                throw new IllegalArgumentException("Character " + c + " out of range of cumulative probabilities");
            }
            // encode character c
            // shrink the range
            if (c == 0) {
                high = low + (high - low) * probs[0];
            } else {
                double newLow = low + (high - low) * probs[c - 1];
                double newHigh = low + (high - low) * probs[c];
                low = newLow;
                high = newHigh;
            }
            // if completely lies in one half, output a bit and renormalize
            while (high <= 0.5 || low >= 0.5) {
                if (high <= 0.5) {
                    ans.add(0);
                    low *= 2;
                    high *= 2;
                } else {
                    // low >= 0.5
                    ans.add(1);
                    low = (low - 0.5) * 2;
                    high = (high - 0.5) * 2;
                }
            }
        }
        // stop symbol
        low = low + (high - low) * probs[probs.length - 1];
        while (high <= 0.5 || low >= 0.5) {
            if (high <= 0.5) {
                ans.add(0);
                low *= 2;
                high *= 2;
            } else {
                // low >= 0.5
                ans.add(1);
                low = (low - 0.5) * 2;
                high = (high - 0.5) * 2;
            }
        }
        // we must have low < 0.5 and high > 0.5, so we can output 0.5 which is a 1 in binary
        ans.add(1);
//        printAsBinaryFraction(ans);
//        printAsApproxDecimalFraction(ans);
        return toByteArray(ans);
    }

    /**
     * Print a list of 1s and 0s as a binary fraction (for debugging purposes)
     */
    private void printAsBinaryFraction(List<Integer> nums) {
        StringBuilder sb = new StringBuilder();
        sb.append("0.");
        for (int num : nums) {
            sb.append(num);
        }
        System.out.println(sb);
    }

    /**
     * Print a list of 1s and 0s as an approximate decimal fraction (for debugging purposes)
     * This is as accurate as the precision of a double
     */
    private void printAsApproxDecimalFraction(List<Integer> nums) {
        double unit = 1;
        double ans = 0;
        for (int i = 0; i < nums.size(); i++) {
            unit /= 2;
            if (unit == 0) {
                break;
            }
            ans += nums.get(i) * unit;
        }
        System.out.println(ans);
    }

    /**
     * Converts a list of 1s and 0s to a byte array
     * where the first 8 bits form the first byte, the second 8 bits form the second byte, etc.
     * 0s are padded at the end.
     * @param nums a nonempty list of 1s and 0s
     */
    private byte[] toByteArray(List<Integer> nums) {
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
