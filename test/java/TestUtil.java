import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    /**
     * Assert that two text files have the same contents
     */
    public static void assertFileContentEquals(File file1, File file2) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file1));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            br.close();

            BufferedReader br2 = new BufferedReader(new FileReader(file2));
            StringBuilder sb2 = new StringBuilder();
            line = br2.readLine();
            while (line != null) {
                sb2.append(line);
                line = br2.readLine();
            }
            assertEquals(sb.toString(), sb2.toString());
            br2.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
