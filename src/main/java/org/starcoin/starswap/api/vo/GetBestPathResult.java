package org.starcoin.starswap.api.vo;

import java.math.BigInteger;
import java.util.List;

public class GetBestPathResult {
    private List<String> path;
    private BigInteger amountOut;

    public GetBestPathResult() {
    }

    public GetBestPathResult(List<String> path, BigInteger amountOut) {
        this.path = path;
        this.amountOut = amountOut;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public BigInteger getAmountOut() {
        return amountOut;
    }

    public void setAmountOut(BigInteger amountOut) {
        this.amountOut = amountOut;
    }

    @Override
    public String toString() {
        return "GetBestPathResult{" +
                "path=" + path +
                ", amountOut=" + amountOut +
                '}';
    }
}
