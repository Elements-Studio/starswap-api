package org.starcoin.smt;

public abstract class AbstractTreeHasher {
    protected final Hasher hasher;
    protected final Bytes zeroValue;

    public AbstractTreeHasher(Hasher hasher) {
        this.hasher = hasher;
        byte[] z = new byte[this.hasher.size()];
        //Arrays.fill(z, (byte) 0);
        this.zeroValue = new Bytes(z);
    }

    public Bytes digest(Bytes data) {
        return this.hasher.hash(data);
    }

    public boolean isKeyRelatedWithLeafData(Bytes key, Bytes leafData) {
        return !isKeyUnrelatedWithLeafData(key, leafData);
    }

    public boolean isKeyUnrelatedWithLeafData(Bytes key, Bytes leafData) {
        if (leafData == null || leafData.getValue().length == 0) {
            return true;
        }
        Bytes path = this.path(key);
        return isPathUnrelatedWithLeafData(path, leafData);
    }

    public boolean isPathUnrelatedWithLeafData(Bytes path, Bytes leafData) {
        boolean unrelated = false;
        if (leafData == null || leafData.getValue().length == 0) {
            unrelated = true;
        } else {
            Pair<Bytes, Bytes> p = parseLeaf(leafData);

            if (!Bytes.equals(p.getItem1(), path)) {
                unrelated = true;
            }
        }
        return unrelated;
    }

    public int pathSize() {
        return this.hasher.size();
    }

    public Hasher getHasher() {
        return hasher;
    }

    public int hasherSize() {
        return this.hasher.size();
    }


    public abstract Bytes path(Bytes key);

    public abstract Bytes valueHash(Bytes value);

    public abstract Pair<Bytes, Bytes> digestLeaf(Bytes path, Bytes leafData);

    public abstract Pair<Bytes, Bytes> parseLeaf(Bytes data);

    public abstract boolean isLeaf(Bytes data);

    public abstract Pair<Bytes, Bytes> digestNode(Bytes leftData, Bytes rightData);

    public abstract Pair<Bytes, Bytes> parseNode(Bytes data);

    public abstract Bytes placeholder();

    public abstract int leafDataSize();
}
