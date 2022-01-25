package org.starcoin.starswap.api.data.model;

import java.math.BigInteger;

public class SyrupStake {
    private Long id;

    private BigInteger amount;

    private Long startTime;

    private Long endTime;

    private Integer rewardMultiplier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getRewardMultiplier() {
        return rewardMultiplier;
    }

    public void setRewardMultiplier(Integer rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }
}
