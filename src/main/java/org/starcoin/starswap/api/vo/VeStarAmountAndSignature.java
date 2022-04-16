package org.starcoin.starswap.api.vo;

import java.math.BigInteger;

public class VeStarAmountAndSignature {
    private BigInteger veStarAmount;
    private String signature;

    public VeStarAmountAndSignature() {
    }

    public VeStarAmountAndSignature(BigInteger veStarAmount, String signature) {
        this.veStarAmount = veStarAmount;
        this.signature = signature;
    }

    public BigInteger getVeStarAmount() {
        return veStarAmount;
    }

    public void setVeStarAmount(BigInteger veStarAmount) {
        this.veStarAmount = veStarAmount;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
