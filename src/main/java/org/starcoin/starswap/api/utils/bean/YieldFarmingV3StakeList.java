package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YieldFarmingV3StakeList {
    /*
        struct StakeList<phantom PoolType, AssetT> has key, store {
            next_id: u64,
            items: vector<Stake<PoolType, AssetT>>,
        }
     */
    @JsonProperty("next_id")//: u64,
    private Long nextId;
    @JsonProperty("items")
    private List<YieldFarmingV3Stake> items;//: vector<Stake<PoolType, AssetT>>,

    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    public List<YieldFarmingV3Stake> getItems() {
        return items;
    }

    public void setItems(List<YieldFarmingV3Stake> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "StakeList{" +
                "nextId=" + nextId +
                ", items=" + items +
                '}';
    }
}
