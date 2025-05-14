import coding.huffman.HuffmanEncoder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class HuffmanTest {
    @Test
    public void test() {
        HuffmanEncoder encoder = new HuffmanEncoder("aaaabbbcc");
        System.out.println(encoder.codeTable());

        Map<Character, String> codeMap = new HashMap<>();
        codeMap.put('A', "10");
        codeMap.put('B', "01");
        codeMap.put('C', "11");
        codeMap.put((char)128, "00");
        HuffmanEncoder encoder2 = new HuffmanEncoder(codeMap);
        System.out.println(encoder2.codeTable());
    }
}