package org.starcoin.starswap.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SyrupPoolStakeEvent {
    /*
         struct StakeEvent has drop, store {
             /// token code of X type
             type_info: type_info::TypeInfo,
             /// signer of stake user
             signer: address,
             // value of stake user
             amount: u128,
             /// admin address
             admin: address,
         }
     */
    @JsonProperty("type_info")
    private String typeInfo;
    @JsonProperty("signer")
    private String signer;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("admin")
    private String admin;

    public String getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(String typeInfo) {
        this.typeInfo = typeInfo;
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
        return "SyrupPoolStakeEvent{" +
                "typeInfo='" + typeInfo + '\'' +
                ", signer='" + signer + '\'' +
                ", amount='" + amount + '\'' +
                ", admin='" + admin + '\'' +
                '}';
    }
}
