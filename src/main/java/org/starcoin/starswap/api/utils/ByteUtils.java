package org.starcoin.starswap.api.utils;

public class ByteUtils {
    private ByteUtils() {
    }

    public static int compareBytes(byte[] bytes1, byte[] bytes2) {
        int end1 = bytes1.length;
        int end2 = bytes2.length;
        for (int i = 0; i < end1; i++) {
            int byte1 = bytes1[i] & 0xFF;
            if (i >= end2) {
                return 1;
            }
            int byte2 = bytes2[i] & 0xFF;
            if (byte1 > byte2) {
                return 1;
            }
            if (byte1 < byte2) {
                return -1;
            }
        }
        if (end2 > end1) {
            return -1;
        }
        return 0;
    }
}
