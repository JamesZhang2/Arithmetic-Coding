import java.io.File;

public class Main {
    public static void main(String[] args) {
        double[] probs = new double[127];
        probs['A'] = 0.3;
        probs['B'] = probs['A'] + 0.4;
        probs['C'] = probs['B'] + 0.2;
        for (char c = 'D'; c < probs.length; c++) {
            probs[c] = probs[c - 1];
        }
        Encoder encoder = new FixedProbEncoder(probs);
        byte[] aBytes = encoder.encode("A");  // Should be between 0.27 and 0.3
        byte[] abBytes = encoder.encode("AB");  // Should be between 0.188 and 0.21
        byte[] abcabBytes = encoder.encode("ABCAB");
        byte[] longBytes = encoder.encode("ACABBABBBCCCABAABCABCABCACABAAABCBCCCCCCBABBB");
        encoder.encode("CABBACCCCCBABA", new File("abc.txt"));
//        for (byte b : aBytes) {
//            System.out.println(b);
//        }
//        System.out.println("----------");
//        for (byte b : abBytes) {
//            System.out.println(b);
//        }

        Decoder decoder = new FixedProbDecoder(probs);
        System.out.println(decoder.decode(aBytes));
        System.out.println(decoder.decode(abBytes));
        System.out.println(decoder.decode(abcabBytes));
        System.out.println(decoder.decode(longBytes));
        System.out.println(decoder.decode(new File("abc.txt")));
        decoder.decode(new File("abc.txt"), new File("def.txt"));
    }
}
