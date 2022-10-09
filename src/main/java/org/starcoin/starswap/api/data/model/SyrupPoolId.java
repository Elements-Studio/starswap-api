package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SyrupPoolId implements Serializable {

    /**
     * 池子的地址。
     */
    @Column(length = 66)
    private String poolAddress;

    @Column(name = "token_id", length = 15, nullable = false)
    private String tokenId;

    public String getPoolAddress() {
        return poolAddress;
    }

    public void setPoolAddress(String poolAddress) {
        this.poolAddress = poolAddress;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "SyrupPoolId{" +
                "poolAddress='" + poolAddress + '\'' +
                ", tokenId='" + tokenId + '\'' +
                '}';
    }
}
