import java.io.File;

public interface Decoder {
    /**
     * Decodes a sequence of bytes into a String.
     */
    String decode(byte[] bytes);

    /**
     * Decodes the content of a file into a String.
     */
    String decode(File input);

    /**
     * Decodes the content of a file and writes the output string to another file.
     */
    void decode(File input, File output);
}
