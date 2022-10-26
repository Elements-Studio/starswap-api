package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FarmPoolStake {
    /*
        //TokenSwapFarm::
        struct FarmPoolStake<phantom X, phantom Y> has key, store {
            id: u64,
            /// Harvest capability for Farm
            cap: YieldFarming::HarvestCapability<PoolTypeFarmPool, coin::Coin<LiquidityToken<X, Y>>>,
        }
        //YieldFarming
        struct HarvestCapability<phantom PoolType, phantom AssetT> has key, store {
            stake_id: u64,
            // Indicates the deadline from start_time for the current harvesting permission,
            // which meaning a timestamp, by seconds
            deadline: u64,
        }
     */
    @JsonProperty("id")
    private Long id;

    @JsonProperty("cap")
    private YieldFarmingHarvestCapability cap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public YieldFarmingHarvestCapability getCap() {
        return cap;
    }

    public void setCap(YieldFarmingHarvestCapability cap) {
        this.cap = cap;
    }

    @Override
    public String toString() {
        return "FarmPoolStake{" +
                "id=" + id +
                ", cap=" + cap +
                '}';
    }
}
