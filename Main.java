import java.io.File;

public class Main {
    public static void main(String[] args) {
        testABC();
        testDefaultFixed();
    }

    private static void testABC() {
        double[] probs = new double[127];
        probs['A'] = 0.3;  // A has probability 0.3
        probs['B'] = probs['A'] + 0.4;  // B has probability 0.4
        probs['C'] = probs['B'] + 0.2;  // C has probability 0.2
        for (char c = 'D'; c < probs.length; c++) {
            probs[c] = probs[c - 1];
        }
        // end-of-file symbol has probability 0.1

        ProbModel abcModel = new FixedProbModel(probs);
        Encoder encoder = new ACEncoder(abcModel);
        byte[] aBytes = encoder.encode("A");  // Should be between 0.27 and 0.3 if we print decimal fraction
        byte[] abBytes = encoder.encode("AB");  // Should be between 0.188 and 0.21 if we print decimal fraction
        byte[] abcabBytes = encoder.encode("ABCAB");
        byte[] longBytes = encoder.encode("ACABBABBBCCCABAABCABCABCACABAAABCBCCCCCCBABBB");
        encoder.encode("CABBACCCCCBABA", new File("abc.txt"));

        abcModel = new FixedProbModel(probs);
        Decoder decoder = new ACDecoder(abcModel);
        System.out.println(decoder.decode(aBytes));
        System.out.println(decoder.decode(abBytes));
        System.out.println(decoder.decode(abcabBytes));
        System.out.println(decoder.decode(longBytes));
        System.out.println(decoder.decode(new File("abc.txt")));
        decoder.decode(new File("abc.txt"), new File("def.txt"));
    }

    private static void testDefaultFixed() {
        ProbModel defaultFixed = new FixedProbModel();
        Encoder encoder = new ACEncoder(defaultFixed);
        byte[] bytes = encoder.encode("Mary had a little lamb!");
        defaultFixed = new FixedProbModel();
        Decoder decoder = new ACDecoder(defaultFixed);
        System.out.println(decoder.decode(bytes));
    }
}
