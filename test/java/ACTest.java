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
            String random = getRandomABCString(i);
//            System.out.println(random);
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
        String[] strings = {
                "",
                ".",
                "42",
                "\n\t\r ",
                "James",
                "aaaaa",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "the quick brown fox jumps over the lazy dog",
                "~When there's a will, there's a way!~",
                "Mary had a little lamb.",
                "A monad is a monoid in the category of endofunctors.",
                "Robert'); DROP TABLE STUDENTS; --"
        };
        for (String str : strings) {
            Encoder encoder = encGen.generate();
            byte[] encoded = encoder.encode(str);
            Decoder decoder = decGen.generate();
            assertEquals(str, decoder.decode(encoded));
        }

        for (int i = 0; i < 1000; i++) {
            String random = getRandomString(i);
//            System.out.println(random);
            Encoder encoder = encGen.generate();
            byte[] encoded = encoder.encode(random);
            Decoder decoder = decGen.generate();
            assertEquals(random, decoder.decode(encoded));
        }
    }

    private String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)(Math.random() * 128));
        }
        return sb.toString();
    }

    private void testBasicIO(EncoderGenerator encGen, DecoderGenerator decGen) {
        // Encode string into a file
        String text = "The rain in Spain stays mainly in the plain.";  // this is actually not true by the way
        Encoder encoder = encGen.generate();
        encoder.encode(text, new File("rain.ac"));

        // Decode file back into a string and compare contents
        Decoder decoder = decGen.generate();
        assertEquals(text, decoder.decode(new File("rain.ac")));

        // Encode file into a file
        encoder = encGen.generate();
        encoder.encode(new File("sampleTexts/fox.txt"), new File("fox.ac"));

        // Decode file into a file
        decoder = decGen.generate();
        decoder.decode(new File("fox.ac"), new File("fox_decoded.txt"));

        // Compare file contents
        assertFileContentEquals("sampleTexts/fox.txt", "fox_decoded.txt");

        // clean up
        (new File("fox.ac")).delete();
        (new File("rain.ac")).delete();
        (new File("fox_decoded.txt")).delete();
    }

    private void testLargeFiles(EncoderGenerator encGen, DecoderGenerator decGen) {
        // Encode file into a file
        Encoder encoder = encGen.generate();
        encoder.encode(new File("sampleTexts/alice1.txt"), new File("alice1.ac"));

        // Decode file into a file
        Decoder decoder = decGen.generate();
        decoder.decode(new File("alice1.ac"), new File("alice1_decoded.txt"));

        // Compare file contents
        assertFileContentEquals("sampleTexts/alice1.txt", "alice1_decoded.txt");

        // clean up
        (new File("alice1.ac")).delete();
        (new File("alice1_decoded.txt")).delete();
    }

    private void testAll(EncoderGenerator encGen, DecoderGenerator decGen) {
        testEncodeDecodeASCII(encGen, decGen);
        testBasicIO(encGen, decGen);
        testLargeFiles(encGen, decGen);
    }

    /**
     * Assert that two text files have the same contents
     */
    private void assertFileContentEquals(String filename1, String filename2) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename1));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            BufferedReader br2 = new BufferedReader(new FileReader(filename2));
            StringBuilder sb2 = new StringBuilder();
            line = br2.readLine();
            while (line != null) {
                sb2.append(line);
                line = br2.readLine();
            }
            assertEquals(sb.toString(), sb2.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
