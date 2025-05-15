package coding.huffman;

import coding.AbstractEncoder;
import coding.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the encoder for Huffman coding.
 */
public class HuffmanEncoder extends AbstractEncoder {
    private final List<List<Integer>> codes;
    private final HuffmanTree tree;

    @Override
    public byte[] encode(String text) {
        return Util.toByteArray(encodeAsList(text));
    }

    /**
     * Encode the text as a string of 1s and 0s.
     * Unlike arithmetic coding, Huffman coding is a symbol code,
     * so substrings of the output can be matched to each symbol of the input text.
     */
    public String encodeAsString(String text) {
        return encodeAsList(text).stream().map(String::valueOf).collect(Collectors.joining());
    }

    /**
     * Encode the text as a list of 1s and 0s.
     */
    private List<Integer> encodeAsList(String text) {
        List<Integer> encoded = new ArrayList<>();
        for (char c : text.toCharArray()) {
            if (codes.get(c).isEmpty()) {
                throw new IllegalArgumentException("Unsupported character: " + c);
            }
            encoded.addAll(codes.get(c));
        }
        encoded.addAll(codes.get(128));
        return encoded;
    }

    /**
     * Creates a Huffman encoder based on the text to encode.
     * Requires: text is not empty.
     */
    public HuffmanEncoder(String text) {
        assert !text.isEmpty();
        int[] freqs = Util.countFreqs(text);
        // Greedy algorithm: take the two least frequent symbols,
        // merge the two symbols into a new symbol by creating a new node with those symbols as children.
        // Repeat until there is only one symbol left.
        codes = new ArrayList<>();
        for (int i = 0; i < freqs.length; i++) {
            codes.add(new LinkedList<>());
        }

        // Elements of pq are (group number, freq) pairs, ordered in increasing order by freq
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p[1]));
        // Only characters with nonzero frequencies are added
        for (int c = 0; c < freqs.length; c++) {
            if (freqs[c] > 0) {
                pq.add(new int[]{c, freqs[c]});
            }
        }
        // initially, every symbol is its own node.
        List<HuffmanTree> trees = new ArrayList<>();
        for (int i = 0; i < 129; i++) {
            trees.add(new HuffmanTree((char)i));
        }

        // Union-find data structure: Each character initially belongs to their own group,
        // but characters can be merged into the same group.
        // When merging, the group number of the bigger group becomes the new group number.

        // groupSizes[g] is the size of group g
        int[] groupSizes = new int[freqs.length];
        Arrays.fill(groupSizes, 1);
        // groupToChars[g] is the list of characters belonging to group g
        List<List<Integer>> groupToChars = new ArrayList<>();
        for (int i = 0; i < freqs.length; i++) {
            List<Integer> lst = new ArrayList<>();
            lst.add(i);
            groupToChars.add(lst);
        }

        while (pq.size() > 1) {
            int[] p1 = pq.poll();
            int[] p2 = pq.poll();
            assert p1 != null && p2 != null;
            int g1 = p1[0];
            int g2 = p2[0];
            // prepend a 0 to all chars belonging to group g1 and a 1 to all chars belonging to group g2
            for (int c : groupToChars.get(g1)) {
                codes.get(c).addFirst(0);
            }
            for (int c : groupToChars.get(g2)) {
                codes.get(c).addFirst(1);
            }
            // create new tree node with g1's tree as left child and g2's tree as right child
            HuffmanTree newNode = new HuffmanTree(trees.get(g1), trees.get(g2));
            // merge the two groups; the large group consumes the smaller group
            if (groupSizes[g1] >= groupSizes[g2]) {
                // new group name is g1
                for (int c : groupToChars.get(g2)) {
                    groupToChars.get(g1).add(c);
                }
                groupToChars.get(g2).clear();
                groupSizes[g1] += groupSizes[g2];
                groupSizes[g2] = 0;
                pq.add(new int[]{g1, p1[1] + p2[1]});  // new frequency is the combined frequency of the two groups
                trees.set(g1, newNode);
                trees.set(g2, null);
            } else {
                // new group name is g2
                for (int c : groupToChars.get(g1)) {
                    groupToChars.get(g2).add(c);
                }
                groupToChars.get(g1).clear();
                groupSizes[g2] += groupSizes[g1];
                groupSizes[g1] = 0;
                pq.add(new int[]{g2, p1[1] + p2[1]});  // new frequency is the combined frequency of the two groups
                trees.set(g2, newNode);
                trees.set(g1, null);
            }
        }
        assert pq.size() == 1;
        int[] pair = pq.poll();
        assert pair[1] == text.length() + 1;  // final node should have all the frequency (+1 for end-of-file)
        this.tree = trees.get(pair[0]);
//        System.out.println(this.tree);
    }

    /**
     * Creates a Huffman encoder based on the given codes for each character.
     * Requires: codes.size() == 129,
     * where codes[0] to codes[127] are codewords for the ASCII characters 0-127
     * and codes[128] is the codeword for the end-of-file symbol.
     * Each codeword is represented as a list of 1s and 0s.
     * If a symbol is unused, the codeword must be an empty list.
     */
    public HuffmanEncoder(List<List<Integer>> codes) {
        assert codes.size() == 129;
        this.codes = new ArrayList<>();
        for (char c = 0; c < 129; c++) {
            this.codes.add(new LinkedList<>(codes.get(c)));
        }
        this.tree = HuffmanTree.generateTreeFromCodes(codes);
    }

    /**
     * Creates a Huffman encoder based on the given codes for each character.
     * Requires: codeMap maps characters to their codewords, represented as a String of 1s and 0s.
     * Only characters that are used at least once are in the codeMap.
     * codeMap[128] is the codeword for the end-of-file symbol.
     */
    public HuffmanEncoder(Map<Character, String> codeMap) {
        codes = new ArrayList<>();
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
//        System.out.println(codes);
        this.tree = HuffmanTree.generateTreeFromCodes(codes);
//        System.out.println(this.tree);
    }

    /**
     * @return a map from character to codeword.
     * Only shows characters whose codewords are not empty (i.e. they appear at least once).
     * Character 128 represents end-of-text.
     */
    public Map<Character, String> codeTable() {
        Map<Character, String> map = new HashMap<>();
        for (char c = 0; c < codes.size(); c++) {
            if (!codes.get(c).isEmpty()) {
                map.put(c, codes.get(c).stream().map(String::valueOf).collect(Collectors.joining()));
            }
        }
        return map;
    }

    /**
     * @return a copy of the Huffman tree used by this Huffman encoder
     */
    public HuffmanTree getTree() {
        return tree.clone();
    }
}
