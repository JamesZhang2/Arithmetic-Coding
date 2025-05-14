package coding;

import java.io.File;

public interface Decoder {
    /**
     * Decodes a sequence of bytes into a String.
     */
    String decode(byte[] bytes);

    /**
     * Decodes the content of the input file into a String.
     */
    String decode(File input);

    /**
     * Decodes the content of the input file and writes the output string to the output file.
     */
    void decode(File input, File output);
}
