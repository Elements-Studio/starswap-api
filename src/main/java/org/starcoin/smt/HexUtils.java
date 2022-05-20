package org.starcoin.smt;

import java.util.ArrayList;
import java.util.List;

public class HexUtils {

    public static byte hexToByte(String h) {
        return (byte) Integer.parseInt(h, 16);
    }

    public static byte[][] hexArrayToByteArrays(String[] hs) {
        List<byte[]> bytesList = new ArrayList<>(hs.length);
        for (String h : hs) {
            bytesList.add(hexToByteArray(h));
        }
        return bytesList.toArray(new byte[0][]);
    }

    public static byte[] hexToByteArray(String h) {
        String tmp = h.substring(0, 2);
        if (tmp.equals("0x")) {
            h = h.substring(2);
        }
        int hexlen = h.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            h = "0" + h;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(h.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static String byteListToHexWithPrefix(List<Byte> bytes) {
        return "0x" + byteListToHex(bytes);
    }

    public static String byteListToHex(List<Byte> bytes) {
        byte[] byteArray = toPrimitive(bytes.toArray(new Byte[0]));
        return byteArrayToHex(byteArray);
    }

    public static byte[] toPrimitive(final Byte[] bytes) {
        if (bytes == null) {
            return null;
        } else if (bytes.length == 0) {
            return new byte[0];
        }
        final byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i].byteValue();
        }
        return result;
    }

    public static String byteArrayToHexWithPrefix(byte[] bytes) {
        return "0x" + byteArrayToHex(bytes);
    }

    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1) {
            String hex1 = Integer.toHexString((bytes[index] >> 4) & 0xF);
            String hex2 = Integer.toHexString(bytes[index] & 0xF);
            result.append(hex1);
            result.append(hex2);
        }
        return result.toString();
    }
}

