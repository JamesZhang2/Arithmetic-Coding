/**
 * A class representing the decoder in arithmetic coding (AC).
 */
public class ACDecoder extends AbstractDecoder {
    private final ProbModel probModel;

    /**
     * Creates an AC decoder with the given probabilistic model.
     * Note that this should be a new instance of the same probabilistic model
     * as the one used in the encoder,
     */
    public ACDecoder(ProbModel probModel) {
        this.probModel = probModel;
    }

    @Override
    public String decode(byte[] bytes) {
        // To decode, we first read in several bits and convert it to a double.
        // We keep reading more bits until we can distinguish what the next character is.
        // We then zoom in [low, high) to the range of that next character.
        // If [low, high) is entirely contained in the first or second half,
        // we renormalize low, high, and encoded, then we bring in another bit from the bytes.
        // Invariant: encoded is the renormalized double for the bytes up to (but excluding) nextBit.
        StringBuilder sb = new StringBuilder();
        double low = 0;
        double high = 1;
        double encoded = 0;
        int renorms = 0;  // number of renormalizations
        int nextBit = 0;  // next bit to bring in from the bytes
        for (int i = 0; i < 16; i++) {
            encoded += getBit(bytes, nextBit) * Math.pow(2, -nextBit - 1);
            nextBit++;
        }
        while (true) {
            double[] probs = probModel.getProbs();
            assert probs[probs.length - 1] < 1;
            assert low <= encoded && encoded < high
                : String.format("low: %f, encoded: %f, high: %f", low, encoded, high);
            boolean found = false;
            if (encoded >= low + (high - low) * probs[probs.length - 1]) {
                // end of file
                return sb.toString();
            }
            for (char c = 0; c < probs.length; c++) {
                if (encoded >= low + (high - low) * (c == 0 ? 0 : probs[c - 1])
                    && encoded + Math.pow(2, -nextBit + renorms - 1) < low + (high - low) * probs[c]) {
                    // next char is c
                    sb.append(c);

                    probModel.update(c);  // inform the probabilistic model that the next character is c

                    // shrink the range
                    if (c == 0) {
                        high = low + (high - low) * probs[0];
                    } else {
                        double newLow = low + (high - low) * probs[c - 1];
                        double newHigh = low + (high - low) * probs[c];
                        low = newLow;
                        high = newHigh;
                    }
                    assert low <= encoded && encoded < high
                            : String.format("low: %f\nencoded: %f\nhigh: %f", low, encoded, high);

                    // if completely lies in one half, renormalize and bring in another bit
                    while (high <= 0.5 || low >= 0.5) {
                        if (high <= 0.5) {
                            assert encoded <= 0.5;
                            low *= 2;
                            high *= 2;
                            encoded *= 2;
                        } else {
                            // low >= 0.5
                            assert encoded >= 0.5;
                            low = (low - 0.5) * 2;
                            high = (high - 0.5) * 2;
                            encoded = (encoded - 0.5) * 2;
                        }
                        renorms++;
                        encoded += getBit(bytes, nextBit) * Math.pow(2, -nextBit + renorms - 1);
                        nextBit++;
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                // The range of possible encoded numbers overlaps with multiple character ranges,
                // so we cannot tell what the next character is.
                // We need to bring in another bit.
                encoded += getBit(bytes, nextBit) * Math.pow(2, -nextBit + renorms - 1);
                nextBit++;
            }
        }
    }
}
