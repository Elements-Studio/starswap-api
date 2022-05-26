package org.starcoin.smt;

import com.novi.serde.SerializationError;

import java.nio.charset.StandardCharsets;

public class StarcoinTreeHasher extends AbstractTreeHasher {

    public static final String STARCOIN_HASH_PREFIX = "STARCOIN::";
    public static final String SPARSE_MERKLE_LEAF_NODE = "SparseMerkleLeafNode";
    public static final String SPARSE_MERKLE_INTERNAL_NODE = "SparseMerkleInternalNode";
    public static final byte[] SPARSE_MERKLE_PLACEHOLDER_HASH = createLiteralHash("SPARSE_MERKLE_PLACEHOLDER_HASH");
    public static final String PREFIX_NAME_BLOB = "Blob";

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

    static byte[] bcsSerializeBytes(byte[] bytes) throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize_vector_u8(bytes, serializer);
        return serializer.get_bytes();
    }

    static void serialize_vector_u8(byte[] value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.serialize_len(value.length);
        for (byte item : value) {
            serializer.serialize_u8(item);
        }
    }

//    static void serialize_option_bytes(java.util.Optional<com.novi.serde.Bytes> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
//        if (value.isPresent()) {
//            serializer.serialize_option_tag(true);
//            serializer.serialize_bytes(value.get());
//        } else {
//            serializer.serialize_option_tag(false);
//        }
//    }

    @Override
    public Bytes path(Bytes bytes) {
        return digest(bytes);
    }

    @Override
    public Bytes valueHash(Bytes bytes) {
        try {
            Bytes salt = prefixHash(PREFIX_NAME_BLOB);
            return digest(new Bytes(ByteUtils.concat(salt.getValue(), bcsSerializeBytes(bytes.getValue()))));
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
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

    private Bytes prefixHash(String prefixName) {
        return hasher.hash(new Bytes(ByteUtils.concat(STARCOIN_HASH_PREFIX.getBytes(StandardCharsets.UTF_8),
                prefixName.getBytes(StandardCharsets.UTF_8))));
    }

    private static class Node {
        private final byte[] hash1;
        private final byte[] hash2;

        public Node(byte[] hash1, byte[] hash2) {
            this.hash1 = hash1;
            this.hash2 = hash2;
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
