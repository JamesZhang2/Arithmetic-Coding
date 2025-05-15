package coding.huffman;

import java.util.List;

/**
 * Represents a Huffman tree.
 * Going left means adding a 0 to the codeword,
 * and going right means adding a 1 to the codeword.
 */
public class HuffmanTree {
    Character c;  // not null if and only if this is a leaf; 128 represents end-of-file symbol
    HuffmanTree left;
    HuffmanTree right;

    public HuffmanTree() {
        this.c = null;
        this.left = null;
        this.right = null;
    }

    public HuffmanTree(char c) {
        this.c = c;
        this.left = null;
        this.right = null;
    }

    public HuffmanTree(HuffmanTree left, HuffmanTree right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public HuffmanTree clone() {
        HuffmanTree t = new HuffmanTree();
        t.c = c;
        if (left == null) {
            t.left = null;
        } else {
            t.left = left.clone();
        }
        if (right == null) {
            t.right = null;
        } else {
            t.right = right.clone();
        }
        return t;
    }

    @Override
    public String toString() {
        if (left == null && right == null) {
            assert c != null;
            return (c == 128 ? "EOF" : String.valueOf(c));
        } else {
            StringBuilder sb = new StringBuilder();
            if (left == null) {
                sb.append("()");
            } else {
                sb.append("(");
                sb.append(left);
                sb.append(")");
            }
            sb.append(" ");
            if (right == null) {
                sb.append("()");
            } else {
                sb.append("(");
                sb.append(right);
                sb.append(")");
            }
            return sb.toString();
        }
    }

    /**
     * Generates the Huffman tree based on the given codes.
     * Postcondition: codes is unchanged.
     */
    public static HuffmanTree generateTreeFromCodes(List<List<Integer>> codes) {
        HuffmanTree root = new HuffmanTree();
        for (char c = 0; c < codes.size(); c++) {
            if (codes.get(c).isEmpty()) {
                continue;
            }
            HuffmanTree cur = root;
            for (int i = 0; i < codes.get(c).size(); i++) {
                if (codes.get(c).get(i) == 0) {
                    // go left
                    if (cur.left == null) {
                        cur.left = new HuffmanTree();
                    }
                    cur = cur.left;
                } else if (codes.get(c).get(i) == 1) {
                    // go right
                    if (cur.right == null) {
                        cur.right = new HuffmanTree();
                    }
                    cur = cur.right;
                } else {
                    throw new IllegalArgumentException(String.format("Index %d of the codeword for %c is not 0 or 1. It's %d.", i, c, codes.get(c).get(i)));
                }
            }
            cur.c = c;
        }
        return root;
    }
}
