package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Treasury {
    /*
        struct Treasury has key, store {
            vtoken: VToken::VToken<VESTAR::VESTAR>,
        }
     */
    @JsonProperty("vtoken")
    private VToken vtoken;

    public VToken getVtoken() {
        return vtoken;
    }

    public void setVtoken(VToken vtoken) {
        this.vtoken = vtoken;
    }

    @Override
    public String toString() {
        return "Treasury{" +
                "vtoken=" + vtoken +
                '}';
    }
}
