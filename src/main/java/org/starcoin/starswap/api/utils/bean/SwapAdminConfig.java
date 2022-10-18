package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwapAdminConfig<T> {
    /*
        /// A generic singleton resource that holds a value of a specific type.
        struct Config<ConfigValue: copy + drop + store> has key { payload: ConfigValue }
     */
    @JsonProperty("payload")
    private T payload;

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "StarswapConfig{" +
                "payload=" + payload +
                '}';
    }

    public static class SwapStepwiseMultiplierConfig
            extends SwapAdminConfig<org.starcoin.starswap.api.utils.bean.SwapStepwiseMultiplierConfig> {
    }

}
