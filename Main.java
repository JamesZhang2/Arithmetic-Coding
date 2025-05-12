public class Main {
    public static void main(String[] args) {
        double[] probs = new double[127];
        probs['A'] = 0.3;
        probs['B'] = probs['A'] + 0.4;
        probs['C'] = probs['B'] + 0.2;
        for (char c = 'D'; c < probs.length; c++) {
            probs[c] = probs[c - 1];
        }
        Encoder encoder = new FixedProbEncoder(probs);
        encoder.encode("A");  // Should be between 0.27 and 0.3
        encoder.encode("AB");  // Should be between 0.188 and 0.21
    }
}
