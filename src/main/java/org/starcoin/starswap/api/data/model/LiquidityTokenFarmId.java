package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LiquidityTokenFarmId implements Serializable {

    @Column(length = 66)
    private String farmAddress;

    private LiquidityTokenId liquidityTokenId = new LiquidityTokenId();

    public LiquidityTokenFarmId() {
    }

    public LiquidityTokenFarmId(LiquidityTokenId liquidityTokenId, String farmAddress) {
        this.liquidityTokenId = liquidityTokenId;
        this.farmAddress = farmAddress;
    }

    public LiquidityTokenFarmId(String tokenXId, String tokenYId, String liquidityTokenAddress, String farmAddress) {
        this.liquidityTokenId = new LiquidityTokenId(tokenXId, tokenYId, liquidityTokenAddress);
        this.farmAddress = farmAddress;
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

    @Column(length = 66)
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

    public String getFarmAddress() {
        return farmAddress;
    }

    public void setFarmAddress(String farmAddress) {
        this.farmAddress = farmAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidityTokenFarmId that = (LiquidityTokenFarmId) o;
        return Objects.equals(farmAddress, that.farmAddress) && Objects.equals(liquidityTokenId, that.liquidityTokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(farmAddress, liquidityTokenId);
    }

    @Override
    public String toString() {
        return "LiquidityTokenFarmId{" +
                "farmAddress='" + farmAddress + '\'' +
                ", liquidityTokenId=" + liquidityTokenId +
                '}';
    }
}
