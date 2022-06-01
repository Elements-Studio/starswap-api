package org.starcoin.bean;

import org.starcoin.utils.HexUtils;

import java.util.Arrays;

/**
 * Starcoin state proof.
 */
public class StateProof {
    /**
     * Account state's proof for global state root.
     */
    private SparseMerkleProof accountProof;

    /**
     * Account state including storage roots.
     */
    private byte[] accountState;

    /**
     * State's proof for account storage root.
     */
    private SparseMerkleProof proof;

    public StateProof() {
    }

    public static StateProof from(RpcStateWithProof stateWithProof) {
        StateProof proof = new StateProof();
        proof.setProof(SparseMerkleProof.valueOf(stateWithProof.getStateProof()));
        proof.setAccountState(HexUtils.hexToByteArray(stateWithProof.getAccountState()));
        proof.setAccountProof(SparseMerkleProof.valueOf(stateWithProof.getAccountProof()));
        return proof;
    }

    public SparseMerkleProof getAccountProof() {
        return accountProof;
    }

    public void setAccountProof(SparseMerkleProof accountProof) {
        this.accountProof = accountProof;
    }

    public byte[] getAccountState() {
        return accountState;
    }

    public void setAccountState(byte[] accountState) {
        this.accountState = accountState;
    }

    public SparseMerkleProof getProof() {
        return proof;
    }

    public void setProof(SparseMerkleProof proof) {
        this.proof = proof;
    }

    @Override
    public String toString() {
        return "StateProof{" +
                "accountProof=" + accountProof +
                ", accountState=" + (accountProof == null ? null : HexUtils.byteArrayToHex(accountState)) +
                ", proof=" + proof +
                '}';
    }

    public static class SparseMerkleProof {
        private byte[][] siblings;
        private Leaf leaf;

        private SparseMerkleProof() {
        }

        public SparseMerkleProof(byte[][] siblings, Leaf leaf) {
            this.siblings = siblings;
            this.leaf = leaf;
        }

        public static SparseMerkleProof valueOf(RpcStateWithProof.SparseMerkleProof s) {
            byte[][] siblings = HexUtils.hexArrayToByteArrays(s.getSiblings());
            return new SparseMerkleProof(siblings, Leaf.valueOf(s.getLeaf()));
        }

        public byte[][] getSiblings() {
            return siblings;
        }

        public void setSiblings(byte[][] siblings) {
            this.siblings = siblings;
        }

        public Leaf getLeaf() {
            return leaf;
        }

        public void setLeaf(Leaf leaf) {
            this.leaf = leaf;
        }

        @Override
        public String toString() {
            return "SparseMerkleProof{" +
                    "siblings=" + Arrays.toString(siblings == null ? null : HexUtils.bytesArrayToHexArray(siblings)) +
                    ", leaf=" + leaf +
                    '}';
        }
    }

    /**
     * Leaf data.
     */
    public static class Leaf {
        /**
         * Path(key hash).
         */
        private byte[] path;

        /**
         * Value hash.
         */
        private byte[] valueHash;

        public Leaf() {
        }

        public Leaf(byte[] path, byte[] valueHash) {
            this.path = path;
            this.valueHash = valueHash;
        }

        public static Leaf valueOf(String[] hexArray) {
            if (hexArray == null || hexArray.length != 2) {
                throw new IllegalArgumentException("hexArray");
            }
            byte[] p = HexUtils.hexToByteArray(hexArray[0]);
            byte[] vh = HexUtils.hexToByteArray(hexArray[1]);
            return new Leaf(p, vh);
        }

        public byte[] getPath() {
            return path;
        }

        public void setPath(byte[] path) {
            this.path = path;
        }

        public byte[] getValueHash() {
            return valueHash;
        }

        public void setValueHash(byte[] valueHash) {
            this.valueHash = valueHash;
        }

        @Override
        public String toString() {
            return "Leaf{" +
                    "path=" + (path == null ? null : HexUtils.byteArrayToHex(path)) +
                    ", valueHash=" + (valueHash == null ? null : HexUtils.byteArrayToHex(valueHash)) +
                    '}';
        }
    }
}
