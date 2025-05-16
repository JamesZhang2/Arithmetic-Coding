package coding.ac;

/**
 * Dirichlet model used in adaptive arithmetic coding,
 * as described on Page 117, Section 6.2 of David MacKay's book
 */
public class DirichletModel implements ProbModel {
    private final double alpha;
    private final int[] freqs;
    private int charsSeen;  // number of characters already seen

    /**
     * Default: Laplace model, alpha = 1
     */
    public DirichletModel() {
        this(1.0);
    }

    public DirichletModel(double alpha) {
        this.alpha = alpha;
        freqs = new int[128];
    }

    @Override
    public void update(char c) {
        freqs[c]++;
        charsSeen++;
    }

    @Override
    public double[] getProbs() {
        // P(x_n = c | x_1, ..., x_{n - 1}) = (freq[c] + alpha) / (sum_i (freq[i] + alpha))
        double[] probs = new double[freqs.length];
        double runningSum = 0;
        double sum = charsSeen + (freqs.length + 1) * alpha;  // the end-of-file symbol definitely has not appeared yet
        for (int i = 0; i < freqs.length; i++) {
            runningSum += (freqs[i] + alpha);
            probs[i] = runningSum / sum;
        }
        return probs;
    }
}
