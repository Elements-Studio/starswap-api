package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FarmingAssetExtend {
    //struct FarmingAssetExtend<phantom PoolType, phantom AssetT> has key, store {
    //        asset_total_weight: u128, // Sigma (per user lp_amount *  user boost_factor)
    @JsonProperty("asset_total_amount")//: u128,
    private String assetTotalAmount;

    @JsonProperty("alloc_point")//: u128,
    private String allocPoint;

    public String getAssetTotalAmount() {
        return assetTotalAmount;
    }

    public void setAssetTotalAmount(String assetTotalAmount) {
        this.assetTotalAmount = assetTotalAmount;
    }

    public String getAllocPoint() {
        return allocPoint;
    }

    public void setAllocPoint(String allocPoint) {
        this.allocPoint = allocPoint;
    }

    @Override
    public String toString() {
        return "FarmingAssetExtend{" +
                "assetTotalAmount='" + assetTotalAmount + '\'' +
                ", allocPoint='" + allocPoint + '\'' +
                '}';
    }
}
