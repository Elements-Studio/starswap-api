package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class YieldFarmingV3StakeExtend {
    /*

        //YieldFarmingV3:
        struct StakeExtend<phantom PoolType, phantom AssetT> has key, store {
            id: u64,
            //option, Option<u64>
            asset_amount: u128,
            //weight factor, if farm: weight_factor = user boost factor * 100; if stake: weight_factor = stepwise multiplier
            weight_factor: u64,
        }
     */
    @JsonProperty("id")
    private Long id;
    @JsonProperty("asset_amount")
    private BigInteger assetAmount;
    @JsonProperty("weight_factor")
    private Long weightFactor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getAssetAmount() {
        return assetAmount;
    }

    public void setAssetAmount(BigInteger assetAmount) {
        this.assetAmount = assetAmount;
    }

    public Long getWeightFactor() {
        return weightFactor;
    }

    public void setWeightFactor(Long weightFactor) {
        this.weightFactor = weightFactor;
    }

    @Override
    public String toString() {
        return "StakeExtend{" +
                "id=" + id +
                ", assetAmount=" + assetAmount +
                ", weightFactor=" + weightFactor +
                '}';
    }
}
