import java.io.File;

public interface Encoder {
    /**
     * Encodes the given text to a sequence of bytes.
     */
    byte[] encode(String text);

    /**
     * Encodes the given text and writes the encoded bytes into a file.
     */
    void encode(String text, File output);
}
