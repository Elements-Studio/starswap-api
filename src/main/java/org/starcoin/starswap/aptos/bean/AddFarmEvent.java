package org.starcoin.starswap.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.aptos.bean.TypeInfo;

public class AddFarmEvent {
    @JsonProperty("x_token_code")
    private TypeInfo xTokenCode; //todo Is this name?
    @JsonProperty("y_token_code")
    private TypeInfo yTokenCode;
    @JsonProperty("signer")
    private String signer;
    @JsonProperty("admin")
    private String admin;
}
