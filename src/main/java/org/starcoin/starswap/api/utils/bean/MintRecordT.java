package org.starcoin.starswap.api.utils.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class MintRecordT {
    /*
        struct MintRecordT<phantom StakeCoinT> has key, store, copy, drop {
        }
     */
    @JsonProperty("id")//: u64,
    private Long id;
    @JsonProperty("minted_amount")//: u128,
    private BigInteger mintedAmount;
    // Vestar amount
    @JsonProperty("staked_amount")//: u128,
    private BigInteger stakedAmount;
    @JsonProperty("pledge_time_sec")//: u64,
    private Long pledgeTimeSec;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getMintedAmount() {
        return mintedAmount;
    }

    public void setMintedAmount(BigInteger mintedAmount) {
        this.mintedAmount = mintedAmount;
    }

    public BigInteger getStakedAmount() {
        return stakedAmount;
    }

    public void setStakedAmount(BigInteger stakedAmount) {
        this.stakedAmount = stakedAmount;
    }

    public Long getPledgeTimeSec() {
        return pledgeTimeSec;
    }

    public void setPledgeTimeSec(Long pledgeTimeSec) {
        this.pledgeTimeSec = pledgeTimeSec;
    }

    @Override
    public String toString() {
        return "MintRecordT{" +
                "id=" + id +
                ", mintedAmount=" + mintedAmount +
                ", stakedAmount=" + stakedAmount +
                ", pledgeTimeSec=" + pledgeTimeSec +
                '}';
    }
}
