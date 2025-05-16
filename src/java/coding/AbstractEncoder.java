package coding;

import java.io.*;

/**
 * A class that factors out common functions and I/O operations
 */
public abstract class AbstractEncoder implements Encoder {
    @Override
    public void encode(String text, File output) {
        try {
            if (output.createNewFile()) {
                System.out.println("Created file " + output.getName());
            }
            System.out.println("Writing output to file " + output.getName());
            OutputStream os = new FileOutputStream(output);
            os.write(encode(text));
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encode(File input, File output) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            encode(sb.toString(), output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
