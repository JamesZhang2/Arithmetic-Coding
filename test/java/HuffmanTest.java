import coding.huffman.HuffmanEncoder;
import org.junit.jupiter.api.Test;

class HuffmanTest {
    @Test
    public void test() {
        HuffmanEncoder encoder = new HuffmanEncoder("aaaabbbcc");
        System.out.println(encoder.codeTable());
    }
}