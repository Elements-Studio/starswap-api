package org.starcoin.starswap.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.wubuku.aptos.bean.TypeInfo;

public class AddLiquidityEvent {
    @JsonProperty("liquidity")//: u128,
    private String liquidity;
    @JsonProperty("x_type_info")//: type_info::TypeInfo,
    private TypeInfo xTypeInfo;
    @JsonProperty("y_type_info")//: type_info::TypeInfo,
    private TypeInfo yTypeInfo;
    @JsonProperty("signer")//: address,
    private String signer;
    @JsonProperty("amount_x_desired")//: u128,
    private String amountXDesired;
    @JsonProperty("amount_y_desired")//: u128,
    private String amountYDesired;
    @JsonProperty("amount_x_min")//: u128,
    private String amountXMin;
    @JsonProperty("amount_y_min")//: u128,
    private String amountYMin;

    public String getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(String liquidity) {
        this.liquidity = liquidity;
    }

    public TypeInfo getXTypeInfo() {
        return xTypeInfo;
    }

    public void setXTypeInfo(TypeInfo xTypeInfo) {
        this.xTypeInfo = xTypeInfo;
    }

    public TypeInfo getYTypeInfo() {
        return yTypeInfo;
    }

    public void setYTypeInfo(TypeInfo yTypeInfo) {
        this.yTypeInfo = yTypeInfo;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getAmountXDesired() {
        return amountXDesired;
    }

    public void setAmountXDesired(String amountXDesired) {
        this.amountXDesired = amountXDesired;
    }

    public String getAmountYDesired() {
        return amountYDesired;
    }

    public void setAmountYDesired(String amountYDesired) {
        this.amountYDesired = amountYDesired;
    }

    public String getAmountXMin() {
        return amountXMin;
    }

    public void setAmountXMin(String amountXMin) {
        this.amountXMin = amountXMin;
    }

    public String getAmountYMin() {
        return amountYMin;
    }

    public void setAmountYMin(String amountYMin) {
        this.amountYMin = amountYMin;
    }

    @Override
    public String toString() {
        return "AddLiquidityEvent{" +
                "liquidity='" + liquidity + '\'' +
                ", xTypeInfo=" + xTypeInfo +
                ", yTypeInfo=" + yTypeInfo +
                ", signer='" + signer + '\'' +
                ", amountXDesired='" + amountXDesired + '\'' +
                ", amountYDesired='" + amountYDesired + '\'' +
                ", amountXMin='" + amountXMin + '\'' +
                ", amountYMin='" + amountYMin + '\'' +
                '}';
    }
}
