package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LiquidityPoolId implements Serializable {

    /**
     * 池子的地址。（流动性存放在哪里。）
     */
    @Column(length = 34)
    private String poolAddress;

    private LiquidityTokenId liquidityTokenId = new LiquidityTokenId();

    public LiquidityPoolId() {
    }

    public LiquidityPoolId(LiquidityTokenId liquidityTokenId, String poolAddress) {
        this.liquidityTokenId = liquidityTokenId;
        this.poolAddress = poolAddress;
    }

    public LiquidityPoolId(String tokenXId, String tokenYId, String liquidityTokenAddress, String poolAddress) {
        this.liquidityTokenId = new LiquidityTokenId(tokenXId, tokenYId, liquidityTokenAddress);
        this.poolAddress = poolAddress;
    }

    @Column(length = 15)
    protected String getTokenXId() {
        return this.getLiquidityTokenId().getTokenXId();
    }

    protected void setTokenXId(String tokenXId) {
        this.getLiquidityTokenId().setTokenXId(tokenXId);
    }

    @Column(length = 15)
    protected String getTokenYId() {
        return this.getLiquidityTokenId().getTokenYId();
    }

    protected void setTokenYId(String tokenYId) {
        this.getLiquidityTokenId().setTokenYId(tokenYId);
    }

    @Column(length = 34)
    protected String getLiquidityTokenAddress() {
        return this.getLiquidityTokenId().getLiquidityTokenAddress();
    }

    protected void setLiquidityTokenAddress(String address) {
        this.getLiquidityTokenId().setLiquidityTokenAddress(address);
    }

    public LiquidityTokenId getLiquidityTokenId() {
        return liquidityTokenId;
    }

    public void setLiquidityTokenId(LiquidityTokenId liquidityTokenId) {
        this.liquidityTokenId = liquidityTokenId;
    }

    public String getPoolAddress() {
        return poolAddress;
    }

    public void setPoolAddress(String poolAddress) {
        this.poolAddress = poolAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidityPoolId that = (LiquidityPoolId) o;
        return Objects.equals(poolAddress, that.poolAddress) && Objects.equals(liquidityTokenId, that.liquidityTokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poolAddress, liquidityTokenId);
    }

    @Override
    public String toString() {
        return "LiquidityPoolId{" +
                "poolAddress='" + poolAddress + '\'' +
                ", liquidityTokenId=" + liquidityTokenId +
                '}';
    }
}
