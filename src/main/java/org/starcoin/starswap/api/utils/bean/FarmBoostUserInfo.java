package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class FarmBoostUserInfo {
    //    struct UserInfo<phantom X, phantom Y> has key, store {
    @JsonProperty("boost_factor")//: u64,
    private Long boostFactor;
    @JsonProperty("locked_vetoken")//: VToken<VESTAR>,
    private VToken lockedVetoken;
    @JsonProperty("user_amount")//: u128,
    private BigInteger userAmount;

    public Long getBoostFactor() {
        return boostFactor;
    }

    public void setBoostFactor(Long boostFactor) {
        this.boostFactor = boostFactor;
    }

    public VToken getLockedVetoken() {
        return lockedVetoken;
    }

    public void setLockedVetoken(VToken lockedVetoken) {
        this.lockedVetoken = lockedVetoken;
    }

    public BigInteger getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(BigInteger userAmount) {
        this.userAmount = userAmount;
    }

    @Override
    public String toString() {
        return "StarswapUserInfo{" +
                "boostFactor=" + boostFactor +
                ", lockedVeToken=" + lockedVetoken +
                ", userAmount=" + userAmount +
                '}';
    }
}
