package org.starcoin.starswap.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@Table(indexes = {@Index(name = "idx_token_id", columnList = "token_id")})
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class SyrupPoolAccount {

    /**
     * 账号 Id（领域键）。
     */
    @EmbeddedId
    @AttributeOverride(name = "tokenId", column = @Column(name = "token_id", nullable = false))
    @AttributeOverride(name = "poolAddress", column = @Column(name = "pool_address", length = 34, nullable = false))
    @AttributeOverride(name = "accountAddress", column = @Column(name = "account_address", length = 34, nullable = false))
    private SyrupPoolAccountId syrupPoolAccountId;

    /**
     * 是否已不再使用。
     */
    @Column(nullable = false)
    private Boolean deactived;

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

    public SyrupPoolAccountId getSyrupPoolAccountId() {
        return syrupPoolAccountId;
    }

    public void setSyrupPoolAccountId(SyrupPoolAccountId syrupPoolAccountId) {
        this.syrupPoolAccountId = syrupPoolAccountId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeactived() {
        return deactived;
    }

    public void setDeactived(Boolean deactived) {
        this.deactived = deactived;
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

}
