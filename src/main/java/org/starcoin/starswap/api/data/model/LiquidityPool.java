package org.starcoin.starswap.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 流动性池子。
 */
@Entity
@Table(indexes = {@Index(name = "idx_token_x_y_id", columnList = "token_x_id, token_y_id")})
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class LiquidityPool {

    /**
     * 池子 Id（领域键）。
     */
    @EmbeddedId
    @AttributeOverride(name = "tokenXId", column = @Column(name = "token_x_id", nullable = false))
    @AttributeOverride(name = "tokenYId", column = @Column(name = "token_y_id", nullable = false))
    @AttributeOverride(name = "liquidityTokenAddress", column = @Column(name = "liquidity_token_address", length = 34, nullable = false))
    @AttributeOverride(name = "poolAddress", column = @Column(name = "pool_address", length = 34, nullable = false))
    private LiquidityPoolId liquidityPoolId;

    @Column(length = 1000, nullable = true)
    private String description;

//    @Column(length = 1000, nullable = false)
//    private String descriptionEn;

    @Column(nullable = false)
    private Integer sequenceNumber;

    @Column(precision = 31, scale = 0)
    private BigInteger totalLiquidity;

    @Column(name = "token_x_reserve", precision = 31, scale = 0)
    private BigInteger tokenXReserve;

    @Column(name = "token_y_reserve", precision = 31, scale = 0)
    private BigInteger tokenYReserve;

    @Column(name = "token_x_reserve_in_usd", precision = 51, scale = 10)
    private BigDecimal tokenXReserveInUsd;

    @Column(name = "token_y_reserve_in_usd", precision = 51, scale = 10)
    private BigDecimal tokenYReserveInUsd;

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

    public LiquidityPoolId getLiquidityPoolId() {
        return liquidityPoolId;
    }

    public void setLiquidityPoolId(LiquidityPoolId liquidityPoolId) {
        this.liquidityPoolId = liquidityPoolId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getDescriptionEn() {
//        return descriptionEn;
//    }
//
//    public void setDescriptionEn(String descriptionEn) {
//        this.descriptionEn = descriptionEn;
//    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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

    public BigInteger getTotalLiquidity() {
        return totalLiquidity;
    }

    public void setTotalLiquidity(BigInteger totalLiquidity) {
        this.totalLiquidity = totalLiquidity;
    }

    public BigInteger getTokenXReserve() {
        return tokenXReserve;
    }

    public void setTokenXReserve(BigInteger tokenXReserve) {
        this.tokenXReserve = tokenXReserve;
    }

    public BigInteger getTokenYReserve() {
        return tokenYReserve;
    }

    public void setTokenYReserve(BigInteger tokenYReserve) {
        this.tokenYReserve = tokenYReserve;
    }

    public BigDecimal getTokenXReserveInUsd() {
        return tokenXReserveInUsd;
    }

    public void setTokenXReserveInUsd(BigDecimal tokenXReserveInUsd) {
        this.tokenXReserveInUsd = tokenXReserveInUsd;
    }

    public BigDecimal getTokenYReserveInUsd() {
        return tokenYReserveInUsd;
    }

    public void setTokenYReserveInUsd(BigDecimal tokenYReserveInUsd) {
        this.tokenYReserveInUsd = tokenYReserveInUsd;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
