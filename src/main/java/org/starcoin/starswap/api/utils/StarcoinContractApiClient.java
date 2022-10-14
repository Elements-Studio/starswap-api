package org.starcoin.starswap.api.utils;


import org.springframework.cache.annotation.Cacheable;
import org.starcoin.bean.Event;
import org.starcoin.bean.RpcStateWithProof;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.SyrupStake;
import org.starcoin.starswap.api.data.model.Triple;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;
import org.starcoin.utils.JsonRpcClientUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StarcoinContractApiClient implements ContractApiClient {

    private final JSONRPC2Session jsonRpcSession;

    private final String jsonRpcUrl;


    private final String contractAddress;

    public StarcoinContractApiClient(String jsonRpcUrl, String contractAddress) throws MalformedURLException {
        this.jsonRpcUrl = jsonRpcUrl;
        this.contractAddress = contractAddress;
        this.jsonRpcSession = new JSONRPC2Session(new URL(this.jsonRpcUrl));

    }

//    protected String getJsonRpcUrl() {
//        return jsonRpcUrl;
//    }

    public Event[] getEvents(Map<String, Object> eventFilter) {
        return JsonRpcClientUtils.getEvents(this.jsonRpcSession, eventFilter);
    }

    public RpcStateWithProof getStateWithProofByRoot(String accessPath, String stateRoot) {
        return JsonRpcClientUtils.getStateWithProofByRoot(this.jsonRpcSession, accessPath, stateRoot);
    }

    @Override
    public BigInteger tokenSwapFarmQueryTotalStake(String farmAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapFarmQueryTotalStake(this.jsonRpcSession, farmAddress, tokenX, tokenY);
    }

    @Override
    public BigInteger tokenSwapFarmQueryReleasePerSecond(String farmAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapFarmQueryReleasePerSecond(this.jsonRpcSession, farmAddress, tokenX, tokenY);
    }

    /**
     * @param farmAddress
     * @param tokenX
     * @param tokenY
     * @return return farm ReleasePerSecond and asset_total_weight which is a "boosted" virtual LP amount.
     */
    @Override
    public Pair<BigInteger, BigInteger> tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(String farmAddress, String tokenX, String tokenY) {
        List<BigInteger> farmInfoV2 = JsonRpcUtils.tokenSwapFarmQueryInfoV2(this.jsonRpcSession, farmAddress, tokenX, tokenY);
        BigInteger alloc_point = farmInfoV2.get(0);
        //BigInteger asset_total_amount = farmInfoV2.get(1);
        BigInteger asset_total_weight = farmInfoV2.get(2);
        Pair<BigInteger, BigInteger> farmGlobalInfo = JsonRpcUtils.tokenSwapFarmQueryGlobalPoolInfo(this.jsonRpcSession, farmAddress);
        BigInteger total_alloc_point = farmGlobalInfo.getItem1();
        BigInteger total_pool_release_per_second = farmGlobalInfo.getItem2();
        BigInteger rps = total_pool_release_per_second.multiply(alloc_point).divide(total_alloc_point);
        return new Pair<>(rps, asset_total_weight);
    }

    @Override
    public Integer tokenSwapFarmGetRewardMultiplier(String farmAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapFarmGetRewardMultiplier(this.jsonRpcSession, farmAddress, tokenX, tokenY);
    }

    @Override
    public Long tokenSwapFarmGetBoostFactor(String farmAddress, String tokenX, String tokenY, String accountAddress) {
        return JsonRpcUtils.tokenSwapFarmGetBoostFactor(this.jsonRpcSession, farmAddress, tokenX, tokenY, accountAddress);
    }

    // ------------------------
    @Override
    public BigInteger syrupPoolQueryTotalStake(String poolAddress, String token) {
        return JsonRpcUtils.syrupPoolQueryTotalStake(this.jsonRpcSession, poolAddress, token);
    }

//    public BigInteger syrupPoolQueryReleasePerSecond(String poolAddress, String token) {
//        return JsonRpcUtils.syrupPoolQueryReleasePerSecond(this.jsonRpcSession, poolAddress, token);
//    }

    @Override
    public BigInteger syrupPoolQueryReleasePerSecondV2(String poolAddress, String token) {
        return JsonRpcUtils.syrupPoolQueryReleasePerSecondV2(this.jsonRpcSession, poolAddress, token);
    }

//    public Integer syrupPoolGetRewardMultiplier(String poolAddress, String token) {
//        return JsonRpcUtils.syrupPoolGetRewardMultiplier(this.jsonRpcSession, poolAddress, token);
//    }

    @Override
    public List<SyrupStake> syrupPoolQueryStakeList(String poolAddress, String token, String accountAddress) {
        List<Long> idList = JsonRpcUtils.syrupPoolQueryStakeList(this.jsonRpcSession, poolAddress, token, accountAddress);
        List<SyrupStake> stakeList = new ArrayList<>();
        for (Long id : idList) {
            SyrupStake stake = JsonRpcUtils.syrupPoolGetStakeInfo(this.jsonRpcSession, poolAddress, token, accountAddress, id);
            stakeList.add(stake);
        }
        return stakeList;
    }

    @Override
    public List<SyrupStake> syrupPoolQueryStakeWithExpectedGainList(String poolAddress, String token, String accountAddress) {
        List<SyrupStake> stakeList = syrupPoolQueryStakeList(poolAddress, token, accountAddress);
        stakeList.forEach(s -> {
            s.setExpectedGain(JsonRpcUtils.syrupPoolQueryExpectedGain(this.jsonRpcSession, poolAddress, token, accountAddress,
                    s.getId()));
        });
        return stakeList;
    }

    @Override
    @Cacheable(cacheNames = "syrupPoolQueryAllMultiplierPoolsCache",
            key = "#poolAddress + ',' + #token", unless = "#result == null")
    public Triple<List<Long>, List<Long>, List<BigInteger>> syrupPoolQueryAllMultiplierPools(String poolAddress,
                                                                                             String token) {
        return JsonRpcUtils.syrupPoolQueryAllMultiplierPools(this.jsonRpcSession, poolAddress, token);
    }


    @Override
    public BigInteger getAccountVestarAmount(String accountAddress) {
        return JsonRpcUtils.getAccountVeStarAmount(this.jsonRpcSession, this.contractAddress, accountAddress);
    }

    @Override
    public BigInteger getAccountVestarAmountByStakeId(String accountAddress, Long stakeId, String tokenTypeTag) {
        return JsonRpcUtils.getAccountVeStarAmountByStakeId(this.jsonRpcSession, this.contractAddress, accountAddress, stakeId, tokenTypeTag);
    }

    // ------------------------

    @Override
    public BigInteger tokenSwapFarmGetAccountStakedLiquidity(String farmAddress, String tokenX, String tokenY, String accountAddress) {
        return JsonRpcUtils.tokenSwapFarmGetAccountStakedLiquidity(this.jsonRpcSession, farmAddress, tokenX, tokenY, accountAddress);
    }

    @Override
    public BigInteger tokenSwapRouterGetTotalLiquidity(String lpTokenAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapRouterTotalLiquidity(this.jsonRpcSession, lpTokenAddress, tokenX, tokenY);
    }

    @Override
    @Cacheable(cacheNames = "tokenScalingFactorCache", key = "#token", unless = "#result == null")
    public BigInteger getTokenScalingFactor(String token) {
        return JsonRpcClientUtils.tokenGetScalingFactor(jsonRpcSession, token);
    }

    @Override
    @Cacheable(cacheNames = "tokenSwapRouterGetReservesCache",
            key = "#lpTokenAddress + ',' + #tokenX + '/' + #tokenY", unless = "#result == null")
    public Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String lpTokenAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapRouterGetReserves(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
    }

    @Override
    public BigInteger tokenSwapRouterGetAmountOut(String lpTokenAddress, BigInteger reserveIn, BigInteger reserveOut,
                                                  BigInteger amountIn,
                                                  long swapFeeNumerator, long swapFeeDenumerator) {
        return JsonRpcUtils.tokenSwapRouterGetAmountOut(jsonRpcSession, lpTokenAddress, amountIn, reserveIn, reserveOut, swapFeeNumerator, swapFeeDenumerator);
    }

    @Override
    public BigInteger tokenSwapRouterGetAmountIn(String lpTokenAddress, String tokenIn, String tokenOut,
                                                 BigInteger amountOut,
                                                 long swapFeeNumerator, long swapFeeDenumerator) {
        Pair<BigInteger, BigInteger> reserves = JsonRpcUtils.tokenSwapRouterGetReserves(jsonRpcSession, lpTokenAddress, tokenIn, tokenOut);
        return JsonRpcUtils.tokenSwapRouterGetAmountIn(jsonRpcSession, lpTokenAddress, amountOut, reserves.getItem1(), reserves.getItem2(), swapFeeNumerator, swapFeeDenumerator);
    }

    @Override
    public Pair<Long, Long> tokenSwapRouterGetPoundageRate(String lpTokenAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapRouterGetPoundageRate(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
    }

    @Override
    public Pair<Long, Long> tokenSwapRouterGetSwapFeeOperationRateV2(String lpTokenAddress, String tokenX, String tokenY) {
        return JsonRpcUtils.tokenSwapRouterGetSwapFeeOperationRateV2(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
    }

//    public List<Object> tokenSwapOracleLibraryCurrentCumulativePrices(String lpTokenAddress, String tokenX, String tokenY) {
//        return JsonRpcUtils.tokenSwapOracleLibraryCurrentCumulativePrices(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
//    }

}
