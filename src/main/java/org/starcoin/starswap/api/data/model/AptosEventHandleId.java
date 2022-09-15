package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AptosEventHandleId implements Serializable {

    @Column(length = 66, nullable = false)
    private String accountAddress;

    @Column(length = 300, nullable = false)
    private String eventHandleStruct;

    @Column(length = 100, nullable = false)
    private String eventHandleFieldName;


    public AptosEventHandleId() {
    }

    public AptosEventHandleId(String accountAddress, String eventHandleStruct, String eventHandleFieldName) {
        this.accountAddress = accountAddress;
        this.eventHandleStruct = eventHandleStruct;
        this.eventHandleFieldName = eventHandleFieldName;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public String getEventHandleStruct() {
        return eventHandleStruct;
    }

    public void setEventHandleStruct(String eventHandleStruct) {
        this.eventHandleStruct = eventHandleStruct;
    }

    public String getEventHandleFieldName() {
        return eventHandleFieldName;
    }

    public void setEventHandleFieldName(String eventHandleFieldName) {
        this.eventHandleFieldName = eventHandleFieldName;
    }

    @Override
    public String toString() {
        return "AptosEventHandleId{" +
                "accountAddress='" + accountAddress + '\'' +
                ", eventHandleStruct='" + eventHandleStruct + '\'' +
                ", eventHandleFieldName='" + eventHandleFieldName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AptosEventHandleId that = (AptosEventHandleId) o;
        return Objects.equals(accountAddress, that.accountAddress) && Objects.equals(eventHandleStruct, that.eventHandleStruct) && Objects.equals(eventHandleFieldName, that.eventHandleFieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountAddress, eventHandleStruct, eventHandleFieldName);
    }
}
