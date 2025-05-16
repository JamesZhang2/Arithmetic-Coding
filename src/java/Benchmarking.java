import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class for benchmarking different coding schemes.
 */
public class Benchmarking {
    public static void main(String[] args) {
        generateTestFiles();
    }


    private static void generateTestFiles() {
        File unifRandom = new File("test/sampleTexts/generated/unif_random.txt");
        File biased50 = new File("test/sampleTexts/generated/biased_random_50.txt");
        File biased99 = new File("test/sampleTexts/generated/biased_random_99.txt");
        File allA = new File("test/sampleTexts/generated/all_a.txt");
        File randomBits = new File("test/sampleTexts/generated/random_bits.txt");
        generateUnifRandomASCII(1000000, unifRandom);
        generateBiasedRandom(1000000, biased50, 'a', 0.5);
        generateBiasedRandom(1000000, biased99, 'a', 0.99);
        generateBiasedRandom(1000000, allA, 'a', 1);
        generateRandomBits(1000000, randomBits, 0.5);
    }

    /**
     * Generates an independent, uniformly random text with ASCII characters (0-127)
     * with the given length and writes the text to the output file.
     */
    private static void generateUnifRandomASCII(int length, File output) {
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
    private static void generateRandomBits(int length, File output, double prob1) {
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
    private static void generateBiasedRandom(int length, File output, char biased, double prob) {
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
}
