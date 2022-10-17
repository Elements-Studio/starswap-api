package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;

public class SyrupStakeList {
    /*
        // TokenSwapSyrup::
        struct SyrupStakeList<phantom CoinT> has key, store {
            items: vector<SyrupStake<CoinT>>,
        }
     */
    @JsonProperty("items")
    private List<SyrupStake> items;

    public List<SyrupStake> getItems() {
        return items;
    }

    public void setItems(List<SyrupStake> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "SyrupStakeList{" +
                "items=" + items +
                '}';
    }

    public static class SyrupStake {
        /*
            struct SyrupStake<phantom CoinT> has key, store {
                id: u64,
                /// Harvest capability for Syrup
                harvest_cap: YieldFarming::HarvestCapability<PoolTypeSyrup, coin::Coin<CoinT>>,
                /// Stepwise multiplier
                stepwise_multiplier: u64,
                /// Stake amount
                token_amount: u128,
                /// The time stamp of start staking
                start_time: u64,
                /// The time stamp of end staking, user can unstake/harvest after this point
                end_time: u64,
            }
         */
        @JsonProperty("id")
        private Long id;
        /// Harvest capability for Syrup
        @JsonProperty("harvest_cap")//: YieldFarming::HarvestCapability<PoolTypeSyrup, coin::Coin<CoinT>>,
        private YieldFarmingHarvestCapability harvestCap;
        /// Stepwise multiplier
        @JsonProperty("stepwise_multiplier")//: u64,
        private Long stepwiseMultiplier;
        /// Stake amount
        @JsonProperty("token_amount")//: u128,
        private BigInteger tokenAmount;
        /// The time stamp of start staking
        @JsonProperty("start_time")//: u64,
        private Long startTime;
        /// The time stamp of end staking, user can unstake/harvest after this point
        @JsonProperty("end_time")//: u64,
        private Long endTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public YieldFarmingHarvestCapability getHarvestCap() {
            return harvestCap;
        }

        public void setHarvestCap(YieldFarmingHarvestCapability harvestCap) {
            this.harvestCap = harvestCap;
        }

        public Long getStepwiseMultiplier() {
            return stepwiseMultiplier;
        }

        public void setStepwiseMultiplier(Long stepwiseMultiplier) {
            this.stepwiseMultiplier = stepwiseMultiplier;
        }

        public BigInteger getTokenAmount() {
            return tokenAmount;
        }

        public void setTokenAmount(BigInteger tokenAmount) {
            this.tokenAmount = tokenAmount;
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

        @Override
        public String toString() {
            return "SyrupStake{" +
                    "id=" + id +
                    ", harvestCap=" + harvestCap +
                    ", stepwiseMultiplier=" + stepwiseMultiplier +
                    ", tokenAmount=" + tokenAmount +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    '}';
        }
    }
}
