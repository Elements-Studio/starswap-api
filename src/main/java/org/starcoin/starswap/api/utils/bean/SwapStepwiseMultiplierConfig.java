package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SwapStepwiseMultiplierConfig {
    /*
        struct SwapStepwiseMultiplierConfig has copy, drop, store {
            list: vector<StepwiseMutiplier>,
        }
     */
    @JsonProperty("list")
    private List<StepwiseMultiplier> list;

    public List<StepwiseMultiplier> getList() {
        return list;
    }

    public void setList(List<StepwiseMultiplier> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "SwapStepwiseMultiplierConfig{" +
                "list=" + list +
                '}';
    }

    public static class StepwiseMultiplier {
        /*
            struct StepwiseMutiplier has copy, drop, store {
                interval_sec: u64,
                multiplier: u64,
            }
         */
        @JsonProperty("interval_sec")
        private Long intervalSec;
        @JsonProperty("multiplier")
        private Long multiplier;

        public Long getIntervalSec() {
            return intervalSec;
        }

        public void setIntervalSec(Long intervalSec) {
            this.intervalSec = intervalSec;
        }

        public Long getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(Long multiplier) {
            this.multiplier = multiplier;
        }

        @Override
        public String toString() {
            return "StepwiseMultiplier{" +
                    "intervalSec=" + intervalSec +
                    ", multiplier=" + multiplier +
                    '}';
        }
    }

}
