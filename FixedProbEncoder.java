import java.util.ArrayList;
import java.util.List;

/**
 * A class for arithmetic encoding with fixed probabilities.
 */
public class FixedProbEncoder extends AbstractEncoder {
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
     *
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
            if ((c == 0 && probs[0] == 0) || (c > 0 && probs[c] == probs[c - 1])) {
                throw new IllegalArgumentException("Character " + c + " is not supported since it has probability 0");
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
     * @return a copy of the cumulative probabilities
     */
    public double[] getProbs() {
        return probs.clone();
    }
}
