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
    private List<TokenSwapVestarMinterMintRecordT> items;

    public List<TokenSwapVestarMinterMintRecordT> getItems() {
        return items;
    }

    public void setItems(List<TokenSwapVestarMinterMintRecordT> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "MintRecordListT{" +
                "items=" + items +
                '}';
    }
}
