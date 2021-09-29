package org.starcoin.starswap.api.data.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class NodeHeartbeatId implements Serializable {
    private String nodeId;
    private BigInteger startedAt;

    public NodeHeartbeatId() {
    }

    public NodeHeartbeatId(String nodeId, BigInteger startedAt) {
        this.nodeId = nodeId;
        this.startedAt = startedAt;
    }

    public String getNodeId() {
        return nodeId;
    }

    public BigInteger getStartedAt() {
        return startedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeHeartbeatId that = (NodeHeartbeatId) o;
        return Objects.equals(nodeId, that.nodeId) && Objects.equals(startedAt, that.startedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, startedAt);
    }

    @Override
    public String toString() {
        return "NodeHeartbeatId{" +
                "nodeId='" + nodeId + '\'' +
                ", startedAt=" + startedAt +
                '}';
    }
}
