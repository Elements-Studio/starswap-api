package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FarmingAsset {
    //struct FarmingAsset<phantom PoolType, phantom AssetT> has key, store {
    @JsonProperty("asset_total_weight")//: u128,
    private String assetTotalWeight;

    // reform: before is actually equivalent to asset amount, after is equivalent to asset weight
    @JsonProperty("harvest_index")//: u128,
    private String harvestIndex;

    @JsonProperty("last_update_timestamp")//: u64,
    private Long lastUpdateTimestamp;//todo is this type ok?

    // Release count per seconds
    @JsonProperty("release_per_second")//: u128,
    private String releasePerSecond;

    //abandoned fields
    // Start time, by seconds, user can operate stake only after this timestamp
    @JsonProperty("start_time")//: u64,
    private Long startTime;//todo is this type ok?

    // Representing the pool is alive, false: not alive, true: alive.
    @JsonProperty("alive")//: bool,
    private Boolean alive;//todo is this type ok?

    public String getAssetTotalWeight() {
        return assetTotalWeight;
    }

    public void setAssetTotalWeight(String assetTotalWeight) {
        this.assetTotalWeight = assetTotalWeight;
    }

    public String getHarvestIndex() {
        return harvestIndex;
    }

    public void setHarvestIndex(String harvestIndex) {
        this.harvestIndex = harvestIndex;
    }

    public Long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public String getReleasePerSecond() {
        return releasePerSecond;
    }

    public void setReleasePerSecond(String releasePerSecond) {
        this.releasePerSecond = releasePerSecond;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "FarmingAsset{" +
                "assetTotalWeight='" + assetTotalWeight + '\'' +
                ", harvestIndex='" + harvestIndex + '\'' +
                ", lastUpdateTimestamp=" + lastUpdateTimestamp +
                ", releasePerSecond='" + releasePerSecond + '\'' +
                ", startTime=" + startTime +
                ", alive=" + alive +
                '}';
    }
}
