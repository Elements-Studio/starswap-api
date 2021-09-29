package org.starcoin.starswap.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * 流动性账号（用户提供的流动性）。
 */
@Entity
@Table(indexes = {@Index(name = "idx_token_x_y_id", columnList = "token_x_id, token_y_id")})
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class LiquidityAccount {

    /**
     * 账号 Id（领域键）。
     */
    @EmbeddedId
    @AttributeOverride(name = "tokenXId", column = @Column(name = "token_x_id", nullable = false))
    @AttributeOverride(name = "tokenYId", column = @Column(name = "token_y_id", nullable = false))
    @AttributeOverride(name = "liquidityTokenAddress", column = @Column(name = "liquidity_token_address", length = 34, nullable = false))
    @AttributeOverride(name = "poolAddress", column = @Column(name = "pool_address", length = 34, nullable = false))
    @AttributeOverride(name = "accountAddress", column = @Column(name = "account_address", length = 34, nullable = false))
    private LiquidityAccountId liquidityAccountId;

    /**
     * 账号提供的流动性数量。
     */
    @Column(precision = 31, scale = 0)
    private BigInteger liquidity;

    /**
     * 是否已不再使用。
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

    public LiquidityAccountId getLiquidityAccountId() {
        return liquidityAccountId;
    }

    public void setLiquidityAccountId(LiquidityAccountId liquidityAccountId) {
        this.liquidityAccountId = liquidityAccountId;
    }

    public BigInteger getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(BigInteger liquidity) {
        this.liquidity = liquidity;
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

    @Override
    public String toString() {
        return "LiquidityAccount{" +
                "liquidityAccountId=" + liquidityAccountId +
                ", liquidity=" + liquidity +
                ", deactived=" + deactived +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
