package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FarmPoolInfo {
    /*
    //TokenSwapFarm::
            struct FarmPoolInfo<phantom X, phantom Y> has key, store {
                alloc_point: u128
            }
     */
    @JsonProperty("alloc_point")
    private String allocPoint;

    public String getAllocPoint() {
        return allocPoint;
    }

    public void setAllocPoint(String allocPoint) {
        this.allocPoint = allocPoint;
    }

    @Override
    public String toString() {
        return "FarmPoolInfo{" +
                "allocPoint='" + allocPoint + '\'' +
                '}';
    }
}
