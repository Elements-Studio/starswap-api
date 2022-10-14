package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MintRecordListT {
    /*
        struct MintRecordListT<phantom StakeCoinT> has key, store {
            items: vector<MintRecordT<StakeCoinT>>
        }
     */
    @JsonProperty("items")
    private List<MintRecordT> items;

    public List<MintRecordT> getItems() {
        return items;
    }

    public void setItems(List<MintRecordT> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "MintRecordListT{" +
                "items=" + items +
                '}';
    }
}
