package org.starcoin.smt;

import com.novi.serde.SerializationError;
import org.starcoin.types.DataPath;

import java.nio.charset.StandardCharsets;

public class StarcoinTreeHasher extends AbstractTreeHasher {

    public static final String PREFIX_STARCOIN = "STARCOIN::";
    public static final String SPARSE_MERKLE_LEAF_NODE = "SparseMerkleLeafNode";
    public static final String SPARSE_MERKLE_INTERNAL_NODE = "SparseMerkleInternalNode";
    public static final byte[] SPARSE_MERKLE_PLACEHOLDER_HASH = createLiteralHash("SPARSE_MERKLE_PLACEHOLDER_HASH");

    public StarcoinTreeHasher() {
        this(new Sha3Digest256Hasher());
    }

    public StarcoinTreeHasher(Hasher hasher) {
        super(hasher);
    }

    private static byte[] createLiteralHash(String word) {
        byte[] wordBytes = word.getBytes(StandardCharsets.UTF_8);
        if (wordBytes.length <= 32) {
            int lenZero = 32 - wordBytes.length;
            byte[] zeroBytes = new byte[lenZero];
            return ByteUtils.concat(wordBytes, zeroBytes);
        }
        throw new IllegalArgumentException("literal hash wrong length");
    }

    @Override
    public Bytes path(Bytes bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<Bytes, Bytes> digestLeaf(Bytes path, Bytes leafData) {
        byte[] bcs;
        try {
            bcs = new Node(path.getValue(), leafData.getValue()).bcsSerialize();
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
        Bytes data = new Bytes(ByteUtils.concat(prefixHash(SPARSE_MERKLE_LEAF_NODE).getValue(), bcs));
        return new Pair<>(digest(data), data);
    }

    @Override
    public Pair<Bytes, Bytes> parseLeaf(Bytes bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLeaf(Bytes bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<Bytes, Bytes> digestNode(Bytes leftData, Bytes rightData) {
        byte[] bcs;
        try {
            bcs = new Node(leftData.getValue(), rightData.getValue()).bcsSerialize();
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
        Bytes data = new Bytes(ByteUtils.concat(prefixHash(SPARSE_MERKLE_INTERNAL_NODE).getValue(), bcs));
        return new Pair<>(digest(data), data);
    }

    @Override
    public Pair<Bytes, Bytes> parseNode(Bytes bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bytes placeholder() {
        return new Bytes(SPARSE_MERKLE_PLACEHOLDER_HASH);
    }

    @Override
    public int leafDataSize() {
        throw new UnsupportedOperationException();
    }

    //todo
//    public Bytes dataPathHash(DataPath dataPath) {
//        try {
//            byte[] bcs = dataPath.bcsSerialize();
//            return this.hasher.hash(new Bytes(bcs));
//        } catch (SerializationError e) {
//            throw new IllegalArgumentException(e);
//        }
//    }

    private Bytes prefixHash(String content) {
        return hasher.hash(new Bytes(ByteUtils.concat(PREFIX_STARCOIN.getBytes(StandardCharsets.UTF_8),
                content.getBytes(StandardCharsets.UTF_8))));
    }

    public static class Node {
        private final byte[] hash1;
        private final byte[] hash2;

        public Node(byte[] hash1, byte[] hash2) {
            this.hash1 = hash1;
            this.hash2 = hash2;
        }

        static void serialize_vector_u8(byte[] value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
            serializer.serialize_len(value.length);
            for (byte item : value) {
                serializer.serialize_u8(item);
            }
        }

        public byte[] getHash1() {
            return hash1;
        }

        public byte[] getHash2() {
            return hash2;
        }

        public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
            com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
            serialize(serializer);
            return serializer.get_bytes();
        }

        public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
            serializer.increase_container_depth();
            serialize_vector_u8(hash1, serializer);
            serialize_vector_u8(hash2, serializer);
            serializer.decrease_container_depth();
        }
    }
}
