import coding.Decoder;
import coding.Encoder;
import coding.ac.*;
import coding.huffman.HuffmanDecoder;
import coding.huffman.HuffmanEncoder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class for benchmarking different coding schemes.
 */
public class Benchmarking {
    @Test
    public void runBenchmarkingTests() {
        File unifRandom = new File("sampleTexts/generated/unif_random.txt");
        File biased50 = new File("sampleTexts/generated/biased_random_50.txt");
        File biased99 = new File("sampleTexts/generated/biased_random_99.txt");
        File allA = new File("sampleTexts/generated/all_a.txt");
        File randomBits = new File("sampleTexts/generated/random_bits.txt");
        File alice = new File("sampleTexts/alice_full.txt");
        File english_words = new File("sampleTexts/english_words.txt");
        // Generate random strings
        generateUnifRandomASCII(1000000, unifRandom);
        generateBiasedRandom(1000000, biased50, 'a', 0.5);
        generateBiasedRandom(1000000, biased99, 'a', 0.99);
        generateBiasedRandom(1000000, allA, 'a', 1);
        generateRandomBits(1000000, randomBits, 0.5);

        File[] files = {
                unifRandom, biased50, biased99, allA, randomBits, alice, english_words
        };
        for (File file : files) {
            encDecHuffman(file);
            encDecACFixed(file);
            encDecACDirichlet(file);
            encDecACBigram(file);
        }
    }

    /**
     * Generates an independent, uniformly random text with ASCII characters (0-127)
     * with the given length and writes the text to the output file.
     */
    private void generateUnifRandomASCII(int length, File output) {
        try {
            FileWriter writer = new FileWriter(output);
            for (int i = 0; i < length; i++) {
                writer.write((char) (Math.random() * 128));
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates an independent, biased random text with '1's and '0's
     * with the given length and writes the text to the output file.
     * The probability of getting a 1 is prob1.
     */
    private void generateRandomBits(int length, File output, double prob1) {
        try {
            FileWriter writer = new FileWriter(output);
            for (int i = 0; i < length; i++) {
                writer.write(Math.random() < prob1 ? '1' : '0');
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates an independent, biased random text with ASCII characters (0-127) with the given length
     * and write the text to the output file.
     * The biased character appears with probability prob while the rest of the characters
     * appear with equal probability.
     */
    private void generateBiasedRandom(int length, File output, char biased, double prob) {
        try {
            FileWriter writer = new FileWriter(output);
            for (int i = 0; i < length; i++) {
                if (Math.random() < prob) {
                    writer.write(biased);
                } else {
                    char c = (char) (Math.random() * 128);
                    while (c == biased) {
                        c = (char) (Math.random() * 128);
                    }
                    writer.write(c);
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes the given file using Huffman encoding, then decodes it,
     * then compares that the decoded content is equal to the original.
     */
    private void encDecHuffman(File file) {
        String filenameNoSuffix = file.getName().substring(0, file.getName().lastIndexOf("."));
        HuffmanEncoder encoder = new HuffmanEncoder(file);
        File encoded = new File("encoded/" + filenameNoSuffix + ".huffman");
        encoder.encode(file, encoded);
        HuffmanDecoder decoder = new HuffmanDecoder(encoder);
        File decoded = new File("decoded/" + filenameNoSuffix + "_huffman.txt");
        decoder.decode(encoded, decoded);
        TestUtil.assertFileContentEquals(file, decoded);
    }

    private void encDecACFixed(File file) {
        String filenameNoSuffix = file.getName().substring(0, file.getName().lastIndexOf("."));
        Encoder encoder = new ACEncoder();
        File encoded = new File("encoded/" + filenameNoSuffix + ".fixed");
        encoder.encode(file, encoded);
        Decoder decoder = new ACDecoder();
        File decoded = new File("decoded/" + filenameNoSuffix + "_fixed.txt");
        decoder.decode(encoded, decoded);
        TestUtil.assertFileContentEquals(file, decoded);
    }

    private void encDecACDirichlet(File file) {
        String filenameNoSuffix = file.getName().substring(0, file.getName().lastIndexOf("."));

        Encoder encoder0 = new ACEncoder(new DirichletModel(0.01));
        Encoder encoder1 = new ACEncoder(new DirichletModel(1));
        Encoder encoder2 = new ACEncoder(new DirichletModel(100));

        File encoded0 = new File("encoded/" + filenameNoSuffix + ".dir001");
        encoder0.encode(file, encoded0);
        File encoded1 = new File("encoded/" + filenameNoSuffix + ".dir1");
        encoder1.encode(file, encoded1);
        File encoded2 = new File("encoded/" + filenameNoSuffix + ".dir100");
        encoder2.encode(file, encoded2);

        Decoder decoder0 = new ACDecoder(new DirichletModel(0.01));
        Decoder decoder1 = new ACDecoder(new DirichletModel(1));
        Decoder decoder2 = new ACDecoder(new DirichletModel(100));

        File decoded0 = new File("decoded/" + filenameNoSuffix + "_dir001.txt");
        decoder0.decode(encoded0, decoded0);
        File decoded1 = new File("decoded/" + filenameNoSuffix + "_dir1.txt");
        decoder1.decode(encoded1, decoded1);
        File decoded2 = new File("decoded/" + filenameNoSuffix + "_dir100.txt");
        decoder2.decode(encoded2, decoded2);

        TestUtil.assertFileContentEquals(file, decoded0);
        TestUtil.assertFileContentEquals(file, decoded1);
        TestUtil.assertFileContentEquals(file, decoded2);
    }

    private void encDecACBigram(File file) {
        String filenameNoSuffix = file.getName().substring(0, file.getName().lastIndexOf("."));

        Encoder encoder0 = new ACEncoder(new BigramDirichletModel(0.01));
        Encoder encoder1 = new ACEncoder(new BigramDirichletModel(1));
        Encoder encoder2 = new ACEncoder(new BigramDirichletModel(100));

        File encoded0 = new File("encoded/" + filenameNoSuffix + ".bigram001");
        encoder0.encode(file, encoded0);
        File encoded1 = new File("encoded/" + filenameNoSuffix + ".bigram1");
        encoder1.encode(file, encoded1);
        File encoded2 = new File("encoded/" + filenameNoSuffix + ".bigram100");
        encoder2.encode(file, encoded2);

        Decoder decoder0 = new ACDecoder(new BigramDirichletModel(0.01));
        Decoder decoder1 = new ACDecoder(new BigramDirichletModel(1));
        Decoder decoder2 = new ACDecoder(new BigramDirichletModel(100));

        File decoded0 = new File("decoded/" + filenameNoSuffix + "_bigram001.txt");
        decoder0.decode(encoded0, decoded0);
        File decoded1 = new File("decoded/" + filenameNoSuffix + "_bigram1.txt");
        decoder1.decode(encoded1, decoded1);
        File decoded2 = new File("decoded/" + filenameNoSuffix + "_bigram100.txt");
        decoder2.decode(encoded2, decoded2);

        TestUtil.assertFileContentEquals(file, decoded0);
        TestUtil.assertFileContentEquals(file, decoded1);
        TestUtil.assertFileContentEquals(file, decoded2);
    }
}
