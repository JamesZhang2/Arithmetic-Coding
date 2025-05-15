package coding;

import java.io.File;

public interface Encoder {
    /**
     * Encodes the given text to a sequence of bytes.
     * If the number of bits in the code is not divisible by 8,
     * then zeros are padded at the end of the last byte.
     */
    byte[] encode(String text);

    /**
     * Encodes the given text and writes the encoded bytes to the output file.
     */
    void encode(String text, File output);

    /**
     * Encodes the text in the input file and writes the encoded bytes to the output file.
     */
    void encode(File input, File output);
}
