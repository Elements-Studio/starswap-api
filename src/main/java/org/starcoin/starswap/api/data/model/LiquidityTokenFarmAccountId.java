package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LiquidityTokenFarmAccountId implements Serializable {
    @Column(length = 34)
    private String accountAddress;

    private LiquidityTokenFarmId liquidityTokenFarmId = new LiquidityTokenFarmId();

    public LiquidityTokenFarmAccountId() {
    }

    public LiquidityTokenFarmAccountId(String accountAddress, LiquidityTokenFarmId liquidityTokenFarmId) {
        this.accountAddress = accountAddress;
        this.liquidityTokenFarmId = liquidityTokenFarmId;
    }

    @Column(length = 34)
    protected String getLiquidityTokenAddress() {
        return getLiquidityTokenFarmId().getLiquidityTokenAddress();
    }

    protected void setLiquidityTokenAddress(String address) {
        this.getLiquidityTokenFarmId().setLiquidityTokenAddress(address);
    }

    @Column(length = 34)
    protected String getFarmAddress() {
        return getLiquidityTokenFarmId().getFarmAddress();
    }

    protected void setFarmAddress(String farmAddress) {
        this.getLiquidityTokenFarmId().setFarmAddress(farmAddress);
    }

    @Column(length = 15)
    protected String getTokenXId() {
        return this.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId();
    }

    protected void setTokenXId(String tokenXId) {
        this.getLiquidityTokenFarmId().getLiquidityTokenId().setTokenXId(tokenXId);
    }

    @Column(length = 15)
    protected String getTokenYId() {
        return this.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId();
    }

    protected void setTokenYId(String tokenYId) {
        this.getLiquidityTokenFarmId().getLiquidityTokenId().setTokenYId(tokenYId);
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public LiquidityTokenFarmId getLiquidityTokenFarmId() {
        return liquidityTokenFarmId;
    }

    public void setLiquidityTokenFarmId(LiquidityTokenFarmId liquidityTokenFarmId) {
        this.liquidityTokenFarmId = liquidityTokenFarmId;
    }

    @Override
    public String toString() {
        return "LiquidityAccountId{" +
                "accountAddress='" + accountAddress + '\'' +
                ", liquidityTokenFarmId=" + liquidityTokenFarmId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidityTokenFarmAccountId that = (LiquidityTokenFarmAccountId) o;
        return Objects.equals(accountAddress, that.accountAddress) && Objects.equals(liquidityTokenFarmId, that.liquidityTokenFarmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountAddress, liquidityTokenFarmId);
    }
}
