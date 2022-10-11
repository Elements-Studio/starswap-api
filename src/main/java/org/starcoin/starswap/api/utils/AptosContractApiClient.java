package org.starcoin.starswap.api.utils;

import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.SyrupStake;
import org.starcoin.starswap.api.data.model.Triple;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class AptosContractApiClient implements ContractApiClient {
    private final String nodeApiBaseUrl;
    private final String contractAddress;

    public AptosContractApiClient(String nodeApiBaseUrl, String contractAddress) {
        this.nodeApiBaseUrl = nodeApiBaseUrl;
        this.contractAddress = contractAddress;
    }

    @Override
    public BigInteger tokenSwapFarmQueryTotalStake(String farmAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapFarmQueryReleasePerSecond(String farmAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(String farmAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer tokenSwapFarmGetRewardMultiplier(String farmAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long tokenSwapFarmGetBoostFactor(String farmAddress, String tokenX, String tokenY, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger syrupPoolQueryTotalStake(String poolAddress, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger syrupPoolQueryReleasePerSecondV2(String poolAddress, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SyrupStake> syrupPoolQueryStakeList(String poolAddress, String token, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SyrupStake> syrupPoolQueryStakeWithExpectedGainList(String poolAddress, String token, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Triple<List<Long>, List<Long>, List<BigInteger>> syrupPoolQueryAllMultiplierPools(String poolAddress, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getAccountVeStarAmount(String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getAccountVeStarAmountByStakeId(String accountAddress, Long stakeId, String tokenTypeTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccountFarmStakeInfo getAccountFarmStakeInfo(String farmAddress, String lpTokenAddress, String tokenX, String tokenY, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> getTokenSwapFarmStakedReserves(String farmAddress, String lpTokenAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> getReservesByLiquidity(String lpTokenAddress, String tokenX, String tokenY, BigInteger liquidity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapRouterGetTotalLiquidity(String lpTokenAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenGetScalingFactor(String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getExchangeRate(String lpTokenAddress, String tokenX, String tokenY, BigInteger tokenXScalingFactor, BigInteger tokenYScalingFactor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String lpTokenAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapRouterGetAmountOut(String lpTokenAddress, String tokenIn, String tokenOut, BigInteger amountIn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapRouterGetAmountIn(String lpTokenAddress, String tokenIn, String tokenOut, BigInteger amountOut) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<Long, Long> tokenSwapRouterGetPoundageRate(String lpTokenAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<Long, Long> tokenSwapRouterGetSwapFeeOperationRateV2(String lpTokenAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }
}
