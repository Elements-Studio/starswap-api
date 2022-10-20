package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MultiplierPoolsGlobalInfo {
    /*
        struct MultiplierPoolsGlobalInfo<phantom PoolType, phantom AssetType> has key, store {
            items: vector<MultiplierPool<PoolType, AssetType>>,
        }
     */
    @JsonProperty("items")
    private List<MultiplierPool> items;

    public List<MultiplierPool> getItems() {
        return items;
    }

    public void setItems(List<MultiplierPool> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "MultiplierPoolsGlobalInfo{" +
                "items=" + items +
                '}';
    }

    public static class MultiplierPool {
        /*
            struct MultiplierPool<phantom PoolType, phantom AssetType> has key, store {
                key: vector<u8>,
                asset_weight: u128,
                asset_amount: u128,
                multiplier: u64,
            }
         */
        @JsonProperty("key")
        private String key;
        @JsonProperty("asset_weight")
        private String assetWeight;
        @JsonProperty("asset_amount")
        private String assetAmount;
        @JsonProperty("multiplier")
        private Long multiplier;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getAssetWeight() {
            return assetWeight;
        }

        public void setAssetWeight(String assetWeight) {
            this.assetWeight = assetWeight;
        }

        public String getAssetAmount() {
            return assetAmount;
        }

        public void setAssetAmount(String assetAmount) {
            this.assetAmount = assetAmount;
        }

        public Long getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(Long multiplier) {
            this.multiplier = multiplier;
        }

        @Override
        public String toString() {
            return "MultiplierPool{" +
                    "key='" + key + '\'' +
                    ", assetWeight='" + assetWeight + '\'' +
                    ", assetAmount='" + assetAmount + '\'' +
                    ", multiplier=" + multiplier +
                    '}';
        }
    }
}
