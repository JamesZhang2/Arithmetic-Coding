import coding.ac.*;
import coding.Decoder;
import coding.Encoder;
import org.junit.jupiter.api.Test;

import java.io.*;

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
            String random = TestUtil.getRandomABCString(i);
//            System.out.println(random);
            ProbModel abcModel = new FixedProbModel(probs);
            Encoder encoder = new ACEncoder(abcModel);
            byte[] encoded = encoder.encode(random);
            abcModel = new FixedProbModel(probs);
            Decoder decoder = new ACDecoder(abcModel);
            assertEquals(random, decoder.decode(encoded));
        }
    }

    interface EncoderGenerator {
        Encoder generate();
    }

    interface DecoderGenerator {
        Decoder generate();
    }

    /**
     * Use the given encoder and decoder generator pair to test encoding and decoding ASCII characters.
     * Note that a new encoder and decoder will be generated each time.
     */
    private void testEncodeDecodeASCII(EncoderGenerator encGen, DecoderGenerator decGen) {
        for (String str : TestUtil.TEST_STRINGS) {
            Encoder encoder = encGen.generate();
            byte[] encoded = encoder.encode(str);
            Decoder decoder = decGen.generate();
            assertEquals(str, decoder.decode(encoded));
        }

        for (int i = 0; i < 1000; i++) {
            String random = TestUtil.getRandomString(i);
//            System.out.println(random);
            Encoder encoder = encGen.generate();
            byte[] encoded = encoder.encode(random);
            Decoder decoder = decGen.generate();
            assertEquals(random, decoder.decode(encoded));
        }
    }

    private void testBasicIO(EncoderGenerator encGen, DecoderGenerator decGen) {
        // Encode string into a file
        String text = "The rain in Spain stays mainly in the plain.";  // this is actually not true by the way
        Encoder encoder = encGen.generate();
        File rainEncoded = new File("rain.ac");
        encoder.encode(text, rainEncoded);

        // Decode file back into a string and compare contents
        Decoder decoder = decGen.generate();
        assertEquals(text, decoder.decode(rainEncoded));

        // Encode file into a file
        encoder = encGen.generate();

        File foxOriginal = new File("sampleTexts/fox.txt");
        File foxEncoded = new File("fox.ac");
        encoder.encode(foxOriginal, foxEncoded);

        // Decode file into a file
        File foxDecoded = new File("fox_decoded.txt");
        decoder = decGen.generate();
        decoder.decode(foxEncoded, foxDecoded);

        // Compare file contents
        TestUtil.assertFileContentEquals(foxOriginal, foxDecoded);

        // clean up
        rainEncoded.delete();
        foxEncoded.delete();
        foxDecoded.delete();
    }

    private void testLargeFiles(EncoderGenerator encGen, DecoderGenerator decGen) {
        // Encode file into a file
        File original = new File("sampleTexts/alice_full.txt");
        File encoded = new File("alice_full.ac");
        Encoder encoder = encGen.generate();
        encoder.encode(original, encoded);

        // Decode file into a file
        File decoded = new File("alice_full_decoded.txt");
        Decoder decoder = decGen.generate();
        decoder.decode(encoded, decoded);

        // Compare file contents
        TestUtil.assertFileContentEquals(original, decoded);

        // clean up
        encoded.delete();
        decoded.delete();
    }

    private void testAll(EncoderGenerator encGen, DecoderGenerator decGen) {
        testEncodeDecodeASCII(encGen, decGen);
        testBasicIO(encGen, decGen);
        testLargeFiles(encGen, decGen);
    }

    @Test
    public void testDefaultFixedProb() {
        testAll(ACEncoder::new, ACDecoder::new);
    }

    @Test
    public void testDirichlet() {
        testAll(() -> new ACEncoder(new DirichletModel()),
                () -> new ACDecoder(new DirichletModel()));
        testAll(() -> new ACEncoder(new DirichletModel(10)),
                () -> new ACDecoder(new DirichletModel(10)));
        testAll(() -> new ACEncoder(new DirichletModel(0.01)),
                () -> new ACDecoder(new DirichletModel(0.01)));
    }

    @Test
    public void testBigramDirichlet() {
        testAll(() -> new ACEncoder(new BigramDirichletModel()),
                () -> new ACDecoder((new BigramDirichletModel())));
        testAll(() -> new ACEncoder(new BigramDirichletModel(10)),
                () -> new ACDecoder((new BigramDirichletModel(10))));
        testAll(() -> new ACEncoder(new BigramDirichletModel(0.01)),
                () -> new ACDecoder((new BigramDirichletModel(0.01))));
    }
}
