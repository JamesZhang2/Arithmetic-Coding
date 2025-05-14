package coding;

import java.io.*;
import java.util.List;

/**
 * A class that factors out common functions and I/O operations
 */
public abstract class AbstractEncoder implements Encoder {
    @Override
    public void encode(String text, File output) {
        try {
            if (output.createNewFile()) {
                System.out.println("Created file " + output.getName());
            }
            System.out.println("Writing output to file " + output.getName());
            OutputStream os = new FileOutputStream(output);
            os.write(encode(text));
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encode(File input, File output) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            encode(sb.toString(), output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Print a list of 1s and 0s as a binary fraction (for debugging purposes)
     */
    protected void printAsBinaryFraction(List<Integer> nums) {
        StringBuilder sb = new StringBuilder();
        sb.append("0.");
        for (int num : nums) {
            sb.append(num);
        }
        System.out.println(sb);
    }

    /**
     * Print a list of 1s and 0s as an approximate decimal fraction (for debugging purposes)
     * This is as accurate as the precision of a double
     */
    protected void printAsApproxDecimalFraction(List<Integer> nums) {
        double unit = 1;
        double ans = 0;
        for (int i = 0; i < nums.size(); i++) {
            unit /= 2;
            if (unit == 0) {
                break;
            }
            ans += nums.get(i) * unit;
        }
        System.out.println(ans);
    }

    /**
     * Converts a list of 1s and 0s to a byte array
     * where the first 8 bits form the first byte, the second 8 bits form the second byte, etc.
     * 0s are padded at the end.
     * @param nums a nonempty list of 1s and 0s
     */
    protected byte[] toByteArray(List<Integer> nums) {
        byte[] bytes = new byte[(int)Math.ceil(nums.size() / 8.0)];
        for (int i = 0; i < bytes.length; i++) {
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                if (i * 8 + j < nums.size()) {
                    b += (byte) (nums.get(i * 8 + j) << (7 - j));
                }
            }
            bytes[i] = b;
        }
        return bytes;
    }
}
