package org.starcoin.smt;

import java.util.List;

public class SideNodesResult {

    /**
     * array of side nodes.
     */
    private Bytes[] sideNodes;

    /**
     * the leaf hash found at this path.
     */
    private Bytes[] pathNodes;

    /**
     * leaf data.
     */
    private Bytes leafData;

    /**
     * sibling data.
     */
    private Bytes siblingData;

    public SideNodesResult() {
    }

    public SideNodesResult(List<Bytes> sideNodes, List<Bytes> pathNodes, Bytes leafData, Bytes siblingData) {
        this(sideNodes.toArray(new Bytes[0]), pathNodes.toArray(new Bytes[0]), leafData, siblingData);
    }

    public SideNodesResult(Bytes[] sideNodes, Bytes[] pathNodes, Bytes leafData, Bytes siblingData) {
        this.sideNodes = sideNodes;
        this.pathNodes = pathNodes;
        this.leafData = leafData;
        this.siblingData = siblingData;
    }

    public Bytes[] getSideNodes() {
        return sideNodes;
    }

    public void setSideNodes(Bytes[] sideNodes) {
        this.sideNodes = sideNodes;
    }

    public Bytes[] getPathNodes() {
        return pathNodes;
    }

    public void setPathNodes(Bytes[] pathNodes) {
        this.pathNodes = pathNodes;
    }

    public Bytes getLeafData() {
        return leafData;
    }

    public void setLeafData(Bytes leafData) {
        this.leafData = leafData;
    }

    public Bytes getSiblingData() {
        return siblingData;
    }

    public void setSiblingData(Bytes siblingData) {
        this.siblingData = siblingData;
    }
}

