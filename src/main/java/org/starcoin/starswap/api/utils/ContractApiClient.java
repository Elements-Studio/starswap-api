package org.starcoin.starswap.api.utils;

import org.springframework.cache.annotation.Cacheable;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.vo.SyrupStakeVO;
import org.starcoin.starswap.api.data.model.Triple;
import org.starcoin.starswap.api.service.OnChainServiceImpl;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

public interface ContractApiClient {
    BigInteger tokenSwapFarmQueryTotalStake(String farmAddress, String tokenX, String tokenY);

    BigInteger tokenSwapFarmQueryReleasePerSecondV2(String farmAddress, String tokenX, String tokenY);

    Pair<BigInteger, BigInteger> tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(String farmAddress, String tokenX, String tokenY);

    Long tokenSwapFarmGetRewardMultiplier(String farmAddress, String tokenX, String tokenY);

    Long tokenSwapFarmGetBoostFactor(String farmAddress, String tokenX, String tokenY, String accountAddress);

    // ------------------------
    BigInteger syrupPoolQueryTotalStake(String poolAddress, String token);

    BigInteger syrupPoolQueryReleasePerSecondV2(String poolAddress, String token);

    List<SyrupStakeVO> syrupPoolQueryStakeList(String poolAddress, String token, String accountAddress);

    List<SyrupStakeVO> syrupPoolQueryStakeWithExpectedGainList(String poolAddress, String token, String accountAddress);

    @Cacheable(cacheNames = "syrupPoolQueryAllMultiplierPoolsCache",
            key = "#poolAddress + ',' + #token", unless = "#result == null")
    Triple<List<Long>, List<Long>, List<BigInteger>> syrupPoolQueryAllMultiplierPools(String poolAddress,
                                                                                      String token);

    BigInteger getAccountVestarAmount(String accountAddress);

    BigInteger getVestarAmountByTokenTypeAndStakeId(String accountAddress, String token, Long stakeId);

    default Pair<BigInteger, BigInteger> getTokenSwapFarmStakedReserves(String farmAddress, String lpTokenAddress, String tokenX, String tokenY) {
        BigInteger stakedLiquidity = tokenSwapFarmQueryTotalStake(farmAddress, tokenX, tokenY);
        return getReservesByLiquidity(lpTokenAddress, tokenX, tokenY, stakedLiquidity);
    }

    default Pair<BigInteger, BigInteger> getReservesByLiquidity(String lpTokenAddress, String tokenX, String tokenY, BigInteger liquidity) {
        //     return JsonRpcUtils.getReservesByLiquidity(this.jsonRpcSession, lpTokenAddress, tokenX, tokenY, liquidity);
        Pair<BigInteger, BigInteger> totalReservesPair = tokenSwapRouterGetReserves(lpTokenAddress, tokenX, tokenY);
        //System.out.println("totalReservesPair: " + totalReservesPair);
        BigInteger totalLiquidity = tokenSwapRouterGetTotalLiquidity(lpTokenAddress, tokenX, tokenY);
        //System.out.println("totalLiquidity: " + totalLiquidity);
        return new Pair<>(
                totalReservesPair.getItem1().multiply(liquidity).divide(totalLiquidity),
                totalReservesPair.getItem2().multiply(liquidity).divide(totalLiquidity)
        );
    }

    default AccountFarmStakeInfo getAccountFarmStakeInfo(String farmAddress, String lpTokenAddress, String tokenX, String tokenY, String accountAddress) {
        BigInteger stakedLiquidity = tokenSwapFarmGetAccountStakedLiquidity(farmAddress, tokenX, tokenY, accountAddress);
        BigInteger farmTotalLiquidity = tokenSwapFarmQueryTotalStake(farmAddress, tokenX, tokenY);
        BigDecimal sharePercentage = new BigDecimal(stakedLiquidity.multiply(BigInteger.valueOf(100))).divide(new BigDecimal(farmTotalLiquidity), 9, RoundingMode.HALF_UP);
        Pair<BigInteger, BigInteger> stakedAmountPair = getReservesByLiquidity(lpTokenAddress, tokenX, tokenY, stakedLiquidity);

        AccountFarmStakeInfo farmStakeInfo = new AccountFarmStakeInfo();
        farmStakeInfo.setStakedLiquidity(stakedLiquidity);
        farmStakeInfo.setFarmTotalLiquidity(farmTotalLiquidity);
        farmStakeInfo.setSharePercentage(sharePercentage);
        farmStakeInfo.setTokenXAmount(new AccountFarmStakeInfo.TokenAmount(null, tokenX, stakedAmountPair.getItem1()));
        farmStakeInfo.setTokenYAmount(new AccountFarmStakeInfo.TokenAmount(null, tokenY, stakedAmountPair.getItem2()));
        //farmStakeInfo.setStakedAmountInUsd();
        return farmStakeInfo;
    }

    BigInteger tokenSwapFarmGetAccountStakedLiquidity(String farmAddress, String tokenX, String tokenY, String accountAddress);

    BigInteger tokenSwapRouterGetTotalLiquidity(String lpTokenAddress, String tokenX, String tokenY);

    @Cacheable(cacheNames = "tokenScalingFactorCache", key = "#token", unless = "#result == null")
    BigInteger getTokenScalingFactor(String token);


    @Cacheable(cacheNames = "tokenExchangeRateCache",
            key = "#lpTokenAddress + ',' + #tokenX + '/' + #tokenY + ',' + #tokenXScalingFactor + '/' + #tokenYScalingFactor", unless = "#result == null")
    default BigDecimal getExchangeRate(String lpTokenAddress, String tokenX, String tokenY,
                                       BigInteger tokenXScalingFactor, BigInteger tokenYScalingFactor) {
        // use tokenSwapRouterGetReserves and tokenSwapRouterGetAmountOut to calculate exchange rate:
        Pair<BigInteger, BigInteger> reserves = tokenSwapRouterGetReserves(lpTokenAddress, tokenX, tokenY);
        BigInteger amountX = reserves.getItem1().divide(BigInteger.valueOf(100L));//try to swap tokenX of 1 percent reserve
        BigInteger amountY = tokenSwapRouterGetAmountOut(lpTokenAddress,
                reserves.getItem1(), reserves.getItem2(), amountX,
                OnChainServiceImpl.DEFAULT_SWAP_FEE_NUMERATOR,
                OnChainServiceImpl.DEFAULT_SWAP_FEE_DENUMERATOR);
//        System.out.println("----- tokenX: " + tokenX + ", ----- amountX: " + amountX);
//        System.out.println("----- tokenY: " + tokenY + ", ----- amountY: " + amountY);
        int scale = Math.max(tokenXScalingFactor.toString().length(), tokenYScalingFactor.toString().length()) - 1;
        return new BigDecimal(amountY).divide(new BigDecimal(tokenYScalingFactor), scale, RoundingMode.HALF_UP)
                .divide(new BigDecimal(amountX).divide(new BigDecimal(tokenXScalingFactor), scale, RoundingMode.HALF_UP),
                        scale, RoundingMode.HALF_UP);

//        // use another method?
//        List<Object> results = JsonRpcUtils.tokenSwapOracleLibraryCurrentCumulativePrices(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
//        BigInteger cumulativePriceX = new BigInteger(results.get(0).toString());
//        BigInteger cumulativePriceY = new BigInteger(results.get(1).toString());
    }

    //    public BigDecimal getExchangeRate(String lpTokenAddress, String tokenX, String tokenY) {
//        BigInteger tokenXScalingFactor = JsonRpcClientUtils.tokenGetScalingFactor(jsonRpcSession, tokenX);
//        BigInteger tokenYScalingFactor = JsonRpcClientUtils.tokenGetScalingFactor(jsonRpcSession, tokenY);
//        return getExchangeRate(lpTokenAddress, tokenX, tokenY, tokenXScalingFactor, tokenYScalingFactor);
//    }

    //todo Is contract address equals LPToken address?
    @Cacheable(cacheNames = "tokenSwapRouterGetReservesCache",
            key = "#lpTokenAddress + ',' + #tokenX + '/' + #tokenY", unless = "#result == null")
    Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String lpTokenAddress, String tokenX, String tokenY);

    default BigInteger tokenSwapRouterGetAmountOut(String lpTokenAddress, String tokenIn, String tokenOut,
                                                   BigInteger amountIn,
                                                   long swapFeeNumerator, long swapFeeDenumerator) {
        Pair<BigInteger, BigInteger> reserves = tokenSwapRouterGetReserves(lpTokenAddress, tokenIn, tokenOut);
        return tokenSwapRouterGetAmountOut(lpTokenAddress, reserves.getItem1(), reserves.getItem2(), amountIn,
                swapFeeNumerator, swapFeeDenumerator);
    }

    BigInteger tokenSwapRouterGetAmountOut(String lpTokenAddress, BigInteger reserveIn, BigInteger reserveOut,
                                           BigInteger amountIn,
                                           long swapFeeNumerator, long swapFeeDenumerator);

    BigInteger tokenSwapRouterGetAmountIn(String lpTokenAddress, String tokenIn, String tokenOut, BigInteger amountOut,
                                          long swapFeeNumerator, long swapFeeDenumerator);

    Pair<Long, Long> tokenSwapRouterGetPoundageRate(String lpTokenAddress, String tokenX, String tokenY);

    Pair<Long, Long> tokenSwapRouterGetSwapFeeOperationRateV2(String lpTokenAddress, String tokenX, String tokenY);
}
