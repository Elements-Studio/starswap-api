package org.starcoin.starswap.api.vo;

import java.math.BigInteger;

/**
 * todo rename to VestarAmountAndSignature?
 */
public class VeStarAmountAndSignature {
    private BigInteger veStarAmount;//todo rename to vestarAmount?
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
