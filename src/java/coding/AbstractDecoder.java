package coding;

import java.io.*;

/**
 * A class that factors out common functions and I/O operations
 */
public abstract class AbstractDecoder implements Decoder {
    @Override
    public String decode(File input) {
        try {
            FileInputStream fis = new FileInputStream(input);
            byte[] bytes = fis.readAllBytes();
            String decoded = decode(bytes);
            fis.close();
            return decoded;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(File input, File output) {
        try {
            FileInputStream fis = new FileInputStream(input);
            byte[] bytes = fis.readAllBytes();
            fis.close();
            if (output.createNewFile()) {
                System.out.println("Created file " + output.getName());
            }
            System.out.println("Writing decoded output to file " + output.getName());
            FileWriter writer = new FileWriter(output);
            writer.write(decode(bytes));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the ith bit (0-indexed) after the decimal point
     * of the binary fractional number represented by bytes.
     * Note that we pad the number with an infinite number of zeros at the end.
     */
    protected int getBit(byte[] bytes, int i) {
        if (i >= bytes.length * 8) {
            return 0;
        }
        return (bytes[i / 8] >> (7 - i % 8)) & 1;
    }
}
