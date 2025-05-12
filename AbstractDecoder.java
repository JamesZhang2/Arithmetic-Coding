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
            return decode(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(File input, File output) {
        try {
            FileInputStream fis = new FileInputStream(input);
            byte[] bytes = fis.readAllBytes();
            if (output.createNewFile()) {
                System.out.println("Created file " + output.getName());
            }
            System.out.println("Writing output to file " + output.getName());
            FileWriter writer = new FileWriter(output);
            writer.write(decode(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
