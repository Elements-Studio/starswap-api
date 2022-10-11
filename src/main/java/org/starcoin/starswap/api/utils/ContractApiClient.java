package org.starcoin.starswap.api.utils;

import org.springframework.cache.annotation.Cacheable;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.SyrupStake;
import org.starcoin.starswap.api.data.model.Triple;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface ContractApiClient {
    BigInteger tokenSwapFarmQueryTotalStake(String farmAddress, String tokenX, String tokenY);

    BigInteger tokenSwapFarmQueryReleasePerSecond(String farmAddress, String tokenX, String tokenY);

    Pair<BigInteger, BigInteger> tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(String farmAddress, String tokenX, String tokenY);

    Integer tokenSwapFarmGetRewardMultiplier(String farmAddress, String tokenX, String tokenY);

    Long tokenSwapFarmGetBoostFactor(String farmAddress, String tokenX, String tokenY, String accountAddress);

    // ------------------------
    BigInteger syrupPoolQueryTotalStake(String poolAddress, String token);

    BigInteger syrupPoolQueryReleasePerSecondV2(String poolAddress, String token);

    List<SyrupStake> syrupPoolQueryStakeList(String poolAddress, String token, String accountAddress);

    List<SyrupStake> syrupPoolQueryStakeWithExpectedGainList(String poolAddress, String token, String accountAddress);

    @Cacheable(cacheNames = "syrupPoolQueryAllMultiplierPoolsCache",
            key = "#poolAddress + ',' + #token", unless = "#result == null")
    Triple<List<Long>, List<Long>, List<BigInteger>> syrupPoolQueryAllMultiplierPools(String poolAddress,
                                                                                      String token);

    BigInteger getAccountVeStarAmount(String accountAddress);

    BigInteger getAccountVeStarAmountByStakeId(String accountAddress, Long stakeId, String tokenTypeTag);

    AccountFarmStakeInfo getAccountFarmStakeInfo(String farmAddress, String lpTokenAddress, String tokenX, String tokenY, String accountAddress);

    Pair<BigInteger, BigInteger> getTokenSwapFarmStakedReserves(String farmAddress, String lpTokenAddress, String tokenX, String tokenY);

    Pair<BigInteger, BigInteger> getReservesByLiquidity(String lpTokenAddress, String tokenX, String tokenY, BigInteger liquidity);

    BigInteger tokenSwapRouterGetTotalLiquidity(String lpTokenAddress, String tokenX, String tokenY);

    @Cacheable(cacheNames = "tokenScalingFactorCache", key = "#token", unless = "#result == null")
    BigInteger tokenGetScalingFactor(String token);

    @Cacheable(cacheNames = "tokenExchangeRateCache",
            key = "#lpTokenAddress + ',' + #tokenX + '/' + #tokenY + ',' + #tokenXScalingFactor + '/' + #tokenYScalingFactor", unless = "#result == null")
    BigDecimal getExchangeRate(String lpTokenAddress, String tokenX, String tokenY,
                               BigInteger tokenXScalingFactor, BigInteger tokenYScalingFactor);

    @Cacheable(cacheNames = "tokenSwapRouterGetReservesCache",
            key = "#lpTokenAddress + ',' + #tokenX + '/' + #tokenY", unless = "#result == null")
    Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String lpTokenAddress, String tokenX, String tokenY);

    BigInteger tokenSwapRouterGetAmountOut(String lpTokenAddress, String tokenIn, String tokenOut, BigInteger amountIn);

    BigInteger tokenSwapRouterGetAmountIn(String lpTokenAddress, String tokenIn, String tokenOut, BigInteger amountOut);

    Pair<Long, Long> tokenSwapRouterGetPoundageRate(String lpTokenAddress, String tokenX, String tokenY);

    Pair<Long, Long> tokenSwapRouterGetSwapFeeOperationRateV2(String lpTokenAddress, String tokenX, String tokenY);
}
