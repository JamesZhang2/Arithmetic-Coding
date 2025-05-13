import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ACTest {
    @Test
    public void testABC() {
        double[] probs = new double[127];
        probs['A'] = 0.3;  // A has probability 0.3
        probs['B'] = probs['A'] + 0.4;  // B has probability 0.4
        probs['C'] = probs['B'] + 0.2;  // C has probability 0.2
        for (char c = 'D'; c < probs.length; c++) {
            probs[c] = probs[c - 1];
        }
        // end-of-file symbol has probability 0.1

        String[] strings = {
                "A",  // Should be between 0.27 and 0.3 if we print decimal fraction
                "B",  // Should be between 0.63 and 0.7 if we print decimal fraction
                "C",  // Should be between 0.81 and 0.9 if we print decimal fraction
                "AB", // Should be between 0.188 and 0.21 if we print decimal fraction
                "ABCAB",
                "ACABBABBBCCCABAABCABCABCACABAAABCBCCCCCCBABBB"
        };

        for (String str : strings) {
            ProbModel abcModel = new FixedProbModel(probs);
            Encoder encoder = new ACEncoder(abcModel);
            byte[] encoded = encoder.encode(str);
            abcModel = new FixedProbModel(probs);
            Decoder decoder = new ACDecoder(abcModel);
            assertEquals(str, decoder.decode(encoded));
        }

        for (int i = 0; i < 1000; i++) {
            String random = getRandomABCString(i);
            System.out.println(random);
            ProbModel abcModel = new FixedProbModel(probs);
            Encoder encoder = new ACEncoder(abcModel);
            byte[] encoded = encoder.encode(random);
            abcModel = new FixedProbModel(probs);
            Decoder decoder = new ACDecoder(abcModel);
            assertEquals(random, decoder.decode(encoded));
        }
    }

    private String getRandomABCString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)('A' + Math.random() * 3));
        }
        return sb.toString();
    }

    @Test
    public void testIO() {

    }

    @Test
    public void testDefaultFixed() {
        ProbModel defaultFixed = new FixedProbModel();
        Encoder encoder = new ACEncoder(defaultFixed);
        byte[] bytes = encoder.encode("Mary had a little lamb!");
        defaultFixed = new FixedProbModel();
        Decoder decoder = new ACDecoder(defaultFixed);
        System.out.println(decoder.decode(bytes));
    }

    @Test
    public void testDirichlet() {
        ProbModel dirichlet = new DirichletModel();
        Encoder encoder = new ACEncoder(dirichlet);
        byte[] bytes = encoder.encode("The quick brown fox jumps over the lazy dog.");
        dirichlet = new DirichletModel();
        Decoder decoder = new ACDecoder(dirichlet);
        System.out.println(decoder.decode(bytes));
    }
}