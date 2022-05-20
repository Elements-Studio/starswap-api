package org.starcoin.smt;

public final class Bytes implements Comparable<Bytes> {
    public static Bytes EMPTY = new Bytes(new byte[0]);

    private final byte[] value;

    public Bytes(byte[] value) {
        if (value == null)
            throw new IllegalArgumentException();
        this.value = value;
    }

    private static int compare(byte x, byte y) {
        return Byte.compare(x, y);
    }

    private static boolean equals(byte x, byte y) {
        return x == y;
    }

    public static Bytes[] reverseBytesArray(Bytes[] bytesArray) {
        for (int left = 0, right = bytesArray.length - 1; left < right; left = left + 1, right = right - 1) {
            Bytes t = bytesArray[left];
            bytesArray[left] = bytesArray[right];
            bytesArray[right] = t;
        }
        return bytesArray;
    }

    public static Bytes[] toBytesArray(byte[][] arrays) {
        if (arrays == null)
            return null;
        Bytes[] bytesArray = new Bytes[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            bytesArray[i] = new Bytes(arrays[i]);
        }
        return bytesArray;
    }

    public static boolean equals(Bytes x, Bytes y) {
        if (x == null)
            return y == null;
        return x.equals(y);
    }

    public byte[] getValue() {
        return value;
    }

    public int compareTo(Bytes that) {
        int n = Math.min(this.value.length, that.value.length);
        for (int i = 0, j = 0; i < n; i++, j++) {
            int cmp = compare(this.value[i], that.value[j]);
            if (cmp != 0)
                return cmp;
        }
        return this.value.length - that.value.length;
    }

    public boolean equals(Object ob) {
        if (this == ob)
            return true;
        if (!(ob instanceof Bytes))
            return false;
        Bytes that = (Bytes) ob;
        if (this.value.length != that.value.length)
            return false;
        for (int i = this.value.length - 1, j = that.value.length - 1; i >= 0; i--, j--)
            if (!equals(this.value[i], that.value[j]))
                return false;
        return true;
    }

    public int hashCode() {
        int h = 1;
        for (int i = this.value.length - 1; i >= 0; i--)
            h = 31 * h + (int) this.value[i];
        return h;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append("[");
        for (int i = 0; i < this.value.length; i++) {
            if (i != 0)
                sb.append(", ");
            sb.append(this.value[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
