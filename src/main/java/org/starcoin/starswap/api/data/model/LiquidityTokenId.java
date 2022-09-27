package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Token Pair 的 Id。
 */
@Embeddable
public class LiquidityTokenId implements Serializable {

    @Column(name = "token_x_id", length = 15, nullable = false)
    private String tokenXId;

    @Column(name = "token_y_id", length = 15, nullable = false)
    private String tokenYId;

    @Column(name = "liquidity_token_address", length = 66, nullable = false)
    private String liquidityTokenAddress;

    public LiquidityTokenId() {
    }

    public LiquidityTokenId(String tokenXId, String tokenYId, String liquidityTokenAddress) {
        TokenIdPair tokenIdPair = new TokenIdPair(tokenXId, tokenYId);
        this.tokenXId = tokenIdPair.tokenXId();
        this.tokenYId = tokenIdPair.tokenYId();
        this.liquidityTokenAddress = liquidityTokenAddress;
    }

    public String getLiquidityTokenAddress() {
        return liquidityTokenAddress;
    }

    public void setLiquidityTokenAddress(String liquidityTokenAddress) {
        this.liquidityTokenAddress = liquidityTokenAddress;
    }

    public String getTokenXId() {
        return tokenXId;
    }

    public void setTokenXId(String tokenXId) {
        this.tokenXId = tokenXId;
    }

    public String getTokenYId() {
        return tokenYId;
    }

    public void setTokenYId(String tokenYId) {
        this.tokenYId = tokenYId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidityTokenId that = (LiquidityTokenId) o;
        return Objects.equals(tokenXId, that.tokenXId) && Objects.equals(tokenYId, that.tokenYId) && Objects.equals(liquidityTokenAddress, that.liquidityTokenAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenXId, tokenYId, liquidityTokenAddress);
    }

    @Override
    public String toString() {
        return "LiquidityTokenId{" +
                "tokenXId='" + tokenXId + '\'' +
                ", tokenYId='" + tokenYId + '\'' +
                ", liquidityTokenAddress='" + liquidityTokenAddress + '\'' +
                '}';
    }
}
