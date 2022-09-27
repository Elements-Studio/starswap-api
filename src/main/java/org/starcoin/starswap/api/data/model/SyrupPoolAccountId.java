package org.starcoin.starswap.api.data.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SyrupPoolAccountId implements Serializable {
    @Column(length = 66)
    private String accountAddress;

    private SyrupPoolId syrupPoolId = new SyrupPoolId();

    public SyrupPoolAccountId() {
    }

    public SyrupPoolAccountId(String accountAddress, SyrupPoolId syrupPoolId) {
        this.accountAddress = accountAddress;
        this.syrupPoolId = syrupPoolId;
    }

    @Column(length = 66)
    protected String getPoolAddress() {
        return getSyrupPoolId().getPoolAddress();
    }

    protected void setPoolAddress(String poolAddress) {
        this.getSyrupPoolId().setPoolAddress(poolAddress);
    }

    @Column(length = 15)
    protected String getTokenId() {
        return this.getSyrupPoolId().getTokenId();
    }

    protected void setTokenId(String tokenId) {
        this.getSyrupPoolId().setTokenId(tokenId);
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public SyrupPoolId getSyrupPoolId() {
        return syrupPoolId;
    }

    public void setSyrupPoolId(SyrupPoolId syrupPoolId) {
        this.syrupPoolId = syrupPoolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyrupPoolAccountId that = (SyrupPoolAccountId) o;
        return Objects.equals(accountAddress, that.accountAddress) && Objects.equals(syrupPoolId, that.syrupPoolId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountAddress, syrupPoolId);
    }

    @Override
    public String toString() {
        return "SyrupPoolAccountId{" +
                "accountAddress='" + accountAddress + '\'' +
                ", syrupPoolId=" + syrupPoolId +
                '}';
    }
}
