package org.starcoin.starswap.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(indexes = {@Index(name = "idx_token_id", columnList = "token_id")})
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class SyrupPool {
    /**
     * Farm Id（领域键）。
     */
    @EmbeddedId
    @AttributeOverride(name = "tokenId", column = @Column(name = "token_id", nullable = false))
    @AttributeOverride(name = "poolAddress", column = @Column(name = "pool_address", length = 34, nullable = false))
    private SyrupPoolId syrupPoolId;

    @Column(length = 1000, nullable = true)
    private String description;

    @Column(nullable = false)
    private Integer sequenceNumber;

    @Column(precision = 31, scale = 0)
    private BigInteger totalStakeAmount;

    /**
     * Estimated annual percentage yield.
     */
    @Column(precision = 31, scale = 10)
    private BigDecimal estimatedApy;

    @Column(length = 15, nullable = false)
    private String rewardTokenId;

    //    @Column
    //    private Integer rewardMultiplier;

    @Column(precision = 31, scale = 0)
    private BigInteger dailyReward;

    @Column(precision = 51, scale = 10)
    private BigDecimal tvlInUsd;

    /**
     * 是否已禁用。
     */
    @Column(nullable = false)
    private Boolean deactived;

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

    public SyrupPoolId getSyrupPoolId() {
        return syrupPoolId;
    }

    public void setSyrupPoolId(SyrupPoolId syrupPoolId) {
        this.syrupPoolId = syrupPoolId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public BigInteger getTotalStakeAmount() {
        return totalStakeAmount;
    }

    public void setTotalStakeAmount(BigInteger totalStakeAmount) {
        this.totalStakeAmount = totalStakeAmount;
    }

    public BigDecimal getEstimatedApy() {
        return estimatedApy;
    }

    public void setEstimatedApy(BigDecimal estimatedApy) {
        this.estimatedApy = estimatedApy;
    }

    public String getRewardTokenId() {
        return rewardTokenId;
    }

    public void setRewardTokenId(String rewardTokenId) {
        this.rewardTokenId = rewardTokenId;
    }

//    public Integer getRewardMultiplier() {
//        return rewardMultiplier;
//    }
//
//    public void setRewardMultiplier(Integer rewardMultiplier) {
//        this.rewardMultiplier = rewardMultiplier;
//    }

    public BigDecimal getTvlInUsd() {
        return tvlInUsd;
    }

    public void setTvlInUsd(BigDecimal tvlInUsd) {
        this.tvlInUsd = tvlInUsd;
    }

    public Boolean getDeactived() {
        return deactived;
    }

    public void setDeactived(Boolean deactived) {
        this.deactived = deactived;
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

    public BigInteger getDailyReward() {
        return dailyReward;
    }

    public void setDailyReward(BigInteger dailyReward) {
        this.dailyReward = dailyReward;
    }

    @Override
    public String toString() {
        return "SyrupPool{" +
                "syrupPoolId=" + syrupPoolId +
                ", description='" + description + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", totalStakeAmount=" + totalStakeAmount +
                ", estimatedApy=" + estimatedApy +
                ", rewardTokenId='" + rewardTokenId + '\'' +
                ", dailyReward=" + dailyReward +
                ", tvlInUsd=" + tvlInUsd +
                ", deactived=" + deactived +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}
