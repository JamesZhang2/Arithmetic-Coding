/**
 * An interface for the probabilistic models used in arithmetic coding.
 */
public interface ProbModel {
    /**
     * Update the model given that the next character is c.
     */
    void update(char c);

    /**
     * @return the cumulative probabilities for each ASCII character (0-127, inclusive)
     * given the characters that the model has seen so far.
     * This should be a copy of the actual probabilities in the model
     * so modifying this array does not affect the model.
     */
    double[] getProbs();
}
