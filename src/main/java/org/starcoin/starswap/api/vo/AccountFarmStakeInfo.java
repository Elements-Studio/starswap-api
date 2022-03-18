package org.starcoin.starswap.api.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AccountFarmStakeInfo {
    /**
     * Account staked liquidity.
     */
    private BigInteger stakedLiquidity;

    /**
     * Total liquidity staked in farm.
     */
    private BigInteger farmTotalLiquidity;

    /**
     * Account staked liquidity share in farm.
     */
    private BigDecimal sharePercentage;

    /**
     * Account staked liquidity amount in USD.
     */
    private BigDecimal stakedAmountInUsd;

    /**
     * TokenX amount which account staked liquidity have.
     */
    private TokenAmount tokenXAmount;

    /**
     * TokenY amount which account staked liquidity have.
     */
    private TokenAmount tokenYAmount;

    public AccountFarmStakeInfo() {
    }

    public BigInteger getStakedLiquidity() {
        return stakedLiquidity;
    }

    public void setStakedLiquidity(BigInteger stakedLiquidity) {
        this.stakedLiquidity = stakedLiquidity;
    }

    public BigDecimal getStakedAmountInUsd() {
        return stakedAmountInUsd;
    }

    public void setStakedAmountInUsd(BigDecimal stakedAmountInUsd) {
        this.stakedAmountInUsd = stakedAmountInUsd;
    }

    public TokenAmount getTokenXAmount() {
        return tokenXAmount;
    }

    public void setTokenXAmount(TokenAmount tokenXAmount) {
        this.tokenXAmount = tokenXAmount;
    }

    public TokenAmount getTokenYAmount() {
        return tokenYAmount;
    }

    public void setTokenYAmount(TokenAmount tokenYAmount) {
        this.tokenYAmount = tokenYAmount;
    }

    public BigInteger getFarmTotalLiquidity() {
        return farmTotalLiquidity;
    }

    public void setFarmTotalLiquidity(BigInteger farmTotalLiquidity) {
        this.farmTotalLiquidity = farmTotalLiquidity;
    }

    public BigDecimal getSharePercentage() {
        return sharePercentage;
    }

    public void setSharePercentage(BigDecimal sharePercentage) {
        this.sharePercentage = sharePercentage;
    }

    @Override
    public String toString() {
        return "AccountFarmStakeInfo{" +
                "stakedLiquidity=" + stakedLiquidity +
                ", farmTotalLiquidity=" + farmTotalLiquidity +
                ", sharePercentage=" + sharePercentage +
                ", stakedAmountInUsd=" + stakedAmountInUsd +
                ", tokenXAmount=" + tokenXAmount +
                ", tokenYAmount=" + tokenYAmount +
                '}';
    }

    public static class TokenAmount {

        private String tokenId;
        private String tokenType;
        private BigInteger amount;

        public TokenAmount() {
        }

        public TokenAmount(String tokenId, String tokenType, BigInteger amount) {
            this.tokenId = tokenId;
            this.tokenType = tokenType;
            this.amount = amount;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public BigInteger getAmount() {
            return amount;
        }

        public void setAmount(BigInteger amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "TokenAmount{" +
                    "tokenId='" + tokenId + '\'' +
                    ", tokenType='" + tokenType + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }
}
