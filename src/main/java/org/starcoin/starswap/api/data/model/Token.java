package org.starcoin.starswap.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "token", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueTokenCode", columnNames = {"token_struct_address", "token_struct_module", "token_struct_name"})
})
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Token {

    /**
     * Token 的 Id。一般应该是缩写，同样的缩写只应该允许注册一次，防止混淆。
     */
    @Id
    @Column(length = 15, nullable = false, unique = true)
    private String tokenId;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "token_struct_address", length = 34, nullable = false))
    @AttributeOverride(name = "module", column = @Column(name = "token_struct_module", nullable = false))
    @AttributeOverride(name = "name", column = @Column(name = "token_struct_name", nullable = false))
    private StructType tokenStructType;

    @Column(length = 1000, nullable = false)
    private String iconUrl;

    @Column(length = 1000, nullable = true)
    private String description;

    @Column(nullable = false)
    private Integer sequenceNumber;

    @Column(precision = 21, scale = 0)
    private BigInteger scalingFactor;

    /**
     * 是否已禁用。
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

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public StructType getTokenStructType() {
        return tokenStructType;
    }

    public void setTokenStructType(StructType tokenStructType) {
        this.tokenStructType = tokenStructType;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getDescriptionEn() {
//        return descriptionEn;
//    }
//
//    public void setDescriptionEn(String descriptionEn) {
//        this.descriptionEn = descriptionEn;
//    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public BigInteger getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(BigInteger scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenId='" + tokenId + '\'' +
                ", tokenStructType=" + tokenStructType +
                ", iconUrl='" + iconUrl + '\'' +
                ", description='" + description + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", scalingFactor=" + scalingFactor +
                ", deactived=" + deactived +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}
