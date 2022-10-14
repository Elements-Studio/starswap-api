package org.starcoin.starswap.api.data.model;

import java.math.BigInteger;

public class SyrupStake {
    private Long id;

    private BigInteger amount;

    private Long startTime;

    private Long endTime;

//    private Long rewardReleasePerSecond;
//
//    private Integer rewardMultiplier;

    private Integer stepwiseMultiplier;

    private BigInteger expectedGain;

    private BigInteger veStarAmount;//todo rename to veStarAmount?

    private String tokenTypeTag;

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

//    public Integer getRewardMultiplier() {
//        return rewardMultiplier;
//    }
//
//    public void setRewardMultiplier(Integer rewardMultiplier) {
//        this.rewardMultiplier = rewardMultiplier;
//    }
//
//    public Long getRewardReleasePerSecond() {
//        return rewardReleasePerSecond;
//    }
//
//    public void setRewardReleasePerSecond(Long rewardReleasePerSecond) {
//        this.rewardReleasePerSecond = rewardReleasePerSecond;
//    }

    public Integer getStepwiseMultiplier() {
        return stepwiseMultiplier;
    }

    public void setStepwiseMultiplier(Integer stepwiseMultiplier) {
        this.stepwiseMultiplier = stepwiseMultiplier;
    }

    public BigInteger getExpectedGain() {
        return expectedGain;
    }

    public void setExpectedGain(BigInteger expectedGain) {
        this.expectedGain = expectedGain;
    }

    public BigInteger getVeStarAmount() {
        return veStarAmount;
    }

    public void setVeStarAmount(BigInteger veStarAmount) {
        this.veStarAmount = veStarAmount;
    }

    public String getTokenTypeTag() {
        return tokenTypeTag;
    }

    public void setTokenTypeTag(String tokenTypeTag) {
        this.tokenTypeTag = tokenTypeTag;
    }

    @Override
    public String toString() {
        return "SyrupStake{" +
                "id=" + id +
                ", amount=" + amount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", stepwiseMultiplier=" + stepwiseMultiplier +
                ", expectedGain=" + expectedGain +
                ", veStarAmount=" + veStarAmount +
                ", tokenTypeTag='" + tokenTypeTag + '\'' +
                '}';
    }
}
