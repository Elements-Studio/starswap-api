package org.starcoin.starswap.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StakeEvent {
    /*
     /// Event emitted when stake been called
     struct StakeEvent has drop, store {
         /// token code of X type
         x_token_code: type_info::TypeInfo,
         /// token code of X type
         y_token_code: type_info::TypeInfo,
         /// signer of stake user
         signer: address,
         // value of stake user
         amount: u128,
         /// admin address
         admin: address,
     }
     */
    @JsonProperty("x_type_info")
    private String xTypeInfo;
    @JsonProperty("y_type_info")
    private String yTypeInfo;
    @JsonProperty("signer")
    private String signer;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("admin")
    private String admin;

    public String getXTypeInfo() {
        return xTypeInfo;
    }

    public void setXTypeInfo(String xTypeInfo) {
        this.xTypeInfo = xTypeInfo;
    }

    public String getYTypeInfo() {
        return yTypeInfo;
    }

    public void setYTypeInfo(String yTypeInfo) {
        this.yTypeInfo = yTypeInfo;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "StakeEvent{" +
                "xTypeInfo='" + xTypeInfo + '\'' +
                ", yTypeInfo='" + yTypeInfo + '\'' +
                ", signer='" + signer + '\'' +
                ", amount='" + amount + '\'' +
                ", admin='" + admin + '\'' +
                '}';
    }
}
