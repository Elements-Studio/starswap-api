package org.starcoin.smt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SparseCompactMerkleProof {

    /**
     * SideNodes is an array of the side nodes leading up to the leaf of the proof.
     */
    private Bytes[] sideNodes;


    /**
     * NonMembershipLeafData is the data of the unrelated leaf at the position
     * of the key being proven, in the case of a non-membership proof. For
     * membership proofs, is nil.
     */
    private Bytes nonMembershipLeafData;


    /**
     * SiblingData is the data of the sibling node to the leaf being proven,
     * required for updatable proofs. For un-updatable proofs, is nil.
     */
    private Bytes siblingData;

    /**
     * BitMask, in the case of a compact proof, is a bit mask of the sidenodes
     * of the proof where an on-bit indicates that the side node at the bit's
     * index is a placeholder. This is only set if the proof is compact.
     */
    private Bytes bitMask;

    /**
     * NumSideNodes, in the case of a compact proof, indicates the number of
     * sidenodes in the proof when decompacted. This is only set if the proof is compact.
     */
    private int numberOfSideNodes;

    public SparseCompactMerkleProof() {
    }

    public SparseCompactMerkleProof(Bytes[] sideNodes, Bytes nonMembershipLeafData, Bytes siblingData, Bytes bitMask, int numberOfSideNodes) {
        this.sideNodes = sideNodes;
        this.nonMembershipLeafData = nonMembershipLeafData;
        this.siblingData = siblingData;
        this.bitMask = bitMask;
        this.numberOfSideNodes = numberOfSideNodes;
    }

    /**
     * CompactProof compacts a proof, to reduce its size.
     *
     * @param proof
     * @param hasher
     * @return
     */
    public static SparseCompactMerkleProof compactProof(SparseMerkleProof proof, Hasher hasher) {
        AbstractTreeHasher th = new TreeHasher(hasher);
        return compactProof(proof, th);
    }

    public static SparseCompactMerkleProof compactProof(SparseMerkleProof proof, AbstractTreeHasher th) {
        if (!proof.sanityCheck(th)) {
            throw new IllegalArgumentException("proof sanityCheck failed.");
        }

        byte[] bitMask = new byte[(int) Math.ceil(((double) (proof.getSideNodes().length)) / ((double) 8))];
        List<Bytes> compactedSideNodes = new ArrayList<>();
        for (int i = 0; i < proof.getSideNodes().length; i++) {
            Bytes node = new Bytes(Arrays.copyOf(proof.getSideNodes()[i].getValue(), th.hasherSize()));
            if (Bytes.equals(node, th.placeholder())) {
                ByteUtils.setBitAtFromMSB(bitMask, i);
            } else {
                compactedSideNodes.add(node);
            }
        }

        return new SparseCompactMerkleProof(compactedSideNodes.toArray(new Bytes[0]), proof.getNonMembershipLeafData(),
                proof.getSiblingData(), new Bytes(bitMask), proof.getSideNodes().length);
    }


    public static SparseMerkleProof decompactProof(SparseCompactMerkleProof proof, Hasher hasher) {
        AbstractTreeHasher th = new TreeHasher(hasher);
        return decompactProof(proof, th);
    }

    public static SparseMerkleProof decompactProof(SparseCompactMerkleProof proof, AbstractTreeHasher th) {
        if (!proof.sanityCheck(th)) {
            throw new IllegalArgumentException("proof sanityCheck failed.");
        }

        Bytes[] decompactedSideNodes = new Bytes[proof.numberOfSideNodes];
        int position = 0;
        for (int i = 0; i < proof.numberOfSideNodes; i++) {
            if (ByteUtils.getBitAtFromMSB(proof.bitMask.getValue(), i) == 1) {
                decompactedSideNodes[i] = th.placeholder();
            } else {
                decompactedSideNodes[i] = proof.sideNodes[position];
                position++;
            }
        }

        return new SparseMerkleProof(decompactedSideNodes, proof.nonMembershipLeafData, proof.siblingData);
    }

    public boolean sanityCheck(AbstractTreeHasher th) {
        // Do a basic sanity check on the proof on the fields of the proof specific to
        // the compact proof only.
        //
        // When the proof is de-compacted and verified, the sanity check for the
        // de-compacted proof should be executed.

        // Compact proofs: check that NumSideNodes is within the right range.
        return this.numberOfSideNodes >= 0 && this.numberOfSideNodes <= th.pathSize() * 8 &&
                // Compact proofs: check that the length of the bit mask is as expected
                // according to NumSideNodes.
                this.bitMask.getValue().length == (int) Math.ceil(((double) this.numberOfSideNodes) / (double) 8) &&
                // Compact proofs: check that the correct number of side nodes have been
                // supplied according to the bit mask.
                (this.numberOfSideNodes <= 0 || this.sideNodes.length == this.numberOfSideNodes - ByteUtils.countSetBits(this.bitMask.getValue()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SparseCompactMerkleProof that = (SparseCompactMerkleProof) o;
        return numberOfSideNodes == that.numberOfSideNodes && Arrays.equals(sideNodes, that.sideNodes) && Objects.equals(nonMembershipLeafData, that.nonMembershipLeafData) && Objects.equals(siblingData, that.siblingData) && Objects.equals(bitMask, that.bitMask);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nonMembershipLeafData, siblingData, bitMask, numberOfSideNodes);
        result = 31 * result + Arrays.hashCode(sideNodes);
        return result;
    }

    @Override
    public String toString() {
        return "SparseCompactMerkleProof{" +
                "sideNodes=" + Arrays.toString(sideNodes) +
                ", nonMembershipLeafData=" + nonMembershipLeafData +
                ", siblingData=" + siblingData +
                ", bitMask=" + bitMask +
                ", numberOfSideNodes=" + numberOfSideNodes +
                '}';
    }

    public Bytes[] getSideNodes() {
        return sideNodes;
    }

    public void setSideNodes(Bytes[] sideNodes) {
        this.sideNodes = sideNodes;
    }

    public Bytes getNonMembershipLeafData() {
        return nonMembershipLeafData;
    }

    public void setNonMembershipLeafData(Bytes nonMembershipLeafData) {
        this.nonMembershipLeafData = nonMembershipLeafData;
    }

    public Bytes getSiblingData() {
        return siblingData;
    }

    public void setSiblingData(Bytes siblingData) {
        this.siblingData = siblingData;
    }

    public Bytes getBitMask() {
        return bitMask;
    }

    public void setBitMask(Bytes bitMask) {
        this.bitMask = bitMask;
    }

    public int getNumberOfSideNodes() {
        return numberOfSideNodes;
    }

    public void setNumberOfSideNodes(int numberOfSideNodes) {
        this.numberOfSideNodes = numberOfSideNodes;
    }

}
