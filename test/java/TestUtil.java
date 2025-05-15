public class TestUtil {
    // test strings only contain ASCII characters (0-127)
    public static final String[] TEST_STRINGS = {
            "",
            ".",
            "42",
            "\n\t\r ",
            "1234567890",
            String.valueOf((char)1),
            "James",
            "aaaaa",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "the quick brown fox jumps over the lazy dog",
            "~When there's a will, there's a way!~",
            "Mary had a little lamb.",
            "A monad is a monoid in the category of endofunctors.",
            "Robert'); DROP TABLE STUDENTS; --"
    };

    /**
     * @return a random string consisting of only the characters 'A', 'B', and 'C' with the given length
     */
    public static String getRandomABCString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)('A' + Math.random() * 3));
        }
        return sb.toString();
    }

    /**
     * @return a random string consisting of ASCII characters (0-127) with the given length
     */
    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)(Math.random() * 128));
        }
        return sb.toString();
    }
}
