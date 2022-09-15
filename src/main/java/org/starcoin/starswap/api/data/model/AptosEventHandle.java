package org.starcoin.starswap.api.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "aptos_event_handle", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueEventJavaType", columnNames = {"event_java_type"})
})
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class AptosEventHandle {

    @EmbeddedId
    @AttributeOverride(name = "accountAddress", column = @Column(name = "account_address", length = 66, nullable = false))
    @AttributeOverride(name = "eventHandleStruct", column = @Column(name = "event_handle_struct", length = 300, nullable = false))
    @AttributeOverride(name = "eventHandleFieldName", column = @Column(name = "event_handle_field_name", length = 100, nullable = false))
    private AptosEventHandleId aptosEventHandleId;

    /**
     * a list of events of a specific type as identified by its event key, which is a globally unique ID.
     */
    private String eventKey;

    @Column(name = "event_java_type", length = 50)
    private String eventJavaType;

    @Column(precision = 31, scale = 0)
    private BigInteger nextSequenceNumber;

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

    public AptosEventHandleId getAptosEventHandleId() {
        return aptosEventHandleId;
    }

    public void setAptosEventHandleId(AptosEventHandleId aptosEventHandleId) {
        this.aptosEventHandleId = aptosEventHandleId;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public BigInteger getNextSequenceNumber() {
        return nextSequenceNumber;
    }

    public void setNextSequenceNumber(BigInteger nextSequenceNumber) {
        this.nextSequenceNumber = nextSequenceNumber;
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

    public String getEventJavaType() {
        return eventJavaType;
    }

    public void setEventJavaType(String eventJavaType) {
        this.eventJavaType = eventJavaType;
    }

    @Override
    public String toString() {
        return "AptosEventHandle{" +
                "aptosEventHandleId=" + aptosEventHandleId +
                ", eventKey='" + eventKey + '\'' +
                ", eventJavaType='" + eventJavaType + '\'' +
                ", nextSequenceNumber=" + nextSequenceNumber +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}
