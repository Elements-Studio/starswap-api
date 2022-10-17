package org.starcoin.starswap.api.service;

import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;
import org.starcoin.starswap.api.vo.SyrupMultiplierPoolInfo;
import org.starcoin.starswap.api.vo.SyrupStakeVO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface OnChainService {
    /**
     * A special token id,
     * when combined with another token id to form a pair,
     * means that the token's exchange rate to USD needs to be obtained through the off-chain API.
     */
    String TOKEN_ID_OFF_CHAIN_USD = "OFF_CHAIN_USD";
    int MAX_SWAP_DEPTH = 3;

    AccountFarmStakeInfo getAccountFarmStakeInfo(String tokenXId, String tokenYId, String accountAddress);

    Long getAccountFarmBoostFactor(String tokenXId, String tokenYId, String accountAddress);

    Pair<List<String>, BigInteger> getBestSwapPathAndAmountOut(String tokenInId, String tokenOutId, BigInteger amountIn);

    Pair<List<String>, BigInteger> getBestSwapPathAndAmountIn(String tokenInId, String tokenOutId, BigInteger amountOut);

    BigInteger getFarmTotalStakeAmount(LiquidityTokenFarm farm);

    List<BigInteger[]> getReservesListByTokenTypeTagPairs(String[][] tokenTypeTagPairs);

    Long getFarmRewardMultiplier(LiquidityTokenFarm farm);

    BigDecimal getFarmTvlInUsd(LiquidityTokenFarm liquidityTokenFarm);

    BigDecimal getFarmEstimatedApyV2(LiquidityTokenFarm liquidityTokenFarm, BigDecimal tvlInUsd);

    BigInteger getFarmDailyRewardV2(LiquidityTokenFarm liquidityTokenFarm);

    BigDecimal getTokenAmountInUsd(Token token, BigInteger tokenAmount);

    BigDecimal getTokenAmountInUsdOffChainFirst(Token token, BigInteger tokenAmount);

    //BigInteger getTokenScalingFactor(String typeTag);

    BigDecimal getToUsdExchangeRate(String tokenTypeTag);

    BigDecimal getExchangeRate(String tokenTypeTag, String toTokenTypeTag);

    Map<String, BigDecimal> getAnyToUsdExchangeRateOffChainFirst(List<String> tokenTypeTags);

    BigDecimal getToUsdExchangeRateOffChainFirst(String tokenTypeTag);

    BigDecimal getToUsdExchangeRateByTokenId(String tokenId);

    void refreshOffChainScalingFactors();

    BigInteger getSyrupPoolTotalStakeAmount(SyrupPool pool);

    BigDecimal getSyrupPoolTvlInUsd(SyrupPool pool);

    BigDecimal getSyrupPoolEstimatedApy(SyrupPool pool, BigDecimal tvlInUsd);

    BigInteger getSyrupPoolDailyReward(SyrupPool pool);

    List<SyrupMultiplierPoolInfo> getSyrupMultiplierPools(SyrupPool syrupPool, Boolean estimateApr);

    List<SyrupStakeVO> getSyrupPoolStakeList(SyrupPool syrupPool, String accountAddress);

    BigInteger getAccountVestarAmount(String accountAddress);

    BigInteger getAccountVestarAmountByStakeId(String accountAddress, Long stakeId, String tokenTypeTag);
}
