package org.starcoin.smt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class SparseMerkleTree {
    public static final int RIGHT = 1;
    public static final Bytes DEFAULT_VALUE = new Bytes(new byte[]{});
    private final AbstractTreeHasher treeHasher;
    private final Map<Bytes, Bytes> nodes;
    private final SmtValueStore values;
    private Bytes root;

    public SparseMerkleTree(Map<Bytes, Bytes> nodes, Map<Bytes, Bytes> values, Hasher hasher, Option... options) {
        this(nodes, values, new TreeHasher(hasher), options);
    }

    public SparseMerkleTree(Map<Bytes, Bytes> nodes, Map<Bytes, Bytes> values, AbstractTreeHasher treeHasher, Option[] options) {
        this.treeHasher = treeHasher;
        this.nodes = nodes;
        this.values = SmtValueStore.asSmtValueStore(values);
        for (Option option : options) {
            option.accept(this);
        }
        this.root = this.treeHasher.placeholder();
    }

    public AbstractTreeHasher getTreeHasher() {
        return treeHasher;
    }

    public Map<Bytes, Bytes> getNodes() {
        return nodes;
    }

    public SmtValueStore getValues() {
        return values;
    }

    public Bytes getRoot() {
        return root;
    }

    public int depth() {
        return this.treeHasher.pathSize() * 8;
    }

    public Bytes update(byte[] key, byte[] value) {
        return update(new Bytes(key), new Bytes(value));
    }

    public Bytes update(Bytes key, Bytes value) {
        Bytes newRoot = this.updateForRoot(key, value, this.getRoot());
        this.root = newRoot;
        return newRoot;
    }

    public Bytes updateForRoot(Bytes key, Bytes value, Bytes root) {
        Bytes path = this.treeHasher.path(key);
        SideNodesResult sideNodesResult = this.sideNodesForRoot(path, root, false);
        Bytes[] sideNodes = sideNodesResult.getSideNodes();
        Bytes[] pathNodes = sideNodesResult.getPathNodes();
        Bytes oldLeafData = sideNodesResult.getLeafData();

        Bytes newRoot;
        if (Bytes.equals(value, DEFAULT_VALUE)) {
            // Delete operation.
            try {
                newRoot = this.deleteWithSideNodes(path, sideNodes, pathNodes, oldLeafData);
            } catch (KeyAlreadyEmptyException err) {
                // This key is already empty; return the old root.
                return root;
            }
            if (!this.values.isImmutable()) {
                this.values.remove(path);
            }
        } else {
            // Insert or update operation.
            newRoot = this.updateWithSideNodes(path, value, sideNodes, pathNodes, oldLeafData);
        }
        return newRoot;
    }

    public SparseMerkleProof prove(Bytes key) {
        return proveForRoot(key, this.root);
    }

    public SparseMerkleProof prove(byte[] key) {
        return proveForRoot(new Bytes(key), this.root);
    }

    public SparseMerkleProof proveUpdatable(Bytes key) {
        return proveUpdatableForRoot(key, this.root);
    }

    public SparseMerkleProof proveForRoot(Bytes key, Bytes root) {
        Pair<SparseMerkleProof, Bytes> p = doProveForRoot(key, root, false);
        return p.getItem1();
    }

    public SparseMerkleProof proveMembershipForRoot(Bytes key, Bytes root) throws MembershipProofException {
        Pair<SparseMerkleProof, Bytes> p = proveMembershipForRootAndGetLeafData(key, root);
        return p.getItem1();
    }

    public Pair<SparseMerkleProof, Bytes> proveMembershipForRootAndGetLeafData(Bytes key, Bytes root) throws MembershipProofException {
        Pair<SparseMerkleProof, Bytes> p = doProveForRoot(key, root, false);
        if (this.treeHasher.isKeyUnrelatedWithLeafData(key, p.getItem2())) {
            throw new MembershipProofException(String.format("Key(%1$s) is unrelated with leaf data(%2$s)",
                    HexUtils.byteArrayToHex(key.getValue()),
                    p.getItem2() == null ? null : HexUtils.byteArrayToHex(p.getItem2().getValue())));
        }
        return p;
    }

    public SparseMerkleProof proveNonMembershipForRoot(Bytes key, Bytes root) throws NonMembershipProofException {
        Pair<SparseMerkleProof, Bytes> p = doProveForRoot(key, root, false);
        if (this.treeHasher.isKeyRelatedWithLeafData(key, p.getItem2())) {
            throw new NonMembershipProofException(String.format("Key(%1$s) is related with leaf data(%2$s)",
                    HexUtils.byteArrayToHex(key.getValue()),
                    p.getItem2() == null ? null : HexUtils.byteArrayToHex(p.getItem2().getValue())));
        }
        return p.getItem1();
    }

    public SparseMerkleProof proveUpdatableForRoot(Bytes key, Bytes root) {
        Pair<SparseMerkleProof, Bytes> p = doProveForRoot(key, root, true);
        return p.getItem1();
    }

    public Pair<SparseMerkleProof, Bytes> proveAndGetLeafData(Bytes key) {
        return proveForRootAndGetLeafData(key, this.root);
    }

    public Pair<SparseMerkleProof, Bytes> proveAndGetLeafData(byte[] key) {
        return proveAndGetLeafData(new Bytes(key));
    }

    public Pair<SparseMerkleProof, Bytes> proveForRootAndGetLeafData(Bytes key, Bytes root) {
        return doProveForRoot(key, root, false);
    }

    public Pair<SparseMerkleProof, Bytes> proveForRootAndGetLeafData(byte[] key, Bytes root) {
        return proveForRootAndGetLeafData(new Bytes(key), root);
    }

    private Pair<SparseMerkleProof, Bytes> doProveForRoot(Bytes key, Bytes root, boolean isUpdatable) {
        Bytes path = this.treeHasher.path(key);
        SideNodesResult sideNodesResult = this.sideNodesForRoot(path, root, isUpdatable);
        Bytes[] sideNodes = sideNodesResult.getSideNodes();
        Bytes[] pathNodes = sideNodesResult.getPathNodes();
        Bytes leafData = sideNodesResult.getLeafData();
        Bytes siblingData = sideNodesResult.getSiblingData();
        List<Bytes> nonEmptySideNodes = new ArrayList<>();
        for (Bytes v : sideNodes) {
            if (v != null) {
                nonEmptySideNodes.add(v);
            }
        }

        // Deal with non-membership proofs. If the leaf hash is the placeholder
        // value, we do not need to add anything else to the proof.
        Bytes nonMembershipLeafData = null;
        if (!Bytes.equals(pathNodes[0], this.treeHasher.placeholder())) {
            Pair<Bytes, Bytes> p = this.treeHasher.parseLeaf(leafData);
            Bytes actualPath = p.getItem1();
            if (!Bytes.equals(actualPath, path)) {
                // This is a non-membership proof that involves showing a different leaf.
                // Add the leaf data to the proof.
                nonMembershipLeafData = leafData;
            }
        }
        SparseMerkleProof proof = new SparseMerkleProof(nonEmptySideNodes.toArray(new Bytes[0]), nonMembershipLeafData, siblingData);
        return new Pair<>(proof, leafData);
    }

    /**
     * Get all the side nodes(sibling nodes) for a given path from a given root.
     * Returns an array of sibling nodes, the leaf hash found at that path, the
     * leaf data, and the sibling data.
     * If the leaf is a placeholder, the leaf data is nil.
     */
    private SideNodesResult sideNodesForRoot(Bytes path, Bytes root, boolean getSiblingData) {
        // Side nodes for the path. Nodes are inserted in reverse order, then the
        // slice is reversed at the end.
        List<Bytes> sideNodes = new ArrayList<>(this.depth());
        List<Bytes> pathNodes = new ArrayList<>(this.depth() + 1);
        pathNodes.add(root);

        if (Bytes.equals(root, this.treeHasher.placeholder())) {
            // If the root is a placeholder, there are no side nodes to return.
            // Let the "actual path" be the input path.
            return new SideNodesResult(sideNodes, pathNodes, null, null);
        }

        Bytes currentData = this.nodes.get(root);
        if (currentData == null) {
            throw new NoSuchElementException(String.format("Root: %1$s", HexUtils.byteArrayToHex(root.getValue())));
        } else if (this.treeHasher.isLeaf(currentData)) {
            // If the root is a leaf, there are also no sidenodes to return.
            return new SideNodesResult(sideNodes, pathNodes, currentData, null);
        }

        Bytes nodeHash;
        Bytes sideNode = null;
        Bytes siblingData = null;
        for (int i = 0; i < this.depth(); i++) {
            Pair<Bytes, Bytes> p = this.treeHasher.parseNode(currentData);
            Bytes leftNode = p.getItem1();
            Bytes rightNode = p.getItem2();
            // Get side node depending on whether the path bit is on or off.
            if (ByteUtils.getBitAtFromMSB(path.getValue(), i) == RIGHT) {
                sideNode = leftNode;
                nodeHash = rightNode;
            } else {
                sideNode = rightNode;
                nodeHash = leftNode;
            }
            sideNodes.add(sideNode);
            pathNodes.add(nodeHash);

            if (Bytes.equals(nodeHash, this.treeHasher.placeholder())) {
                // If the node is a placeholder, we've reached the end.
                currentData = null;
                break;
            }
            currentData = this.nodes.get(nodeHash);
            if (currentData == null) {
                throw new NoSuchElementException(String.format("Node hash: %1$s", HexUtils.byteArrayToHex(nodeHash.getValue())));
            } else if (this.treeHasher.isLeaf(currentData)) {
                // If the node is a leaf, we've reached the end.
                break;
            }
        }

        if (getSiblingData) {
            if (sideNode == null) {
                throw new NullPointerException("Side node is null");
            }
            siblingData = this.nodes.get(sideNode);
            if (siblingData == null) {
                throw new NoSuchElementException(String.format("Side node: %1$s", HexUtils.byteArrayToHex(sideNode.getValue())));
            }
        }
        return new SideNodesResult(Bytes.reverseBytesArray(sideNodes.toArray(new Bytes[0])),
                Bytes.reverseBytesArray(pathNodes.toArray(new Bytes[0])), currentData, siblingData);
    }

    private Bytes updateWithSideNodes(Bytes path, Bytes value, Bytes[] sideNodes, Bytes[] pathNodes, Bytes oldLeafData) {
        Bytes valueHash = this.treeHasher.valueHash(value);
        Bytes currentHash, currentData;
        Pair<Bytes, Bytes> leafPair = this.treeHasher.digestLeaf(path, valueHash);
        currentHash = leafPair.getItem1();
        currentData = leafPair.getItem2();
        this.nodes.put(currentHash, currentData);
        currentData = currentHash;

        // If the leaf node that sibling nodes lead to has a different actual path
        // than the leaf node being updated, we need to create an intermediate node
        // with this leaf node and the new leaf node as children.
        //
        // First, get the number of bits that the paths of the two leaf nodes share
        // in common as a prefix.
        int commonPrefixCount;
        Bytes oldValueHash = null;
        if (Bytes.equals(pathNodes[0], this.treeHasher.placeholder())) {
            commonPrefixCount = this.depth();
        } else {
            Bytes actualPath;
            Pair<Bytes, Bytes> p = this.treeHasher.parseLeaf(oldLeafData);
            actualPath = p.getItem1();
            oldValueHash = p.getItem2();
            commonPrefixCount = ByteUtils.countCommonPrefix(path.getValue(), actualPath.getValue());
        }
        if (commonPrefixCount != this.depth()) {
            Pair<Bytes, Bytes> p;
            if (ByteUtils.getBitAtFromMSB(path.getValue(), commonPrefixCount) == RIGHT) {
                p = this.treeHasher.digestNode(pathNodes[0], currentData);
            } else {
                p = this.treeHasher.digestNode(currentData, pathNodes[0]);
            }
            currentHash = p.getItem1();
            currentData = p.getItem2();
            this.nodes.put(currentHash, currentData);
            currentData = currentHash;
        } else if (oldValueHash != null) {
            // Short-circuit if the same value is being set
            if (Bytes.equals(oldValueHash, valueHash)) {
                return this.root;
            }
            // If an old leaf exists, remove it
            this.nodes.remove(pathNodes[0]);
            if (!this.values.isImmutable()) {
                this.values.remove(path);
            }
        }
        // All remaining path nodes are orphaned
        for (int i = 1; i < pathNodes.length; i++) {
            this.nodes.remove(pathNodes[i]);
        }

        // The offset from the bottom of the tree to the start of the side nodes.
        // Note: i-offsetOfSideNodes is the index into sideNodes[]
        int offsetOfSideNodes = this.depth() - sideNodes.length;
        for (int i = 0; i < this.depth(); i++) {
            Bytes sideNode;
            if (i - offsetOfSideNodes < 0 || sideNodes[i - offsetOfSideNodes] == null) {
                if (commonPrefixCount != this.depth() && commonPrefixCount > this.depth() - 1 - i) {
                    // If there are no side nodes at this height, but the number of
                    // bits that the paths of the two leaf nodes share in common is
                    // greater than this depth, then we need to build up the tree
                    // to this depth with placeholder values at siblings.
                    sideNode = this.treeHasher.placeholder();
                } else {
                    continue;
                }
            } else {
                sideNode = sideNodes[i - offsetOfSideNodes];
            }
            Pair<Bytes, Bytes> p;
            if (ByteUtils.getBitAtFromMSB(path.getValue(), this.depth() - 1 - i) == RIGHT) {
                p = this.treeHasher.digestNode(sideNode, currentData);
            } else {
                p = this.treeHasher.digestNode(currentData, sideNode);
            }
            currentHash = p.getItem1();
            currentData = p.getItem2();
            this.nodes.put(currentHash, currentData);
            currentData = currentHash;
        }
        if (this.values.isImmutable()) {
            this.values.putForValueHash(path, valueHash, value);
        } else {
            this.values.put(path, value);
        }

        return currentHash;
    }

    private Bytes deleteWithSideNodes(Bytes path, Bytes[] sideNodes, Bytes[] pathNodes, Bytes oldLeafData) {
        if (Bytes.equals(pathNodes[0], this.treeHasher.placeholder())) {
            // This key is already empty as it is a placeholder; return an error.
            throw new KeyAlreadyEmptyException("Key is already empty as pathNodes[0] is a placeholder");
        }
        Pair<Bytes, Bytes> leafPair = this.treeHasher.parseLeaf(oldLeafData);
        Bytes actualPath = leafPair.getItem1();
        if (!Bytes.equals(path, actualPath)) {
            // This key is already empty as a different key was found its place; return an error.
            throw new KeyAlreadyEmptyException(String.format("Key is already empty as a different key was found on actual path: %1$s", HexUtils.byteArrayToHex(actualPath.getValue())));
        }
        // All nodes above the deleted leaf are now orphaned
        for (Bytes node : pathNodes) {
            this.nodes.remove(node);
        }

        Bytes currentHash = null;
        Bytes currentData = null;
        boolean nonPlaceholderReached = false;
        for (int i = 0; i < sideNodes.length; i++) {
            Bytes sideNode = sideNodes[i];
            if (currentData == null) {
                Bytes sideNodeValue = this.nodes.get(sideNode);
                if (this.treeHasher.isLeaf(sideNodeValue)) {
                    // This is the leaf sibling that needs to be bubbled up the tree.
                    currentHash = sideNode;
                    currentData = sideNode;
                    continue;
                } else {
                    // This is the node sibling that needs to be left in its place.
                    currentData = this.treeHasher.placeholder();
                    nonPlaceholderReached = true;
                }
            }

            if (!nonPlaceholderReached && Bytes.equals(sideNode, this.treeHasher.placeholder())) {
                // We found another placeholder sibling node, keep going up the
                // tree until we find the first sibling that is not a placeholder.
                continue;
            } else if (!nonPlaceholderReached) {
                // We found the first sibling node that is not a placeholder, it is
                // time to insert our leaf sibling node here.
                nonPlaceholderReached = true;
            }
            Pair<Bytes, Bytes> p;
            if (ByteUtils.getBitAtFromMSB(path.getValue(), sideNodes.length - 1 - i) == RIGHT) {
                p = this.treeHasher.digestNode(sideNode, currentData);
            } else {
                p = this.treeHasher.digestNode(currentData, sideNode);
            }
            currentHash = p.getItem1();
            currentData = p.getItem2();
            this.nodes.put(currentHash, currentData);
            currentData = currentHash;
        }
        if (currentHash == null) {
            // The tree is empty; return placeholder value as root.
            currentHash = this.treeHasher.placeholder();
        }
        return currentHash;
    }

    /**
     * Delete deletes a value from tree. It returns the new root of the tree.
     *
     * @param key
     * @return
     */
    public Bytes delete(Bytes key) {
        return this.update(key, DEFAULT_VALUE);
    }


    /**
     * Get gets the value of a key from the tree.
     *
     * @param key
     * @return
     */
    public Bytes get(Bytes key) {
        // Get tree's root
        Bytes root = this.getRoot();
        if (Bytes.equals(root, this.treeHasher.placeholder())) {
            // The tree is empty, return the default value.
            return DEFAULT_VALUE;
        }
        Bytes path = this.treeHasher.path(key);
        Bytes value = null;
        if (this.values.isImmutable()) {
            throw new UnsupportedOperationException("Not implemented, this SMT values can only getForValueHash");
        } else {
            value = values.get(path);
        }
        return value != null ? value : DEFAULT_VALUE;
    }

    /**
     * Has returns true if the value at the given key is non-default, false
     * otherwise.
     *
     * @param key
     * @return
     */
    public boolean has(Bytes key) {
        Bytes val = get(key);
        return val != null && !Bytes.equals(DEFAULT_VALUE, val);
    }

    /**
     * Option is a function that configures SMT.
     */
    public interface Option extends Consumer<SparseMerkleTree> {
    }

    public static class KeyAlreadyEmptyException extends RuntimeException {
        public KeyAlreadyEmptyException(String message) {
            super(message);
        }
    }

    public static class MembershipProofException extends Exception {
        public MembershipProofException(String message) {
            super(message);
        }
    }

    public static class NonMembershipProofException extends MembershipProofException {
        public NonMembershipProofException(String message) {
            super(message);
        }
    }
}
