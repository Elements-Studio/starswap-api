package org.starcoin.starswap.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.aptos.bean.TypeInfo;

public class AddFarmEvent {
    /*
    struct AddFarmEvent has drop, store {
        /// token code of X type
        x_token_code: type_info::TypeInfo,
        /// token code of X type
        y_token_code: type_info::TypeInfo,
        /// signer of farm add
        signer: address,
        /// admin address
        admin: address,
    }
     */

    @JsonProperty("x_type_info")
    private TypeInfo xTypeInfo;
    @JsonProperty("y_type_info")
    private TypeInfo yTypeInfo;
    @JsonProperty("signer")
    private String signer;
    @JsonProperty("admin")
    private String admin;

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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "AddFarmEvent{" +
                "xTypeInfo=" + xTypeInfo +
                ", yTypeInfo=" + yTypeInfo +
                ", signer='" + signer + '\'' +
                ", admin='" + admin + '\'' +
                '}';
    }
}
