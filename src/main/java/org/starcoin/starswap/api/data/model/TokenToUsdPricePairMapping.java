package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * Token 到（兑美元价格的）币对（Pair）的映射信息。在调用 Price Oracle API 时使用。
 * 在 Price Oracle 的模型中只存在 token pair 的概念，并没有独立的 token 实体。
 * Price Oracle 并不关心币对（pair）涉及的 token 是哪条链上的资产。即不管 token 是否在 Stacoin 链上，也不管其在链上的地址、模块、名称等。
 * 其他项目在调用 Price Oracle 时需要自行处理这些具体问题，因为在 Price Oracle 中的 token 是一个更抽象的概念。
 */
@Entity
public class TokenToUsdPricePairMapping {

    public static final String DEFAULT_MAPPING_RULE_TOKEN_ID = "{TOKEN_ID}";

    /**
     * Token 的 Id。如果等于“{TOKEN_ID}”，表示本记录是默认映射规则。
     * 如果是默认映射规则，使用实际的 token Id 替换 pair Id 中的“{TOKEN_ID}”占位符，得到具体的 pair Id。
     */
    @Id
    @Column(length = 15)
    private String tokenId;

    /**
     * 代币兑 USD 的币对 Id（Pair Id）。
     */
    @Column(length = 25)
    private String pairId;

    @Column
    private Boolean isUsdEquivalentToken = false;

    @Column(length = 70, nullable = false)
    private String createdBy;

    @Column(length = 70, nullable = false)
    private String updatedBy;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;

    @Version
    private Long version;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getPairId() {
        return pairId;
    }

    public void setPairId(String pairId) {
        this.pairId = pairId;
    }

    public Boolean getUsdEquivalentToken() {
        return isUsdEquivalentToken;
    }

    public void setUsdEquivalentToken(Boolean usdEquivalentToken) {
        isUsdEquivalentToken = usdEquivalentToken;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "TokenToUsdPricePairMapping{" +
                "tokenId='" + tokenId + '\'' +
                ", pairId='" + pairId + '\'' +
                ", isUsdEquivalentToken=" + isUsdEquivalentToken +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}
