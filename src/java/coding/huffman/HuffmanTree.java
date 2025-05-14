package coding.huffman;

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
}
