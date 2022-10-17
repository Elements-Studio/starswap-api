package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YieldFarmingHarvestCapability {
    /*
    //YieldFarming::
        struct HarvestCapability<phantom PoolType, phantom AssetT> has key, store {
            stake_id: u64,
            // Indicates the deadline from start_time for the current harvesting permission,
            // which meaning a timestamp, by seconds
            deadline: u64,
        }
     */
    @JsonProperty("stake_id")
    private Long stakeId;
    @JsonProperty("deadline")
    private Long deadline;

    public Long getStakeId() {
        return stakeId;
    }

    public void setStakeId(Long stakeId) {
        this.stakeId = stakeId;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "YieldFarmingHarvestCapability{" +
                "stakeId=" + stakeId +
                ", deadline=" + deadline +
                '}';
    }
}
