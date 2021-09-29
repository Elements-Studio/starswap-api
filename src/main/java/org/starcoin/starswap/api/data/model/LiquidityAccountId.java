package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LiquidityAccountId implements Serializable {
    @Column(length = 34)
    private String accountAddress;

    private LiquidityPoolId liquidityPoolId = new LiquidityPoolId();

    public LiquidityAccountId() {
    }

    public LiquidityAccountId(String accountAddress, LiquidityPoolId liquidityPoolId) {
        this.accountAddress = accountAddress;
        this.liquidityPoolId = liquidityPoolId;
    }

    @Column(length = 34)
    protected String getLiquidityTokenAddress() {
        return getLiquidityPoolId().getLiquidityTokenAddress();
    }

    protected void setLiquidityTokenAddress(String address) {
        this.getLiquidityPoolId().setLiquidityTokenAddress(address);
    }

    @Column(length = 34)
    protected String getPoolAddress() {
        return getLiquidityPoolId().getPoolAddress();
    }

    protected void setPoolAddress(String poolAddress) {
        this.getLiquidityPoolId().setPoolAddress(poolAddress);
    }

    @Column(length = 15)
    protected String getTokenXId() {
        return this.getLiquidityPoolId().getLiquidityTokenId().getTokenXId();
    }

    protected void setTokenXId(String tokenXId) {
        this.getLiquidityPoolId().getLiquidityTokenId().setTokenXId(tokenXId);
    }

    @Column(length = 15)
    protected String getTokenYId() {
        return this.getLiquidityPoolId().getLiquidityTokenId().getTokenYId();
    }

    protected void setTokenYId(String tokenYId) {
        this.getLiquidityPoolId().getLiquidityTokenId().setTokenYId(tokenYId);
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public LiquidityPoolId getLiquidityPoolId() {
        return liquidityPoolId;
    }

    public void setLiquidityPoolId(LiquidityPoolId liquidityPoolId) {
        this.liquidityPoolId = liquidityPoolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidityAccountId that = (LiquidityAccountId) o;
        return Objects.equals(accountAddress, that.accountAddress) && Objects.equals(liquidityPoolId, that.liquidityPoolId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountAddress, liquidityPoolId);
    }

    @Override
    public String toString() {
        return "LiquidityAccountId{" +
                "accountAddress='" + accountAddress + '\'' +
                ", liquidityPoolId=" + liquidityPoolId +
                '}';
    }
}
