public interface Encoder {
    /**
     * Encodes the given text to a sequence of bytes.
     */
    byte[] encode(String text);
}
