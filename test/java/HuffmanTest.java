import coding.huffman.HuffmanDecoder;
import coding.huffman.HuffmanEncoder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HuffmanTest {
    @Test
    public void testABC() {
        String text0 = "aaaabbbcc";
        HuffmanEncoder encoder0 = new HuffmanEncoder(text0);
        byte[] bytes = encoder0.encode(text0);
        HuffmanDecoder decoder0 = new HuffmanDecoder(encoder0);
        assertEquals(text0, decoder0.decode(bytes));

        Map<Character, String> codeMap = new HashMap<>();
        codeMap.put('A', "10");
        codeMap.put('B', "01");
        codeMap.put('C', "11");
        codeMap.put((char)128, "00");
        HuffmanEncoder encoder1 = new HuffmanEncoder(codeMap);
        String text1 = "ACBACCBC";
        String encoded1 = encoder1.encodeAsString(text1);
        assertEquals("101101101111011100", encoded1);
        HuffmanDecoder decoder1 = new HuffmanDecoder(encoder1);
        assertEquals(text1, decoder1.decodeString(encoded1));

        for (int i = 1; i < 1000; i++) {
            // Huffman encoder does not accept empty strings
            String random = TestUtil.getRandomABCString(i);
//            System.out.println(random);
            HuffmanEncoder encoder = new HuffmanEncoder(random);
//            System.out.println(encoder.encodeAsString(random));
            bytes = encoder.encode(random);
            HuffmanDecoder decoder = new HuffmanDecoder(encoder);
            assertEquals(random, decoder.decode(bytes), "Failed on string: " + random);
        }
    }

    @Test
    public void testEncodeDecodeASCII() {
        for (String str : TestUtil.TEST_STRINGS) {
            if (str.isEmpty()) {
                // Huffman encoder does not accept empty strings
                continue;
            }
            HuffmanEncoder encoder = new HuffmanEncoder(str);
            byte[] encoded = encoder.encode(str);
            HuffmanDecoder decoder = new HuffmanDecoder(encoder);
            assertEquals(str, decoder.decode(encoded));
        }

        for (int i = 1; i < 1000; i++) {
            String random = TestUtil.getRandomString(i);
//            System.out.println(random);
            HuffmanEncoder encoder = new HuffmanEncoder(random);
            byte[] encoded = encoder.encode(random);
            HuffmanDecoder decoder = new HuffmanDecoder(encoder);
            assertEquals(random, decoder.decode(encoded));
        }
    }

    @Test
    public void testLargeFiles() {
        // Encode file into a file
        File alice = new File("sampleTexts/alice_full.txt");
        File encoded = new File("alice_full.huffman");
        HuffmanEncoder encoder = new HuffmanEncoder(alice);
        encoder.encode(alice, encoded);

        // Decode file into a file
        File decoded = new File("alice_full_decoded.txt");
        HuffmanDecoder decoder = new HuffmanDecoder(encoder);
        decoder.decode(encoded, decoded);

        // Compare file contents
        TestUtil.assertFileContentEquals(alice, decoded);

        // clean up
        encoded.delete();
        decoded.delete();
    }
}