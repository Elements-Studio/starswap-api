package org.starcoin.starswap.api.data.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.math.BigInteger;

@Entity
@DynamicInsert
@DynamicUpdate
public class PullingEventTask {
    public static final int PULLING_BLOCK_MAX_COUNT = 32;

    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_DONE = "DONE";
    public static final String STATUS_PROCESSING = "PROCESSING";

    @Id
    @Column(precision = 21, scale = 0)
    private BigInteger fromBlockNumber;

    @Column(precision = 21, scale = 0, nullable = false)
    private BigInteger toBlockNumber;

    @Column(length = 20, nullable = false)
    private String status = STATUS_CREATED;

    @Column(length = 70, nullable = false)
    private String createdBy;

    @Column(length = 70, nullable = false)
    private String updatedBy;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;

    @Version
    private Long version;

    public BigInteger getFromBlockNumber() {
        return fromBlockNumber;
    }

    public void setFromBlockNumber(BigInteger fromBlockNumber) {
        this.fromBlockNumber = fromBlockNumber;
    }

    public BigInteger getToBlockNumber() {
        return toBlockNumber;
    }

    public void setToBlockNumber(BigInteger toBlockNumber) {
        this.toBlockNumber = toBlockNumber;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    public void done() {
        if (!STATUS_CREATED.equals(this.status) && !STATUS_PROCESSING.equals(this.status)) {
            throw new RuntimeException("Source status error: " + this.status);
        }
        this.status = STATUS_DONE;
    }

    public void processing() {
        if (!STATUS_CREATED.equals(this.status)) {
            throw new RuntimeException("Source status error: " + this.status);
        }
        this.status = STATUS_PROCESSING;
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


    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void resetStatus() {
        this.status = STATUS_CREATED;
    }
}
