package org.starcoin.smt;

public class ByteUtils {
    private ByteUtils() {
    }

    public static int countCommonPrefix(byte[] data1, byte[] data2) {
        int count = 0;
        for (int i = 0; i < data1.length * 8; i++) {
            if (getBitAtFromMSB(data1, i) == getBitAtFromMSB(data2, i)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static int countSetBits(byte[] data) {
        int count = 0;
        for (int i = 0; i < data.length * 8; i++) {
            if (getBitAtFromMSB(data, i) == 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * getBitAtFromMSB gets the bit at an offset from the most significant bit
     *
     * @param data
     * @param position
     * @return
     */
    public static int getBitAtFromMSB(byte[] data, int position) {
        if (((data[position / 8]) & (1 << (8 - 1 - position % 8))) > 0) {
            return 1;
        }
        return 0;
    }

    /**
     * setBitAtFromMSB sets the bit at an offset from the most significant bit
     *
     * @param data
     * @param position
     */
    public static void setBitAtFromMSB(byte[] data, int position) {
        int n = data[position / 8];
        n |= 1 << (8 - 1 - position % 8);
        data[position / 8] = (byte) n;
    }


    public static byte[][] reverseByteArrays(byte[][] arrays) {
        for (int left = 0, right = arrays.length - 1; left < right; left = left + 1, right = right - 1) {
            byte[] t = arrays[left];
            arrays[left] = arrays[right];
            arrays[right] = t;
        }
        return arrays;
    }

    /**
     * Returns the values from each provided array combined into a single array. For example, {@code
     * concat(new byte[] {a, b}, new byte[] {}, new byte[] {c}} returns the array {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code byte} arrays
     * @return a single array containing all the values from the source arrays, in order
     */
    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }

}
