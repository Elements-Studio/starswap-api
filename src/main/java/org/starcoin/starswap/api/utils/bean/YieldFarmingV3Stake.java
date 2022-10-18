package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YieldFarmingV3Stake {
    /*
        /// YieldFarmingV3
        /// To store user's asset token
        struct Stake<phantom PoolType, AssetT> has store, copy, drop {
            id: u64,
            asset: AssetT,
            asset_weight: u128,
            // reform: before is actually equivalent to asset amount, after is equivalent to asset weight
            last_harvest_index: u128,
            gain: u128,
            asset_multiplier: u64,
            //abandoned fields
        }
     */
    @JsonProperty("id")//: u64,
    private Long id;
    @JsonProperty("asset")
    private Object asset;//: AssetT,
    @JsonProperty("asset_weight")
    private String assetWeight;//: u128,
    // reform: before is actually equivalent to asset amount, after is equivalent to asset weight
    @JsonProperty("last_harvest_index")
    private String lastHarvestIndex;//: u128,
    @JsonProperty("gain")
    private String gain;//: u128,
    @JsonProperty("asset_multiplier")
    private Long assetMultiplier;//: u64,

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getAsset() {
        return asset;
    }

    public void setAsset(Object asset) {
        this.asset = asset;
    }

    public String getAssetWeight() {
        return assetWeight;
    }

    public void setAssetWeight(String assetWeight) {
        this.assetWeight = assetWeight;
    }

    public String getLastHarvestIndex() {
        return lastHarvestIndex;
    }

    public void setLastHarvestIndex(String lastHarvestIndex) {
        this.lastHarvestIndex = lastHarvestIndex;
    }

    public String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        this.gain = gain;
    }

    public Long getAssetMultiplier() {
        return assetMultiplier;
    }

    public void setAssetMultiplier(Long assetMultiplier) {
        this.assetMultiplier = assetMultiplier;
    }

    @Override
    public String toString() {
        return "Stake{" +
                "id=" + id +
                ", asset=" + asset +
                ", assetWeight='" + assetWeight + '\'' +
                ", lastHarvestIndex='" + lastHarvestIndex + '\'' +
                ", gain='" + gain + '\'' +
                ", assetMultiplier=" + assetMultiplier +
                '}';
    }
}
