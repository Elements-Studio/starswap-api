package org.starcoin.starswap.api.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SyrupMultiplierPoolInfo {
    private Long pledgeTimeSeconds;
    private Long multiplier; //stepwise_multiplier
    private BigInteger assetAmount;

    //stepwise_asset_weight = stepwise_asset_amount * stepwise_multiplier
    private BigInteger assetWeight;

    //asset_total_weight = Σ(stepwise_asset_weight)
    //stepwise_pool_ratio = (stepwise_asset_weight) / asset_total_weight
    private BigDecimal poolRatio;

    // estimated_apr = pool_alloc_point / total_alloc_point *
    // (pool_release_per_second * 86400 * 365) * stepwise_pool_ratio  / stepwise_total_amount * 100
    private BigDecimal estimatedApr;

    public Long getPledgeTimeSeconds() {
        return pledgeTimeSeconds;
    }

    public void setPledgeTimeSeconds(Long pledgeTimeSeconds) {
        this.pledgeTimeSeconds = pledgeTimeSeconds;
    }

    public Long getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Long multiplier) {
        this.multiplier = multiplier;
    }

    public BigInteger getAssetAmount() {
        return assetAmount;
    }

    public void setAssetAmount(BigInteger assetAmount) {
        this.assetAmount = assetAmount;
    }

    public BigInteger getAssetWeight() {
        return assetWeight;
    }

    public void setAssetWeight(BigInteger assetWeight) {
        this.assetWeight = assetWeight;
    }

    public BigDecimal getPoolRatio() {
        return poolRatio;
    }

    public void setPoolRatio(BigDecimal poolRatio) {
        this.poolRatio = poolRatio;
    }

    public BigDecimal getEstimatedApr() {
        return estimatedApr;
    }

    public void setEstimatedApr(BigDecimal estimatedApr) {
        this.estimatedApr = estimatedApr;
    }

    @Override
    public String toString() {
        return "SyrupMultiplierPoolInfo{" +
                "pledgeTimeSeconds=" + pledgeTimeSeconds +
                ", multiplier=" + multiplier +
                ", assetAmount=" + assetAmount +
                ", assetWeight=" + assetWeight +
                ", poolRatio=" + poolRatio +
                ", estimatedApr=" + estimatedApr +
                '}';
    }
}
