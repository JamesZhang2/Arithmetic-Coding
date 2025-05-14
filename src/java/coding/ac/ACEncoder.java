package coding.ac;

import coding.AbstractEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the encoder in arithmetic coding (AC).
 */
public class ACEncoder extends AbstractEncoder {
    private final ProbModel probModel;

    /**
     * Creates an AC encoder with the given probabilistic model.
     */
    public ACEncoder(ProbModel probModel) {
        this.probModel = probModel;
    }

    /**
     * The default constructor uses the default fixed probability model.
     */
    public ACEncoder() {
        this.probModel = new FixedProbModel();
    }

    @Override
    public byte[] encode(String text) {
        List<Integer> ans = new ArrayList<>();  // 1s and 0s
        char[] chars = text.toCharArray();
        double low = 0;
        double high = 1;
        int underflows = 0;
        for (char c : chars) {
            double[] probs = probModel.getProbs();
            assert probs[probs.length - 1] < 1;
            if (c >= probs.length) {
                throw new IllegalArgumentException("Character " + c + " out of range of cumulative probabilities");
            }
            if ((c == 0 && probs[0] == 0) || (c > 0 && probs[c] == probs[c - 1])) {
                throw new IllegalArgumentException("Character " + c + " is not supported since it has probability 0");
            }

            probModel.update(c);  // inform the probabilistic model that the next character is c

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
            // if completely lies in the middle half, i.e. [0.25, 0.75),
            // we scale it up and remember there was an underflow condition
            while (high <= 0.5 || low >= 0.5 || (high <= 0.75 && low >= 0.25)) {
                if (high <= 0.5) {
                    ans.add(0);
                    while (underflows > 0) {
                        ans.add(1);
                        underflows--;
                    }
                    low *= 2;
                    high *= 2;
                } else if (low >= 0.5){
                    ans.add(1);
                    while (underflows > 0) {
                        ans.add(0);
                        underflows--;
                    }
                    low = (low - 0.5) * 2;
                    high = (high - 0.5) * 2;
                } else {
                    low = (low - 0.25) * 2;
                    high = (high - 0.25) * 2;
                    underflows++;
                }
            }
        }
        double[] probs = probModel.getProbs();
        // stop symbol
        low = low + (high - low) * probs[probs.length - 1];
        while (high <= 0.5 || low >= 0.5) {
            if (high <= 0.5) {
                ans.add(0);
                while (underflows > 0) {
                    ans.add(1);
                    underflows--;
                }
                low *= 2;
                high *= 2;
            } else {
                // low >= 0.5
                ans.add(1);
                while (underflows > 0) {
                    ans.add(0);
                    underflows--;
                }
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
}
