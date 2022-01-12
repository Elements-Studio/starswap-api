package org.starcoin.starswap.api.utils;


import org.springframework.cache.annotation.Cacheable;
import org.starcoin.bean.Event;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.starswap.api.data.model.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class JsonRpcClient {

    private final JSONRPC2Session jsonRpcSession;

    private final String jsonRpcUrl;

    public JsonRpcClient(String jsonRpcUrl) throws MalformedURLException {
        this.jsonRpcUrl = jsonRpcUrl;
        this.jsonRpcSession = new JSONRPC2Session(new URL(this.jsonRpcUrl));

    }

    public String getJsonRpcUrl() {
        return jsonRpcUrl;
    }

    public Event[] getEvents(Map<String, Object> eventFilter) {
        return JsonRpcUtils.getEvents(this.jsonRpcSession, eventFilter);
    }

    public BigInteger tokenSwapFarmQueryTotalStake(String farmAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapFarmQueryTotalStake(this.jsonRpcSession, farmAddress, tokenX, tokenY);
    }

    public BigInteger tokenSwapFarmQueryReleasePerSecond(String farmAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapFarmQueryReleasePerSecond(this.jsonRpcSession, farmAddress, tokenX, tokenY);
    }

    public Pair<BigInteger, BigInteger> getTokenSwapFarmStakedReserves(String farmAddress, String lpTokenAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.getTokenSwapFarmStakedReserves(this.jsonRpcSession, farmAddress, lpTokenAddress, tokenX, tokenY);
    }

    @Cacheable(cacheNames = "tokenScalingFactorCache", key = "#token", unless = "#result == null")
    public BigInteger tokenGetScalingFactor(String token) {
        return JsonRpcUtils.tokenGetScalingFactor(jsonRpcSession, token);
    }

    public BigDecimal getExchangeRate(String lpTokenAddress, String tokenX, String tokenY) {
        BigInteger tokenXScalingFactor = JsonRpcUtils.tokenGetScalingFactor(jsonRpcSession, tokenX);
        BigInteger tokenYScalingFactor = JsonRpcUtils.tokenGetScalingFactor(jsonRpcSession, tokenY);
        return getExchangeRate(lpTokenAddress, tokenX, tokenY, tokenXScalingFactor, tokenYScalingFactor);
    }

    @Cacheable(cacheNames = "tokenExchangeRateCache",
            key = "#lpTokenAddress + ',' + #tokenX + '/' + #tokenY + ',' + #tokenXScalingFactor + '/' + #tokenYScalingFactor", unless = "#result == null")
    public BigDecimal getExchangeRate(String lpTokenAddress, String tokenX, String tokenY,
                                      BigInteger tokenXScalingFactor, BigInteger tokenYScalingFactor) {
        Pair<BigInteger, BigInteger> reserves = JsonRpcUtils.tokenSwapRouterGetReserves(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
        BigInteger amountX = reserves.getItem1().divide(BigInteger.valueOf(100L));//try to swap tokenX of 1 percent reserve
        BigInteger amountY = JsonRpcUtils.tokenSwapRouterGetAmountOut(jsonRpcSession, lpTokenAddress, amountX, reserves.getItem1(), reserves.getItem2());
        //System.out.println("tokenX:" + tokenX + ", ----- amountX ------" + amountX);
        //System.out.println("tokenY:" + tokenY + ", ----- amountY ------" + amountY);
        int scale = Math.max(tokenXScalingFactor.toString().length(), tokenYScalingFactor.toString().length()) - 1;
        return new BigDecimal(amountY).divide(new BigDecimal(tokenYScalingFactor), scale, RoundingMode.HALF_UP)
                .divide(new BigDecimal(amountX).divide(new BigDecimal(tokenXScalingFactor), scale, RoundingMode.HALF_UP),
                        scale, RoundingMode.HALF_UP);
    }

    public Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String lpTokenAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapRouterGetReserves(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
    }

    public BigInteger tokenSwapRouterGetAmountOut(String lpTokenAddress, String tokenIn, String tokenOut, BigInteger amountIn) {
        Pair<BigInteger, BigInteger> reserves = JsonRpcUtils.tokenSwapRouterGetReserves(jsonRpcSession, lpTokenAddress, tokenIn, tokenOut);
        return JsonRpcUtils.tokenSwapRouterGetAmountOut(jsonRpcSession, lpTokenAddress, amountIn, reserves.getItem1(), reserves.getItem2());
    }

    public BigInteger tokenSwapRouterGetAmountIn(String lpTokenAddress, String tokenIn, String tokenOut, BigInteger amountOut) {
        Pair<BigInteger, BigInteger> reserves = JsonRpcUtils.tokenSwapRouterGetReserves(jsonRpcSession, lpTokenAddress, tokenIn, tokenOut);
        return JsonRpcUtils.tokenSwapRouterGetAmountIn(jsonRpcSession, lpTokenAddress, amountOut, reserves.getItem1(), reserves.getItem2());
    }
}
