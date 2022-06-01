package org.starcoin.smt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.starcoin.smt.SparseMerkleTree.DEFAULT_VALUE;
import static org.starcoin.smt.SparseMerkleTree.RIGHT;

/**
 * SparseMerkleProof is a Merkle proof for an element in a SparseMerkleTree.
 */
public class SparseMerkleProof {

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

    public SparseMerkleProof() {
    }

    public SparseMerkleProof(Bytes[] sideNodes, Bytes nonMembershipLeafData, Bytes siblingData) {
        this.sideNodes = sideNodes;
        this.nonMembershipLeafData = nonMembershipLeafData;
        this.siblingData = siblingData;
    }

    public static Bytes updateRoot(AbstractTreeHasher th, byte[] key, byte[] value, Bytes[] sideNodes, byte[] oldLeafData) {
        return updateRoot(th, new Bytes(key), new Bytes(value), sideNodes, new Bytes(oldLeafData));
    }

    public static Bytes updateRoot(AbstractTreeHasher th, byte[] key, byte[] value, Bytes[] sideNodes, Bytes oldLeafData) {
        return updateRoot(th, new Bytes(key), new Bytes(value), sideNodes, oldLeafData);
    }

    public static Bytes updateRoot(AbstractTreeHasher th, Bytes key, Bytes value, Bytes[] sideNodes, Bytes oldLeafData) {
        return updateRootByPath(th, th.path(key), value, sideNodes, oldLeafData);
    }

    public static Bytes updateRootByPath(AbstractTreeHasher th, byte[] path, byte[] value, Bytes[] sideNodes, byte[] oldLeafData) {
        return updateRootByPath(th, new Bytes(path), new Bytes(value), sideNodes, new Bytes(oldLeafData));
    }

    public static Bytes updateRootByPath(AbstractTreeHasher th, byte[] path, byte[] value, Bytes[] sideNodes, Bytes oldLeafData) {
        return updateRootByPath(th, new Bytes(path), new Bytes(value), sideNodes, oldLeafData);
    }

    public static Bytes updateRootByPath(AbstractTreeHasher th, Bytes path, Bytes value, Bytes[] sideNodes, Bytes oldLeafData) {
        Bytes pathNode0;      //pathNodes[0]
        if (oldLeafData == null || oldLeafData.getValue().length == 0) {
            pathNode0 = th.placeholder();
        } else {
            pathNode0 = th.digest(oldLeafData);
        }
        Bytes valueHash = th.valueHash(value);
        Pair<Bytes, Bytes> leafPair = th.digestLeaf(path, valueHash);
        Bytes currentHash = leafPair.getItem1();
        Bytes currentData = leafPair.getItem2();
        // if err := smt.nodes.Set(currentHash, currentData); err != nil {
        // 	return nil, err
        // }
        currentData = currentHash;

        // If the leaf node that sibling nodes lead to has a different actual path
        // than the leaf node being updated, we need to create an intermediate node
        // with this leaf node and the new leaf node as children.
        //
        // First, get the number of bits that the paths of the two leaf nodes share
        // in common as a prefix.
        int depth = th.pathSize() * 8;
        int commonPrefixCount;
        //Bytes oldValueHash = null;
        if (oldLeafData == null || oldLeafData.getValue().length == 0) {
            commonPrefixCount = depth;
        } else {
            Bytes actualPath;
            Pair<Bytes, Bytes> p = th.parseLeaf(oldLeafData);
            actualPath = p.getItem1();
            //oldValueHash = p.getItem2();
            commonPrefixCount = ByteUtils.countCommonPrefix(path.getValue(), actualPath.getValue());
        }
        if (commonPrefixCount != depth) {
            Pair<Bytes, Bytes> p;
            if (ByteUtils.getBitAtFromMSB(path.getValue(), commonPrefixCount) == RIGHT) {
                p = th.digestNode(pathNode0, currentData);
            } else {
                p = th.digestNode(currentData, pathNode0);
            }
            currentHash = p.getItem1();
            //currentData = p.getItem2();
            // err := smt.nodes.Set(currentHash, currentData)
            // if err != nil {
            // 	return nil, err
            // }
            currentData = currentHash;
        } //else if (oldValueHash != null) {
        // // Short-circuit if the same value is being set
        // if bytes.Equal(oldValueHash, valueHash) {
        // 	return smt.root, nil
        // }
        // // If an old leaf exists, remove it
        // if err := smt.nodes.Delete(pathNodes[0]); err != nil {
        // 	return nil, err
        // }
        // if err := smt.values.Delete(path); err != nil {
        // 	return nil, err
        // }
        //}
        // // All remaining path nodes are orphaned
        // for i := 1; i < len(pathNodes); i++ {
        // 	if err := smt.nodes.Delete(pathNodes[i]); err != nil {
        // 		return nil, err
        // 	}
        // }
        // The offset from the bottom of the tree to the start of the side nodes.
        // Note: i-offsetOfSideNodes is the index into sideNodes[]
        int offsetOfSideNodes = depth - sideNodes.length;
        for (int i = 0; i < depth; i++) {
            Bytes sideNode;
            if (i - offsetOfSideNodes < 0 || sideNodes[i - offsetOfSideNodes] == null) {
                if (commonPrefixCount != depth && commonPrefixCount > depth - 1 - i) {
                    // If there are no side nodes at this height, but the number of
                    // bits that the paths of the two leaf nodes share in common is
                    // greater than this depth, then we need to build up the tree
                    // to this depth with placeholder values at siblings.
                    sideNode = th.placeholder();
                } else {
                    continue;
                }
            } else {
                sideNode = sideNodes[i - offsetOfSideNodes];
            }
            Pair<Bytes, Bytes> p;
            if (ByteUtils.getBitAtFromMSB(path.getValue(), depth - 1 - i) == RIGHT) {
                p = th.digestNode(sideNode, currentData);
            } else {
                p = th.digestNode(currentData, sideNode);
            }
            currentHash = p.getItem1();
            // currentData = p.getItem2();
            // err := smt.nodes.Set(currentHash, currentData)
            // if err != nil {
            // 	return nil, err
            // }
            currentData = currentHash;
        }
        // if err := smt.values.Set(path, value); err != nil {
        // 	return nil, err
        // }
        return currentHash;
    }


    public static boolean verifyProof(SparseMerkleProof proof, Bytes root, byte[] key, byte[] value, Hasher hasher) {
        return verifyProofWithUpdates(proof, root, new Bytes(key), new Bytes(value), hasher).getItem1();
    }

    public static boolean verifyProof(SparseMerkleProof proof, Bytes root, Bytes key, Bytes value, Hasher hasher) {
        return verifyProofWithUpdates(proof, root, key, value, hasher).getItem1();
    }

    public static boolean verifyProof(SparseMerkleProof proof, Bytes root, byte[] key, byte[] value, AbstractTreeHasher th) {
        return verifyProofWithUpdates(proof, root, new Bytes(key), new Bytes(value), th).getItem1();
    }

    public static boolean verifyProof(SparseMerkleProof proof, Bytes root, Bytes key, Bytes value, AbstractTreeHasher th) {
        return verifyProofWithUpdates(proof, root, key, value, th).getItem1();
    }

    private static Pair<Boolean, List<Pair<Bytes, Bytes>>> verifyProofWithUpdates(SparseMerkleProof proof, Bytes root, Bytes key, Bytes value, Hasher hasher) {
        AbstractTreeHasher th = new TreeHasher(hasher);
        return verifyProofWithUpdates(proof, root, key, value, th);
    }

    private static Pair<Boolean, List<Pair<Bytes, Bytes>>> verifyProofWithUpdates(SparseMerkleProof proof, Bytes root, Bytes key, Bytes value, AbstractTreeHasher th) {
        Bytes path = th.path(key);
        if (!proof.sanityCheck(th)) {
            return new Pair<>(false, null);
        }
        List<Pair<Bytes, Bytes>> updates = new ArrayList<>();

        // Determine what the leaf hash should be.
        Bytes currentHash;
        Bytes currentData;
        if (value == null || Bytes.equals(value, DEFAULT_VALUE)) { // Non-membership proof.
            if (proof.nonMembershipLeafData == null) { // Leaf is a placeholder value.
                currentHash = th.placeholder();
            } else { // Leaf is an unrelated leaf.
                Pair<Bytes, Bytes> p = th.parseLeaf(proof.nonMembershipLeafData);
                Bytes actualPath = p.getItem1();
                Bytes valueHash = p.getItem2();
                if (Bytes.equals(actualPath, path)) {
                    // This is not an unrelated leaf; non-membership proof failed.
                    return new Pair<>(false, null);
                }
                if (!(ByteUtils.countCommonPrefix(actualPath.getValue(), path.getValue()) >= proof.sideNodes.length)) {
                    return new Pair<>(false, null);
                }
                p = th.digestLeaf(actualPath, valueHash);
                currentHash = p.getItem1();
                currentData = p.getItem2();
                Pair<Bytes, Bytes> update = new Pair<>(currentHash, currentData);
                updates.add(update);
            }
        } else { // Membership proof.
            Bytes valueHash = th.valueHash(value);
            Pair<Bytes, Bytes> p = th.digestLeaf(path, valueHash);
            currentHash = p.getItem1();
            currentData = p.getItem2();
            Pair<Bytes, Bytes> update = new Pair<>(currentHash, currentData);
            updates.add(update);
        }

        // Recompute root.
        for (int i = 0; i < proof.sideNodes.length; i++) {
            //byte[] node = new byte[th.pathSize()];
            Bytes node = new Bytes(Arrays.copyOf(proof.sideNodes[i].getValue(), th.pathSize()));
            Pair<Bytes, Bytes> p;
            if (ByteUtils.getBitAtFromMSB(path.getValue(), proof.sideNodes.length - 1 - i) == RIGHT) {
                p = th.digestNode(node, currentHash);
            } else {
                p = th.digestNode(currentHash, node);
            }
            currentHash = p.getItem1();
            currentData = p.getItem2();
            Pair<Bytes, Bytes> update = new Pair<>(currentHash, currentData);
            updates.add(update);
        }

        return new Pair<>(Bytes.equals(currentHash, root), updates);
    }


    public static boolean verifyProof(byte[][] sideNodes, Pair<byte[], byte[]> leafData, byte[] root, byte[] key, byte[] value, AbstractTreeHasher th) {
        return verifyProof(Bytes.toBytesArray(sideNodes),
                new Pair<>(new Bytes(leafData.getItem1()), new Bytes(leafData.getItem2())), new Bytes(root),
                new Bytes(key),
                value == null ? null : new Bytes(value),
                th
        );
    }

    public static boolean verifyProof(Bytes[] sideNodes, Pair<Bytes, Bytes> leafData, Bytes root, Bytes key, Bytes value, AbstractTreeHasher th) {
        Bytes path = th.path(key);
        if (value == null || Bytes.equals(value, DEFAULT_VALUE)) { // Non-membership proof.
            if (Bytes.equals(leafData.getItem1(), path)) {
                return false;
            }
            if (!(ByteUtils.countCommonPrefix(leafData.getItem1().getValue(), path.getValue()) >= sideNodes.length)) {
                return false;
            }
        } else {
            if (!Bytes.equals(leafData.getItem1(), path)) {
                return false;
            }
            Bytes valueHash = th.valueHash(value);
            if (!Bytes.equals(leafData.getItem2(), valueHash)) {
                return false;
            }
        }
        Bytes computedRoot = computeRootByPathAndValueHash(sideNodes, leafData.getItem1(), leafData.getItem2(), th);
        return Bytes.equals(root, computedRoot);
    }

    public static Bytes computeRootByPathAndValueHash(Bytes[] sideNodes, Bytes path, Bytes valueHash, AbstractTreeHasher th) {
        Pair<Bytes, Bytes> p = th.digestLeaf(path, valueHash);
        Bytes currentHash = p.getItem1();
        //Bytes currentData = p.getItem2();
        for (int i = 0; i < sideNodes.length; i++) {
            //byte[] node = new byte[th.pathSize()];
            Bytes node = new Bytes(Arrays.copyOf(sideNodes[i].getValue(), th.pathSize()));
            if (ByteUtils.getBitAtFromMSB(path.getValue(), sideNodes.length - 1 - i) == RIGHT) {
                p = th.digestNode(node, currentHash);
            } else {
                p = th.digestNode(currentHash, node);
            }
            currentHash = p.getItem1();
            //currentData = p.getItem2();
        }
        return currentHash;
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

    /**
     * Do a basic sanity check on the proof, so that a malicious proof cannot
     * cause the verifier to fatally exit (e.g. due to an index out-of-range
     * error) or cause a CPU DoS attack.
     *
     * @param th TreeHasher
     * @return
     */
    public boolean sanityCheck(AbstractTreeHasher th) {
        // Check that the number of supplied sidenodes does not exceed the maximum possible.
        if (this.sideNodes.length > th.pathSize() * 8 ||
                // Check that leaf data for non-membership proofs is the correct size.
                (this.nonMembershipLeafData != null && this.nonMembershipLeafData.getValue().length != th.leafDataSize())) {
            return false;
        }

        // Check that all supplied side nodes are the correct size.
        for (Bytes v : this.sideNodes) {
            if (v.getValue().length != th.hasherSize()) {
                return false;
            }
        }

        // Check that the sibling data hashes to the first side node if not nil
        if (this.siblingData == null || this.sideNodes.length == 0) {
            return true;
        }
        Bytes siblingHash = th.digest(this.siblingData);
        return Bytes.equals(this.sideNodes[0], siblingHash);
    }

    @Override
    public String toString() {
        return "SparseMerkleProof{" +
                "sideNodes=" + Arrays.toString(sideNodes) +
                ", nonMembershipLeafData=" + nonMembershipLeafData +
                ", siblingData=" + siblingData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SparseMerkleProof proof = (SparseMerkleProof) o;
        return Arrays.equals(sideNodes, proof.sideNodes) && Objects.equals(nonMembershipLeafData, proof.nonMembershipLeafData) && Objects.equals(siblingData, proof.siblingData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nonMembershipLeafData, siblingData);
        result = 31 * result + Arrays.hashCode(sideNodes);
        return result;
    }
}
