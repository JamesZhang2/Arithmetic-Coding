package coding.ac;

/**
 * Bigram Dirichlet model: frequencies are updated based on what the previous symbol is.
 * Each character c has a frequency array indicating the frequency of seeing the next character
 * conditioned on the previous character being c.
 * When no characters have been read, use a uniform probability.
 * In this way, common 2-character combinations like qu will be assigned high probability.
 */
public class BigramDirichletModel implements ProbModel {
    private final double alpha;
    // freqs[c] is the frequency array conditioned on the previous character being c
    private final int[][] freqs;
    private final int[] charsSeen;  // number of characters already seen
    private char prevChar = Character.MAX_VALUE;  // placeholder value when we haven't seen a previous character

    /**
     * Default: Laplace model, alpha = 1
     */
    public BigramDirichletModel() {
        this(1.0);
    }

    public BigramDirichletModel(double alpha) {
        this.alpha = alpha;
        freqs = new int[128][128];
        charsSeen = new int[128];
    }

    @Override
    public void update(char c) {
        if (prevChar != Character.MAX_VALUE) {
            freqs[prevChar][c]++;
            charsSeen[prevChar]++;
        }
        prevChar = c;
    }

    @Override
    public double[] getProbs() {
        // P(x_n = c | x_1, ..., x_{n - 2}, x_{n - 1} = c') = (freq[c'][c] + alpha) / (sum_i (freq[c'][i] + alpha))
        double[] probs = new double[freqs.length];

        if (prevChar == Character.MAX_VALUE) {
            // haven't read any character yet, output uniform distribution
            for (int i = 0; i < freqs.length; i++) {
                probs[i] = (i + 1.0) / (freqs.length + 1);
            }
        } else {
            double runningSum = 0;
            double sum = charsSeen[prevChar] + (freqs.length + 1) * alpha;  // the end-of-file symbol definitely has not appeared yet
            for (int i = 0; i < freqs.length; i++) {
                runningSum += (freqs[prevChar][i] + alpha);
                probs[i] = runningSum / sum;
            }
        }
        return probs;
    }
}
