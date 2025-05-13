/**
 * A probability model where the probability of each character is fixed,
 * regardless of what the context (previous characters) is.
 * In other words, P(x_n = c | x_1, x_2, ..., x_{n - 1}) = p_c.
 */
public class FixedProbModel implements ProbModel {
    private final double[] probs;  // cumulative probabilities
    // The probability that character c appears is probs[c] - probs[c - 1]
    // (for c = 0, it's probs[0])
    // The probability that the stop character appears is 1 - probs[probs.length - 1]

    /**
     * Assigns equal probability to the range of all ASCII characters (0-127, inclusive)
     * and the end-of-text symbol
     */
    public FixedProbModel() {
        probs = new double[128];
        double delta = 1.0 / 129;
        double sum = 0;
        for (int i = 0; i < probs.length; i++) {
            sum += delta;
            probs[i] = sum;
        }
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
    public FixedProbModel(double[] probs) {
        for (int i = 1; i < probs.length; i++) {
            assert probs[i] >= probs[i - 1];
        }
        assert probs[probs.length - 1] < 1;  // probability of stop character must be nonzero
        this.probs = probs.clone();
    }

    @Override
    public void update(char c) {
        // do nothing
    }

    @Override
    public double[] getProbs() {
        return probs.clone();
    }
}
