package coding.huffman;

import coding.AbstractDecoder;
import coding.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the decoder for Huffman coding.
 */
public class HuffmanDecoder extends AbstractDecoder {
    private final HuffmanTree tree;

    /**
     * Creates a Huffman decoder using the same codes as the given encoder.
     */
    public HuffmanDecoder(HuffmanEncoder encoder) {
        this.tree = encoder.getTree();
    }

    /**
     * Creates a Huffman decoder based on the given codes for each character.
     * Requires: codes.size() == 129,
     * where codes[0] to codes[127] are codewords for the ASCII characters 0-127
     * and codes[128] is the codeword for the end-of-file symbol.
     * Each codeword is represented as a list of 1s and 0s.
     * If a symbol is unused, the codeword must be an empty list.
     */
    public HuffmanDecoder(List<List<Integer>> codes) {
        assert codes.size() == 129;
        this.tree = HuffmanTree.generateTreeFromCodes(codes);
    }

    /**
     * Creates a Huffman encoder based on the given codes for each character.
     * Requires: codeMap maps characters to their codewords, represented as a String of 1s and 0s.
     * Only characters that are used at least once are in the codeMap.
     * codeMap[128] is the codeword for the end-of-file symbol.
     */
    public HuffmanDecoder(Map<Character, String> codeMap) {
        List<List<Integer>> codes = new ArrayList<>();
        for (char c = 0; c < 129; c++) {
            List<Integer> code = new ArrayList<>();
            String codeStr = codeMap.get(c);
            if (codeStr != null) {
                for (int i = 0; i < codeStr.length(); i++) {
                    assert codeStr.charAt(i) == '0' || codeStr.charAt(i) == '1';
                    code.add(codeStr.charAt(i) == '0' ? 0 : 1);
                }
            }
            codes.add(code);
        }
        this.tree = HuffmanTree.generateTreeFromCodes(codes);
    }

    @Override
    public String decode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        HuffmanTree cur = tree;
        int idx = 0;
        while (true) {
            int b = getBit(bytes, idx++);
            if (b == 0) {
                if (cur.left == null) {
                    throw new IllegalArgumentException("Failed to decode at bit " + idx + ": Bit is 0 but left tree is null");
                }
                cur = cur.left;
            } else {
                // b == 1
                if (cur.right == null) {
                    throw new IllegalArgumentException("Failed to decode at bit " + idx + ": Bit is 0 but left tree is null");
                }
                cur = cur.right;
            }
            if (cur.c != null) {
                // reached character, next character is c
                if (cur.c == 128) {
                    // reached end-of-file character
                    return sb.toString();
                } else {
                    sb.append(cur.c);
                    // reset cur to point to root to start decoding next character
                    cur = tree;
                }
            }
        }
    }

    /**
     * Decode the string str and return the result.
     * Requires: str must be a string of 1s and 0s.
     */
    public String decodeString(String str) {
        List<Integer> encoded = new ArrayList<>();
        for (char c : str.toCharArray()) {
            assert c == '0' || c == '1';
            encoded.add(c - '0');
        }
        return decode(Util.toByteArray(encoded));
    }
}
