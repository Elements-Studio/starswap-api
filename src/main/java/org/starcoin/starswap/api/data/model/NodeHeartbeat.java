package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.math.BigInteger;

@Entity
@IdClass(NodeHeartbeatId.class)
public class NodeHeartbeat {

    @Id
    @Column(length = 34)
    private String nodeId;

    @Id
    @Column(precision = 21, scale = 0)
    private BigInteger startedAt;

    @Column(precision = 21, scale = 0)
    private BigInteger beatenAt;


    @Column(length = 70, nullable = false)
    private String createdBy;

    @Column(length = 70, nullable = false)
    private String updatedBy;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;


    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public BigInteger getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(BigInteger startedAt) {
        this.startedAt = startedAt;
    }

    public BigInteger getBeatenAt() {
        return beatenAt;
    }

    public void setBeatenAt(BigInteger beatenAt) {
        this.beatenAt = beatenAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
