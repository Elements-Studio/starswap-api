package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YieldFarmingV3StakeListExtend {
    /*
        //YieldFarmingV3::
        struct StakeListExtend<phantom PoolType, phantom AssetT> has key, store {
            next_id: u64,
            items: vector<StakeExtend<PoolType, AssetT>>,
        }
     */
    @JsonProperty("next_id")
    private Long nextId;
    @JsonProperty("items")
    private List<YieldFarmingV3StakeExtend> items;

    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    public List<YieldFarmingV3StakeExtend> getItems() {
        return items;
    }

    public void setItems(List<YieldFarmingV3StakeExtend> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "StakeListExtend{" +
                "nextId=" + nextId +
                ", items=" + items +
                '}';
    }
}
