package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VToken {
    /*
        struct VToken<phantom CoinT> has key, store {
            token: coin::Coin<CoinT>
        }
     */
    @JsonProperty("token")
    private Coin token;

    public Coin getToken() {
        return token;
    }

    public void setToken(Coin token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "VToken{" +
                "token=" + token +
                '}';
    }
}
