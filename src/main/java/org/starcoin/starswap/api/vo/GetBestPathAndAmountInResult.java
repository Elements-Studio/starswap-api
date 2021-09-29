package org.starcoin.starswap.api.vo;

import java.math.BigInteger;
import java.util.List;

public class GetBestPathAndAmountInResult {
    private List<String> path;
    private BigInteger amountIn;

    public GetBestPathAndAmountInResult() {
    }

    public GetBestPathAndAmountInResult(List<String> path, BigInteger amountIn) {
        this.path = path;
        this.amountIn = amountIn;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public BigInteger getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(BigInteger amountIn) {
        this.amountIn = amountIn;
    }

    @Override
    public String toString() {
        return "GetBestPathResult{" +
                "path=" + path +
                ", amountOut=" + amountIn +
                '}';
    }
}
