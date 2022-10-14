package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class YieldFarmingGlobalPoolInfo {
    /*
        struct YieldFarmingGlobalPoolInfo<phantom PoolType> has key, store {
     */

    // total allocation points. Must be the sum of all allocation points in all pools.
    @JsonProperty("total_alloc_point")//: u128,
    private BigInteger totalAllocPoint;

    @JsonProperty("pool_release_per_second")//: u128,
    private BigInteger poolReleasePerSecond;

    public BigInteger getTotalAllocPoint() {
        return totalAllocPoint;
    }

    public void setTotalAllocPoint(BigInteger totalAllocPoint) {
        this.totalAllocPoint = totalAllocPoint;
    }

    public BigInteger getPoolReleasePerSecond() {
        return poolReleasePerSecond;
    }

    public void setPoolReleasePerSecond(BigInteger poolReleasePerSecond) {
        this.poolReleasePerSecond = poolReleasePerSecond;
    }

    @Override
    public String toString() {
        return "YieldFarmingGlobalPoolInfo{" +
                "totalAllocPoint=" + totalAllocPoint +
                ", poolReleasePerSecond=" + poolReleasePerSecond +
                '}';
    }
}
