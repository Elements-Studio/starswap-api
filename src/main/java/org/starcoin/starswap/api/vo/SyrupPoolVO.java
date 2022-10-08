package org.starcoin.starswap.api.vo;

import org.springframework.beans.BeanUtils;
import org.starcoin.starswap.api.data.model.SyrupPool;

import java.util.List;

public class SyrupPoolVO extends SyrupPool {
    private List<SyrupMultiplierPoolInfo> syrupMultiplierPools;

    public static SyrupPoolVO toSyrupPoolVO(SyrupPool s, List<SyrupMultiplierPoolInfo> multiplierPools) {
        SyrupPoolVO t = new SyrupPoolVO();
        BeanUtils.copyProperties(s, t);
        t.setSyrupMultiplierPools(multiplierPools);
        return t;
    }

    public List<SyrupMultiplierPoolInfo> getSyrupMultiplierPools() {
        return syrupMultiplierPools;
    }

    public void setSyrupMultiplierPools(List<SyrupMultiplierPoolInfo> syrupMultiplierPools) {
        this.syrupMultiplierPools = syrupMultiplierPools;
    }

    @Override
    public String toString() {
        return "SyrupPoolVO{" +
                "syrupMultiplierPools=" + syrupMultiplierPools +
                '}';
    }
}
