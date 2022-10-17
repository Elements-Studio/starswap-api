package org.starcoin.starswap.api.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.api.utils.ContractApiClient;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;
import org.starcoin.starswap.api.vo.SyrupMultiplierPoolInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;

@Service
public class OnChainServiceImpl implements OnChainService {

    public static final long DEFAULT_SWAP_FEE_NUMERATOR = 3; //u64
    public static final long DEFAULT_SWAP_FEE_DENUMERATOR = 1000;//u64

    private static final long ONE_YEAR_SECONDS = 60L * 60 * 24 * 365;

    private static final long ONE_DAY_SECONDS = 60L * 60 * 24;

    //private static final long SYRUP_POOL_TOTAL_REWARD_MULTIPLIER = 2 + 3 + 4 + 6 + 8;

    private static final Logger LOG = LoggerFactory.getLogger(OnChainServiceImpl.class);

//    private static final boolean USE_FARM_AVERAGE_APY = false;

    private final ContractApiClient contractApiClient;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LiquidityTokenService liquidityTokenService;

    @Autowired
    private LiquidityTokenFarmService liquidityTokenFarmService;

    /**
     * Off-chain token price service.
     */
    @Autowired
    private TokenPriceService tokenPriceService;

    @Autowired
    public OnChainServiceImpl(ContractApiClient contractApiClient) {
        this.contractApiClient = contractApiClient;
    }


    @Override
    public AccountFarmStakeInfo getAccountFarmStakeInfo(String tokenXId, String tokenYId, String accountAddress) {
        TokenIdPair tokenIdPair = new TokenIdPair(tokenXId, tokenYId);
        Token tokenX = tokenService.getTokenOrElseThrow(tokenIdPair.tokenXId(), () -> new RuntimeException("Cannot find Token by Id: " + tokenIdPair.tokenXId()));
        Token tokenY = tokenService.getTokenOrElseThrow(tokenIdPair.tokenYId(), () -> new RuntimeException("Cannot find Token by Id: " + tokenIdPair.tokenYId()));
        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenX.getTokenId(), tokenY.getTokenId());

        AccountFarmStakeInfo farmStakeInfo = this.contractApiClient.getAccountFarmStakeInfo(liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress(),
                liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getLiquidityTokenAddress(),
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString(),
                accountAddress);
        farmStakeInfo.getTokenXAmount().setTokenId(tokenIdPair.tokenXId());
        farmStakeInfo.getTokenYAmount().setTokenId(tokenIdPair.tokenYId());

        //        BigDecimal farmTvlInUsd = getFarmTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
        //        BigDecimal stakedAmountInUsd = farmTvlInUsd.multiply(farmStakeInfo.getSharePercentage()).divide(BigDecimal.valueOf(100L), 9, RoundingMode.HALF_UP);
        BigDecimal tokenXAmountInUsd = getTokenAmountInUsdOffChainFirst(tokenX, farmStakeInfo.getTokenXAmount().getAmount());
        BigDecimal tokenYAmountInUsd = getTokenAmountInUsdOffChainFirst(tokenY, farmStakeInfo.getTokenYAmount().getAmount());
        BigDecimal stakedAmountInUsd = (tokenXAmountInUsd.compareTo(tokenYAmountInUsd) > 0
                ? tokenXAmountInUsd
                : tokenYAmountInUsd).multiply(BigDecimal.valueOf(2));
        farmStakeInfo.setStakedAmountInUsd(stakedAmountInUsd.round(new MathContext(9, RoundingMode.HALF_UP)));

        if (!tokenXId.equals(tokenIdPair.tokenXId())) {
            AccountFarmStakeInfo.TokenAmount t = farmStakeInfo.getTokenXAmount();
            farmStakeInfo.setTokenXAmount(farmStakeInfo.getTokenYAmount());
            farmStakeInfo.setTokenYAmount(t);
        }
        return farmStakeInfo;
    }

    @Override
    public Long getAccountFarmBoostFactor(String tokenXId, String tokenYId, String accountAddress) {
        TokenIdPair tokenIdPair = new TokenIdPair(tokenXId, tokenYId);
        Token tokenX = tokenService.getTokenOrElseThrow(tokenIdPair.tokenXId(), () -> new RuntimeException("Cannot find Token by Id: " + tokenIdPair.tokenXId()));
        Token tokenY = tokenService.getTokenOrElseThrow(tokenIdPair.tokenYId(), () -> new RuntimeException("Cannot find Token by Id: " + tokenIdPair.tokenYId()));
        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenX.getTokenId(), tokenY.getTokenId());
        return this.contractApiClient.tokenSwapFarmGetBoostFactor(liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress(),
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString(),
                accountAddress);
    }

    @Override
    public Pair<List<String>, BigInteger> getBestSwapPathAndAmountOut(String tokenInId, String tokenOutId, BigInteger amountIn) {
        Pair<Token, Token> tokenPair = getTokenPairByIds(tokenInId, tokenOutId);
        Token tokenIn = tokenPair.getItem1(), tokenOut = tokenPair.getItem2();
        LiquidityToken directLiquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenInId, tokenOutId);
        List<String> indirectSwapPath = liquidityTokenService.getShortestIndirectSwapPath(tokenInId, tokenOutId);
        BigInteger directAmountOut = null;
        if (directLiquidityToken != null) {
            // todo call On-Chain contract twice??
            directAmountOut = contractApiClient.tokenSwapRouterGetAmountOut(
                    directLiquidityToken.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(), tokenOut.getTokenStructType().toTypeTagString(),
                    amountIn, DEFAULT_SWAP_FEE_NUMERATOR, DEFAULT_SWAP_FEE_DENUMERATOR);
            if (isInvalidSwapPath(indirectSwapPath)) {
                return new Pair<>(Arrays.asList(tokenInId, tokenOutId), directAmountOut);
            }
        }
        if (directLiquidityToken == null && (isInvalidSwapPath(indirectSwapPath))) {
            return new Pair<>(Collections.emptyList(), null);
        }
        List<Token> pathTokens = getPathTokens(tokenIn, tokenOut, indirectSwapPath);
        BigInteger indirectAmountOut = getSwapAmountOut(pathTokens, amountIn);
        if (directLiquidityToken == null) {
            return new Pair<>(indirectSwapPath, indirectAmountOut);
        }
        return directAmountOut.compareTo(indirectAmountOut) >= 0
                ? new Pair<>(Arrays.asList(tokenInId, tokenOutId), directAmountOut)
                : new Pair<>(indirectSwapPath, indirectAmountOut);
    }

    @Override
    public Pair<List<String>, BigInteger> getBestSwapPathAndAmountIn(String tokenInId, String tokenOutId, BigInteger amountOut) {
        Pair<Token, Token> tokenPair = getTokenPairByIds(tokenInId, tokenOutId);
        Token tokenIn = tokenPair.getItem1(), tokenOut = tokenPair.getItem2();
        LiquidityToken directLiquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenInId, tokenOutId);
        List<String> indirectSwapPath = liquidityTokenService.getShortestIndirectSwapPath(tokenInId, tokenOutId);
        BigInteger directAmountIn = null;
        if (directLiquidityToken != null) {
            // todo call On-Chain contract twice??
            directAmountIn = contractApiClient.tokenSwapRouterGetAmountIn(
                    directLiquidityToken.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(), tokenOut.getTokenStructType().toTypeTagString(),
                    amountOut, DEFAULT_SWAP_FEE_NUMERATOR, DEFAULT_SWAP_FEE_DENUMERATOR);
            if (isInvalidSwapPath(indirectSwapPath)) {
                return new Pair<>(Arrays.asList(tokenInId, tokenOutId), directAmountIn);
            }
        }
        if (directLiquidityToken == null && (isInvalidSwapPath(indirectSwapPath))) {
            return new Pair<>(Collections.emptyList(), null);
        }
        List<Token> pathTokens = getPathTokens(tokenIn, tokenOut, indirectSwapPath);
        BigInteger indirectAmountIn = getSwapAmountIn(pathTokens, amountOut);
        if (directLiquidityToken == null) {
            return new Pair<>(indirectSwapPath, indirectAmountIn);
        }
        return directAmountIn.compareTo(indirectAmountIn) <= 0
                ? new Pair<>(Arrays.asList(tokenInId, tokenOutId), directAmountIn)
                : new Pair<>(indirectSwapPath, indirectAmountIn);
    }

    private boolean isInvalidSwapPath(List<String> indirectSwapPath) {
        return indirectSwapPath.size() == 0 || indirectSwapPath.size() > MAX_SWAP_DEPTH + 1;
    }

    private Pair<Token, Token> getTokenPairByIds(String tokenXId, String tokenYId) {
        Token tokenX = tokenService.getTokenOrElseThrow(tokenXId, () -> {
            throw new RuntimeException("Cannot find token by Id: " + tokenXId);
        });
        Token tokenY = tokenService.getTokenOrElseThrow(tokenYId, () -> {
            throw new RuntimeException("Cannot find token by Id: " + tokenYId);
        });
        return new Pair<>(tokenX, tokenY);
    }

    private List<Token> getPathTokens(Token tokenX, Token tokenY, List<String> indirectSwapPath) {
        if (!indirectSwapPath.get(0).equals(tokenX.getTokenId()) || !indirectSwapPath.get(indirectSwapPath.size() - 1).equals(tokenY.getTokenId())) {
            throw new RuntimeException("Unexpected error, get wrong path for token pair: " + tokenX.getTokenId() + " / " + tokenY.getTokenId());
        }
        List<Token> pathTokens = new ArrayList<>(indirectSwapPath.size());
        pathTokens.add(tokenX);
        for (int i = 1; i < indirectSwapPath.size() - 1; i++) {
            final String tokenRId = indirectSwapPath.get(i);
            Token tokenR = tokenService.getTokenOrElseThrow(tokenRId, () -> {
                throw new RuntimeException("Cannot find token by Id: " + tokenRId);
            });
            pathTokens.add(tokenR);
        }
        pathTokens.add(tokenY);
        return pathTokens;
    }

    private BigInteger getSwapAmountOut(List<Token> pathTokens, BigInteger amountIn) {
        BigInteger amountOut = null;
        for (int i = 0; i < pathTokens.size() - 1; i++) {
            Token tokenIn = pathTokens.get(i);
            Token tokenOut = pathTokens.get(i + 1);
            LiquidityToken liquidityTokenInOut = liquidityTokenService.findOneByTokenIdPairOrElseThrow(tokenIn.getTokenId(), tokenOut.getTokenId());
            BigInteger currentAmountIn = (amountOut == null ? amountIn : amountOut);
            // todo call On-Chain contract twice??
            amountOut = contractApiClient.tokenSwapRouterGetAmountOut(liquidityTokenInOut.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(),
                    tokenOut.getTokenStructType().toTypeTagString(),
                    currentAmountIn, DEFAULT_SWAP_FEE_NUMERATOR, DEFAULT_SWAP_FEE_DENUMERATOR);
        }
        if (amountOut == null) {
            throw new IllegalArgumentException("Path error. Size of path: " + pathTokens.size());
        }
        return amountOut;
    }

    private BigInteger getSwapAmountIn(List<Token> pathTokens, BigInteger amountOut) {
        BigInteger amountIn = null;
        for (int i = pathTokens.size() - 1; i >= 1; i--) {
            Token tokenOut = pathTokens.get(i);
            Token tokenIn = pathTokens.get(i - 1);
            LiquidityToken liquidityTokenInOut = liquidityTokenService.findOneByTokenIdPairOrElseThrow(tokenIn.getTokenId(), tokenOut.getTokenId());
            BigInteger currentAmountOut = (amountIn == null ? amountOut : amountIn);
            // todo call On-Chain contract twice??
            amountIn = contractApiClient.tokenSwapRouterGetAmountIn(liquidityTokenInOut.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(),
                    tokenOut.getTokenStructType().toTypeTagString(),
                    currentAmountOut, DEFAULT_SWAP_FEE_NUMERATOR, DEFAULT_SWAP_FEE_DENUMERATOR);
        }
        if (amountIn == null) {
            throw new IllegalArgumentException("Path error. Size of path: " + pathTokens.size());
        }
        return amountIn;
    }

    @Override
    public BigInteger getFarmTotalStakeAmount(LiquidityTokenFarm farm) {
        String farmAddress = farm.getLiquidityTokenFarmId().getFarmAddress();
        String tokenXId = farm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId();
        String tokenX = tokenService.getTokenOrElseThrow(tokenXId, () -> {
            throw new RuntimeException("Cannot find token by Id: " + tokenXId);
        }).getTokenStructType().toTypeTagString();
        String tokenYId = farm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId();
        String tokenY = tokenService.getTokenOrElseThrow(tokenYId, () -> {
            throw new RuntimeException("Cannot find token by Id: " + tokenYId);
        }).getTokenStructType().toTypeTagString();
        BigInteger stakeAmount = contractApiClient.tokenSwapFarmQueryTotalStake(farmAddress, tokenX, tokenY);
        return stakeAmount;
    }

//    public Pair<BigInteger, BigInteger> getFarmStakedReservesByTokenIdPair(String tokenXId, String tokenYId) {
//        //LiquidityToken liquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenXId, tokenYId);
//        Token tokenX = tokenService.getToken(tokenXId);
//        Token tokenY = tokenService.getToken(tokenYId);
//        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenXId, tokenYId);
//        LiquidityTokenFarmId liquidityTokenFarmId = liquidityTokenFarm.getLiquidityTokenFarmId();
//        return jsonRpcClient.getTokenSwapFarmStakedReserves(liquidityTokenFarmId.getFarmAddress(),
//                liquidityTokenFarmId.getLiquidityTokenId().getLiquidityTokenAddress(),
//                tokenX.getTokenStructType().toTypeTagString(),
//                tokenY.getTokenStructType().toTypeTagString());
//    }

    @Override
    public List<BigInteger[]> getReservesListByTokenTypeTagPairs(String[][] tokenTypeTagPairs) {
        BigInteger[] emptyReservePair = new BigInteger[]{BigInteger.ZERO, BigInteger.ZERO};
        List<BigInteger[]> rs = new ArrayList<>();
        for (int i = 0; i < tokenTypeTagPairs.length; i++) {
            String[] typeTagPair = tokenTypeTagPairs[i];
            if (typeTagPair == null || typeTagPair.length < 2 || typeTagPair[0] == null || typeTagPair[1] == null) {
                rs.add(emptyReservePair);
                LOG.info("Illegal token pair. Index: " + i + ", tokenTypeTagPairs: " + Arrays.deepToString(tokenTypeTagPairs));
                continue;
            }
            try {
                Token tokenX = tokenService.getTokenByStructType(StructType.parse(typeTagPair[0]));
                if (tokenX == null) {
                    rs.add(emptyReservePair);
                    LOG.info("Cannot find token by type tag: " + typeTagPair[0]);
                    continue;
                }
                Token tokenY = tokenService.getTokenByStructType(StructType.parse(typeTagPair[1]));
                if (tokenY == null) {
                    rs.add(emptyReservePair);
                    LOG.info("Cannot find token by type tag: " + typeTagPair[1]);
                    continue;
                }
                LiquidityToken liquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenX.getTokenId(), tokenY.getTokenId());
                if (liquidityToken == null) {
                    rs.add(emptyReservePair);
                    LOG.info("Cannot find LiquidityToken by token Id. pair: " + tokenX.getTokenId() + " / " + tokenY.getTokenId());
                    continue;
                }
                if (!liquidityToken.getLiquidityTokenId().getTokenXId().equals(tokenX.getTokenId())) {
                    Token t = tokenX;
                    tokenX = tokenY;
                    tokenY = t;
                }
                Pair<BigInteger, BigInteger> rp = contractApiClient.tokenSwapRouterGetReserves(
                        liquidityToken.getLiquidityTokenId().getLiquidityTokenAddress(),
                        tokenX.getTokenStructType().toTypeTagString(),
                        tokenY.getTokenStructType().toTypeTagString());
                if (rp == null) {
                    rs.add(emptyReservePair);
                    LOG.info("Cannot Get Reserves by on-chain TokenSwapRouter: " + liquidityToken.getLiquidityTokenId());
                    continue;
                }
                if (tokenX.getTokenStructType().toTypeTagString().equals(typeTagPair[0])) {
                    rs.add(new BigInteger[]{rp.getItem1(), rp.getItem2()});
                } else {
                    rs.add(new BigInteger[]{rp.getItem2(), rp.getItem1()});
                }
            } catch (RuntimeException e) {
                LOG.info("Runtime exception when Get Reserves by on-chain TokenSwapRouter.", e);
                rs.add(emptyReservePair);
            }
        }
        return rs;
    }

    @Override
    public Long getFarmRewardMultiplier(LiquidityTokenFarm farm) {
        LiquidityTokenFarmId liquidityTokenFarmId = farm.getLiquidityTokenFarmId();
        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarmId.getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarmId.getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
        return contractApiClient.tokenSwapFarmGetRewardMultiplier(liquidityTokenFarmId.getFarmAddress(),
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString());
    }

//    public BigDecimal getFarmEstimatedApy(LiquidityTokenFarm liquidityTokenFarm) {
//        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
//        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        return getFarmEstimatedApyAndTvlInUsd(tokenX, tokenY, liquidityTokenFarm).getItem1();
//    }

//    public BigDecimal getFarmEstimatedApyV2(LiquidityTokenFarm liquidityTokenFarm) {
//        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
//        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        return getFarmEstimatedApyAndTvlInUsdV2(tokenX, tokenY, liquidityTokenFarm).getItem1();
//    }

//    public Pair<BigDecimal, BigDecimal> getFarmEstimatedApyAndTvlInUsd(LiquidityTokenFarm liquidityTokenFarm) {
//        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
//        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        return getFarmEstimatedApyAndTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
//    }

//    public Pair<BigDecimal, BigDecimal> getFarmEstimatedApyAndTvlInUsdV2(LiquidityTokenFarm liquidityTokenFarm) {
//        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
//        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        return getFarmEstimatedApyAndTvlInUsdV2(tokenX, tokenY, liquidityTokenFarm);
//    }

    @Override
    public BigDecimal getFarmTvlInUsd(LiquidityTokenFarm liquidityTokenFarm) {
        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
        return getFarmTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
    }

//    public BigDecimal getFarmEstimatedApy(LiquidityTokenFarm liquidityTokenFarm, BigDecimal tvlInUsd) {
//        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
//        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        return getFarmEstimatedApy(tokenX, tokenY, liquidityTokenFarm, tvlInUsd);
//    }

    @Override
    public BigDecimal getFarmEstimatedApyV2(LiquidityTokenFarm liquidityTokenFarm, BigDecimal tvlInUsd) {
        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        if (USE_FARM_AVERAGE_APY) {
//            return getFarmEstimatedApyV2(tokenX, tokenY, liquidityTokenFarm, tvlInUsd);
//        } else {
        return getFarmEstimatedApyV2(tokenX, tokenY, liquidityTokenFarm);
//        }
    }

//    public BigDecimal getFarmEstimatedApyByTokenIdPair(String tokenXId, String tokenYId) {
//        Token tokenX = tokenService.getTokenOrElseThrow(tokenXId, () -> new RuntimeException("Cannot find Token by Id: " + tokenXId));
//        Token tokenY = tokenService.getTokenOrElseThrow(tokenYId, () -> new RuntimeException("Cannot find Token by Id: " + tokenYId));
//        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenX.getTokenId(), tokenY.getTokenId());
//        if (liquidityTokenFarm == null) {
//            throw new RuntimeException("Cannot find LiquidityTokenFarm by tokenId pair: " + tokenXId + " / " + tokenYId);
//        }
//        return getFarmEstimatedApyAndTvlInUsd(tokenX, tokenY, liquidityTokenFarm).getItem1();
//    }
//
//    public BigDecimal getFarmEstimatedApyByTokenIdPairV2(String tokenXId, String tokenYId) {
//        Token tokenX = tokenService.getTokenOrElseThrow(tokenXId, () -> new RuntimeException("Cannot find Token by Id: " + tokenXId));
//        Token tokenY = tokenService.getTokenOrElseThrow(tokenYId, () -> new RuntimeException("Cannot find Token by Id: " + tokenYId));
//        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenX.getTokenId(), tokenY.getTokenId());
//        if (liquidityTokenFarm == null) {
//            throw new RuntimeException("Cannot find LiquidityTokenFarm by tokenId pair: " + tokenXId + " / " + tokenYId);
//        }
//        if (USE_FARM_AVERAGE_APY) {
//            BigDecimal totalTvlInUsd = getFarmTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
//            return getFarmEstimatedApyV2(tokenX, tokenY, liquidityTokenFarm, totalTvlInUsd);
//        } else {
//            return getFarmEstimatedApyV2(tokenX, tokenY, liquidityTokenFarm);
//        }
//    }

//    private Pair<BigDecimal, BigDecimal> getFarmEstimatedApyAndTvlInUsd(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
//        BigDecimal totalTvlInUsd = getFarmTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
//        BigDecimal estimatedApy = getFarmEstimatedApy(tokenX, tokenY, liquidityTokenFarm, totalTvlInUsd);
//        return new Pair<>(estimatedApy, totalTvlInUsd);
//    }
//
//    private Pair<BigDecimal, BigDecimal> getFarmEstimatedApyAndTvlInUsdV2(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
//        BigDecimal totalTvlInUsd = getFarmTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
//        BigDecimal estimatedApy;
//        if (USE_FARM_AVERAGE_APY) {
//            estimatedApy = getFarmEstimatedApyV2(tokenX, tokenY, liquidityTokenFarm, totalTvlInUsd);
//        } else {
//            estimatedApy = getFarmEstimatedApyV2(tokenX, tokenY, liquidityTokenFarm);
//        }
//        return new Pair<>(estimatedApy, totalTvlInUsd);
//    }

    private BigDecimal getFarmEstimatedApyV2(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
        Pair<BigDecimal, BigInteger> p = getFarmRewardPerYearInUsdV2AndAssetTotalWeight(tokenX, tokenY, liquidityTokenFarm);
        BigDecimal rewardPerYearInUsd = p.getItem1();
        BigInteger asset_total_weight = p.getItem2(); // asset_total_weight which is a "boosted" virtual LP amount.
        BigDecimal virtualTotalTvlInUsd = getLiquidityAmountInUsd(tokenX, tokenY,
                liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getLiquidityTokenAddress(),
                asset_total_weight);
        int scale = 10;
        BigDecimal estimatedApy = virtualTotalTvlInUsd.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : rewardPerYearInUsd
                .divide(virtualTotalTvlInUsd, scale, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return estimatedApy;
    }

//    /**
//     * Get FarmEstimatedApyV2 using the totalTvlInUsd argument.
//     *
//     * @param tokenX
//     * @param tokenY
//     * @param liquidityTokenFarm
//     * @param totalTvlInUsd
//     * @return
//     */
//    private BigDecimal getFarmEstimatedApyV2(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm, BigDecimal totalTvlInUsd) {
//        BigDecimal rewardPerYearInUsd = getFarmRewardPerYearInUsdV2AndAssetTotalWeight(tokenX, tokenY, liquidityTokenFarm).getItem1();
//        int scale = 10;
//        return totalTvlInUsd.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : rewardPerYearInUsd
//                .divide(totalTvlInUsd, scale, RoundingMode.HALF_UP)
//                .multiply(BigDecimal.valueOf(100));
//    }

//    private BigDecimal getFarmEstimatedApy(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm, BigDecimal totalTvlInUsd) {
//        BigDecimal rewardPerYearInUsd = getFarmRewardPerYearInUsd(tokenX, tokenY, liquidityTokenFarm);
////        Integer m = jsonRpcClient.tokenSwapFarmGetRewardMultiplier(liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress(),
////                tokenX.getTokenStructType().toTypeTagString(),
////                tokenY.getTokenStructType().toTypeTagString());
////        if (m == null || m == 0) {
////            m = 1;
////        }
//        int scale = 10;//Math.max(tokenXScalingFactor.toString().length(), tokenYScalingFactor.toString().length()) - 1;
//        return totalTvlInUsd.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : rewardPerYearInUsd//.multiply(BigDecimal.valueOf(m))
//                .divide(totalTvlInUsd, scale, RoundingMode.HALF_UP)
//                .multiply(BigDecimal.valueOf(100));
//    }


//    private BigDecimal getFarmRewardPerYearInUsd(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
//        String farmAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress();
//
//        BigInteger rewardReleasePerSecond = jsonRpcClient.tokenSwapFarmQueryReleasePerSecond(farmAddress,
//                tokenX.getTokenStructType().toTypeTagString(),
//                tokenY.getTokenStructType().toTypeTagString());
//        BigInteger rewardPerYear = rewardReleasePerSecond.multiply(BigInteger.valueOf(ONE_YEAR_SECONDS))
//                .multiply(BigInteger.valueOf(liquidityTokenFarm.getRewardMultiplier()));
//
//        Token rewardToken = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getRewardTokenId(),
//                () -> new RuntimeException("Cannot find Token by Id: " + liquidityTokenFarm.getRewardTokenId()));
//        return getTokenAmountInUsd(rewardToken, rewardPerYear);
//    }

    private Pair<BigDecimal, BigInteger> getFarmRewardPerYearInUsdV2AndAssetTotalWeight(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
        String farmAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress();

        Pair<BigInteger, BigInteger> p = contractApiClient.tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(
                farmAddress,
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString()
        );
        BigInteger rewardReleasePerSecond = p.getItem1();
        BigInteger rewardPerYear = rewardReleasePerSecond.multiply(BigInteger.valueOf(ONE_YEAR_SECONDS));
        //.multiply(BigInteger.valueOf(liquidityTokenFarm.getRewardMultiplier()));
        Token rewardToken = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getRewardTokenId(),
                () -> new RuntimeException("Cannot find Token by Id: " + liquidityTokenFarm.getRewardTokenId()));
        BigDecimal tokenAmountInUsd = getTokenAmountInUsd(rewardToken, rewardPerYear);
        return new Pair<>(tokenAmountInUsd, p.getItem2());
    }

//    public BigInteger getFarmDailyReward(LiquidityTokenFarm liquidityTokenFarm) {
//        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
//        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        String farmAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress();
//        BigInteger rewardReleasePerSecond = jsonRpcClient.tokenSwapFarmQueryReleasePerSecond(farmAddress,
//                tokenX.getTokenStructType().toTypeTagString(),
//                tokenY.getTokenStructType().toTypeTagString());
//        BigInteger rewardPerDay = rewardReleasePerSecond.multiply(BigInteger.valueOf(ONE_DAY_SECONDS))
//                .multiply(BigInteger.valueOf(liquidityTokenFarm.getRewardMultiplier()));
//        return rewardPerDay;
//    }

    @Override
    public BigInteger getFarmDailyRewardV2(LiquidityTokenFarm liquidityTokenFarm) {
//        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
//        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
//        String farmAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress();
//        BigInteger rewardReleasePerSecond = jsonRpcClient.tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(farmAddress,
//                tokenX.getTokenStructType().toTypeTagString(),
//                tokenY.getTokenStructType().toTypeTagString()).getItem1();
//        BigInteger rewardPerDay = rewardReleasePerSecond.multiply(BigInteger.valueOf(ONE_DAY_SECONDS));
//        //.multiply(BigInteger.valueOf(liquidityTokenFarm.getRewardMultiplier()));
//        return rewardPerDay;

        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
        String farmAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress();
        BigInteger rewardReleasePerSecond = contractApiClient.tokenSwapFarmQueryReleasePerSecondV2(farmAddress,
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString());
        BigInteger rewardPerDay = rewardReleasePerSecond.multiply(BigInteger.valueOf(ONE_DAY_SECONDS));
        //.multiply(BigInteger.valueOf(liquidityTokenFarm.getRewardMultiplier()));
        return rewardPerDay;
    }


    /**
     * get farm total value locked in USD.
     */
    private BigDecimal getFarmTvlInUsd(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
        if (!tokenX.getTokenId().equals(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId())
                || !tokenY.getTokenId().equals(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId())) {
            throw new IllegalArgumentException("Token Id in token object NOT equals token id in farm object.");
        }
        String farmAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress();
        String liquidityTokenAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getLiquidityTokenAddress();
        Pair<BigInteger, BigInteger> reservePair = contractApiClient.getTokenSwapFarmStakedReserves(
                farmAddress,
                liquidityTokenAddress,
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString());
        BigInteger tokenXAmount = reservePair.getItem1();
        BigInteger tokenYAmount = reservePair.getItem2();
        BigDecimal tokenXReserveInUsd = getTokenAmountInUsd(tokenX, tokenXAmount);
        BigDecimal tokenYReserveInUsd = getTokenAmountInUsd(tokenY, tokenYAmount);
        return tokenXReserveInUsd.add(tokenYReserveInUsd);
    }

    private BigDecimal getLiquidityAmountInUsd(Token tokenX, Token tokenY, String liquidityTokenAddress, BigInteger liquidity) {
        Pair<BigInteger, BigInteger> reservePair = contractApiClient.getReservesByLiquidity(
                liquidityTokenAddress,
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString(),
                liquidity);
        BigInteger tokenXAmount = reservePair.getItem1();
        BigInteger tokenYAmount = reservePair.getItem2();
        BigDecimal tokenXReserveInUsd = getTokenAmountInUsd(tokenX, tokenXAmount);
        BigDecimal tokenYReserveInUsd = getTokenAmountInUsd(tokenY, tokenYAmount);
        return tokenXReserveInUsd.add(tokenYReserveInUsd);
    }


    @Override
    public BigDecimal getTokenAmountInUsd(Token token, BigInteger tokenAmount) {
        BigInteger tokenScalingFactor = getTokenScalingFactorOffChainFirst(token);
        BigDecimal tokenToUsdRate = getToUsdExchangeRate(token, defaultTokenToUsdExchangeRateShortCircuitFun());
        return new BigDecimal(tokenAmount)
                .divide(new BigDecimal(tokenScalingFactor), tokenScalingFactor.toString().length() - 1, RoundingMode.HALF_UP)
                .multiply(tokenToUsdRate);
    }

    @Override
    public BigDecimal getTokenAmountInUsdOffChainFirst(Token token, BigInteger tokenAmount) {
        BigInteger tokenScalingFactor = getTokenScalingFactorOffChainFirst(token);
        BigDecimal tokenToUsdRate = getToUsdExchangeRate(token, offChainTokenToUsdExchangeRateShortCircuitFun());
        return new BigDecimal(tokenAmount)
                .divide(new BigDecimal(tokenScalingFactor), tokenScalingFactor.toString().length() - 1, RoundingMode.HALF_UP)
                .multiply(tokenToUsdRate);
    }


    // get token scaling factor from database first, or else get from on-chain.
    private BigInteger getTokenScalingFactorOffChainFirst(Token token) {
        if (token.getScalingFactor() != null) {
            return token.getScalingFactor();
        }
        return contractApiClient.getTokenScalingFactor(token.getTokenStructType().toTypeTagString());
    }

//    @Override
//    public BigInteger getTokenScalingFactor(String typeTag) {
//        return jsonRpcClient.tokenGetScalingFactor(typeTag);
//    }

    @Override
    public BigDecimal getToUsdExchangeRate(String tokenTypeTag) {
        StructType tokenStructType = StructType.parse(tokenTypeTag);
        Token token = tokenService.getTokenByStructType(tokenStructType);
        if (token == null) {
            return null;
        }
        return getToUsdExchangeRate(token, defaultTokenToUsdExchangeRateShortCircuitFun());
    }

    @Override
    public BigDecimal getExchangeRate(String tokenTypeTag, String toTokenTypeTag) {
        Token token = tokenService.getTokenByStructType(StructType.parse(tokenTypeTag));
        if (token == null) {
            throw new IllegalArgumentException("tokenTypeTag");
        }
        Token toToken = tokenService.getTokenByStructType(StructType.parse(toTokenTypeTag));
        if (toToken == null) {
            throw new IllegalArgumentException("toTokenTypeTag");
        }
        return getExchangeRate(token, toToken);
    }

    @Override
    public Map<String, BigDecimal> getAnyToUsdExchangeRateOffChainFirst(List<String> tokenTypeTags) {
        Map<String, BigDecimal> m = new HashMap<>();
        for (String t : tokenTypeTags) {
            BigDecimal price = getToUsdExchangeRateOffChainFirst(t);
            if (price != null) {
                m.put(t, price);
                break;
            }
        }
        return m;
    }

    @Override
    public BigDecimal getToUsdExchangeRateOffChainFirst(String tokenTypeTag) {
        StructType tokenStructType = StructType.parse(tokenTypeTag);
        Token token = tokenService.getTokenByStructType(tokenStructType);
        return getToUsdExchangeRateOffChainFirst(token);
    }

    @Override
    public BigDecimal getToUsdExchangeRateByTokenId(String tokenId) {
        Token token = tokenService.getToken(tokenId);//getTokenOrElseThrow(tokenId, () -> new RuntimeException("Cannot find Token by Id: " + tokenId));
        if (token == null) {
            return null;
        }
        return getToUsdExchangeRate(token, defaultTokenToUsdExchangeRateShortCircuitFun());
    }

    private BigDecimal getToUsdExchangeRateOffChainFirst(Token token) {
        return getToUsdExchangeRate(token, offChainTokenToUsdExchangeRateShortCircuitFun());
    }

    private Function<Token, BigDecimal> offChainTokenToUsdExchangeRateShortCircuitFun() {
        return token -> {
            BigDecimal rate;
            try {
                if (tokenPriceService.isUsdEquivalentToken(token)) {
                    return BigDecimal.ONE;
                }
                rate = tokenPriceService.getToUsdExchangeRateByTokenId(token.getTokenId());
            } catch (RuntimeException runtimeException) {
                LOG.info("Get token to USD price from off-chain service error.", runtimeException);
                rate = null;
            }
            return rate;
        };
    }

    private Function<Token, BigDecimal> defaultTokenToUsdExchangeRateShortCircuitFun() {
        return token -> {
            if (tokenPriceService.isUsdEquivalentToken(token)) {
                return BigDecimal.ONE;
            } else {
                return null;
            }
        };
    }

    /**
     * Get 'THE_TOKEN'-to-USD price based on swap pool.
     *
     * @param token token
     * @return to USD price.
     */
    private BigDecimal getToUsdExchangeRate(Token token, Function<Token, BigDecimal> shortCircuitFun) {
        if (shortCircuitFun != null) {
            BigDecimal r = shortCircuitFun.apply(token);
            if (r != null) {
                return r;
            }
        }

        String[] path = new String[0];
        if (token.getToUsdExchangeRatePath() != null) {
            path = token.getToUsdExchangeRatePath().trim().split(",");
        }
        if (path.length == 0) {
            String usdTokenId = tokenPriceService.getDefaultUsdEquivalentTokenId();
            path = new String[]{usdTokenId};
        }
        BigDecimal r = null;
        Token fromToken = token;
        for (int i = 0; i < path.length; i++) {
            String toTokenId = path[i].trim();
            if (toTokenId.isEmpty()) {
                continue;
            }
            BigDecimal er;
            if (TOKEN_ID_OFF_CHAIN_USD.equals(toTokenId)) {
                // ////////////////// Get to USD exchange rate by Off-Chain API ////////////////////
                er = tokenPriceService.getToUsdExchangeRateByTokenId(fromToken.getTokenId());
                r = i == 0 ? er : r.multiply(er);
                break;//fromToken = toToken;
                // /////////////////////////////////////////////////////////////////////////////////
            } else {
                Token toToken = tokenService.getTokenOrElseThrow(toTokenId, () -> new RuntimeException("Cannot find token by Id. : " + toTokenId));
                er = getExchangeRate(fromToken, toToken);
                r = i == 0 ? er : r.multiply(er);
                fromToken = toToken;
            }
        }
        if (r == null) {
            throw new RuntimeException("token.getToUsdExchangeRatePath() is empty. Token Id.: " + token.getTokenId());
        }
        return r;
    }

    /**
     * Get 'FROM_TOKEN'-to-'TO_TOKEN' price based on swap pool.
     *
     * @param fromToken from Token
     * @param toToken   to Token
     * @return exchange rate
     */
    private BigDecimal getExchangeRate(Token fromToken, Token toToken) {
        TokenIdPair tokenIdPair = new TokenIdPair(fromToken.getTokenId(), toToken.getTokenId());
        LiquidityToken liquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenIdPair.tokenXId(), tokenIdPair.tokenYId());
        if (liquidityToken == null) {
            throw new RuntimeException("Cannot find LiquidityToken by tokenId pair: " + tokenIdPair.tokenXId() + " / " + tokenIdPair.tokenYId());
        }
        Token src, trg;
        if (fromToken.getTokenId().equals(tokenIdPair.tokenXId())) {
            src = fromToken;
            trg = toToken;
        } else {
            src = toToken;
            trg = fromToken;
        }
        BigDecimal r = contractApiClient.getExchangeRate(liquidityToken.getLiquidityTokenId().getLiquidityTokenAddress(),
                src.getTokenStructType().toTypeTagString(), trg.getTokenStructType().toTypeTagString(),
                getTokenScalingFactorOffChainFirst(src), getTokenScalingFactorOffChainFirst(trg));
        if (fromToken.getTokenId().equals(tokenIdPair.tokenXId())) {
            return r;
        } else {
            return BigDecimal.ONE.divide(r, r.scale(), RoundingMode.HALF_UP);
        }
    }

    /**
     * refresh token scaling factors in database.
     */
    @Override
    public void refreshOffChainScalingFactors() {
        tokenService.findByScalingFactorIsNull().forEach((t) -> {
            if (t.getScalingFactor() == null) {
                t.setScalingFactor(contractApiClient.getTokenScalingFactor(t.getTokenStructType().toTypeTagString()));
                t.setUpdatedAt(System.currentTimeMillis());
                t.setUpdatedBy("admin");
                tokenService.save(t);
            }
        });
    }

    @Override
    public BigInteger getSyrupPoolTotalStakeAmount(SyrupPool pool) {
        String tokenId = pool.getSyrupPoolId().getTokenId();
        Token token = tokenService.getTokenOrElseThrow(tokenId, () -> new RuntimeException("Cannot find Token by Id: " + tokenId));
        return contractApiClient.syrupPoolQueryTotalStake(pool.getSyrupPoolId().getPoolAddress(),
                token.getTokenStructType().toTypeTagString());
    }

    @Override
    public BigDecimal getSyrupPoolTvlInUsd(SyrupPool pool) {
        String tokenId = pool.getSyrupPoolId().getTokenId();
        Token token = tokenService.getTokenOrElseThrow(tokenId, () -> new RuntimeException("Cannot find Token by Id: " + tokenId));
        BigInteger stakeAmount = contractApiClient.syrupPoolQueryTotalStake(pool.getSyrupPoolId().getPoolAddress(),
                token.getTokenStructType().toTypeTagString());
        return getTokenAmountInUsd(token, stakeAmount);
    }

    @Override
    public BigDecimal getSyrupPoolEstimatedApy(SyrupPool pool, BigDecimal tvlInUsd) {
        BigDecimal rewardPerYearInUsd = getSyrupPoolRewardPerYearInUsd(pool);
//        Token token = tokenService.getTokenOrElseThrow(pool.getSyrupPoolId().getTokenId(), () -> new RuntimeException("Cannot find Token by Id: " + pool.getSyrupPoolId().getTokenId()));
//        Integer m = jsonRpcClient.syrupPoolGetRewardMultiplier(pool.getSyrupPoolId().getPoolAddress(), token.getTokenStructType().toTypeTagString());
//        if (m == null || m == 0) {
//            m = 1;
//        }
        int scale = 10;//Is this ok???
        return tvlInUsd.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : rewardPerYearInUsd//.multiply(BigDecimal.valueOf(m))
                .divide(tvlInUsd, scale, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal getSyrupPoolRewardPerYearInUsd(SyrupPool pool) {
        String rewardTokenId = pool.getRewardTokenId();
        Token rewardToken = tokenService.getTokenOrElseThrow(rewardTokenId, () -> new RuntimeException("Cannot find Token by Id: " + rewardTokenId));

        BigInteger rewardReleasePerSecond = contractApiClient.syrupPoolQueryReleasePerSecondV2(pool.getSyrupPoolId().getPoolAddress(),
                rewardToken.getTokenStructType().toTypeTagString());
        BigInteger rewardPerYear = rewardReleasePerSecond.multiply(BigInteger.valueOf(ONE_YEAR_SECONDS));

        return getTokenAmountInUsd(rewardToken, rewardPerYear);
    }

    @Override
    public BigInteger getSyrupPoolDailyReward(SyrupPool pool) {
        String rewardTokenId = pool.getRewardTokenId();
        Token rewardToken = tokenService.getTokenOrElseThrow(rewardTokenId, () -> new RuntimeException("Cannot find Token by Id: " + rewardTokenId));
        BigInteger rewardReleasePerSecond = contractApiClient.syrupPoolQueryReleasePerSecondV2(pool.getSyrupPoolId().getPoolAddress(),
                rewardToken.getTokenStructType().toTypeTagString());
        BigInteger rewardPerDay = rewardReleasePerSecond.multiply(BigInteger.valueOf(ONE_DAY_SECONDS));
        return rewardPerDay;//.multiply(BigInteger.valueOf(SYRUP_POOL_TOTAL_REWARD_MULTIPLIER));
        //SYRUP_POOL_TOTAL_REWARD_MULTIPLIER = 2 + 3 + 4 + 6 + 8;
    }

    @Override
    public List<SyrupMultiplierPoolInfo> getSyrupMultiplierPools(SyrupPool syrupPool, Boolean estimateApr) {
        Token poolToken = tokenService.getTokenOrElseThrow(syrupPool.getSyrupPoolId().getTokenId(),
                () -> new RuntimeException("Cannot find Token by Id: " + syrupPool.getSyrupPoolId().getTokenId()));
        Triple<List<Long>, List<Long>, List<BigInteger>> triple =
                contractApiClient.syrupPoolQueryAllMultiplierPools(syrupPool.getSyrupPoolId().getPoolAddress(),
                        poolToken.getTokenStructType().toTypeTagString());
        List<SyrupMultiplierPoolInfo> pools = new ArrayList<>();
        for (int i = 0; i < triple.getItem1().size(); i++) {
            SyrupMultiplierPoolInfo p = new SyrupMultiplierPoolInfo();
            p.setPledgeTimeSeconds(triple.getItem1().get(i));
            p.setMultiplier(triple.getItem2().get(i));
            p.setAssetAmount(triple.getItem3().get(i));
            pools.add(p);
        }
        if (estimateApr != null && estimateApr) {
            // estimate APR
            BigInteger totalAssetWeight = BigInteger.ZERO;
            for (SyrupMultiplierPoolInfo p : pools) {
                p.setAssetWeight(p.getAssetAmount().multiply(BigInteger.valueOf(p.getMultiplier())));
                totalAssetWeight = totalAssetWeight.add(p.getAssetWeight());
            }
            for (SyrupMultiplierPoolInfo p : pools) {
                if (BigInteger.ZERO.compareTo(totalAssetWeight) == 0) {
                    p.setPoolRatio(BigDecimal.ZERO);
                } else {
                    p.setPoolRatio(new BigDecimal(p.getAssetWeight()).divide(new BigDecimal(totalAssetWeight), 9, RoundingMode.HALF_UP));
                }
            }
            BigDecimal totalRewardPerYearInUsd = getSyrupPoolRewardPerYearInUsd(syrupPool);
            for (SyrupMultiplierPoolInfo p : pools) {
                BigDecimal assetAmountInUsd = getTokenAmountInUsd(poolToken, p.getAssetAmount());
                if (BigDecimal.ZERO.compareTo(assetAmountInUsd) == 0) {
                    p.setEstimatedApr(BigDecimal.ZERO);
                } else {
                    p.setEstimatedApr(totalRewardPerYearInUsd
                            .multiply(p.getPoolRatio())
                            .divide(assetAmountInUsd, 9, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                    );
                }
            }
        }

        return pools;
    }

//    public Integer getSyrupPoolRewardMultiplier(SyrupPool pool) {
//        String tokenId = pool.getSyrupPoolId().getTokenId();
//        Token token = tokenService.getTokenOrElseThrow(tokenId, () -> new RuntimeException("Cannot find Token by Id: " + tokenId));
//        return jsonRpcClient.syrupPoolGetRewardMultiplier(pool.getSyrupPoolId().getPoolAddress(),
//                token.getTokenStructType().toTypeTagString());
//    }

    @Override
    public List<SyrupStake> getSyrupPoolStakeList(SyrupPool syrupPool, String accountAddress) {
        Token token = this.tokenService.getToken(syrupPool.getSyrupPoolId().getTokenId());
        if (token == null) {
            LOG.info("getSyrupPoolStakeList error, cannot get token by Id: " + syrupPool.getSyrupPoolId().getTokenId());
            return null;
        }
        try {
            //return jsonRpcClient.syrupPoolQueryStakeList(syrupPool.getSyrupPoolId().getPoolAddress(),
            List<SyrupStake> stakeList = contractApiClient.syrupPoolQueryStakeWithExpectedGainList(syrupPool.getSyrupPoolId().getPoolAddress(),
                    token.getTokenStructType().toTypeTagString(),
                    accountAddress);
            stakeList.forEach(s -> s.setTokenTypeTag(token.getTokenStructType().toTypeTagString()));
            return stakeList;
        } catch (RuntimeException e) {
            LOG.info("getSyrupPoolStakeList RuntimeException.", e);
            return Collections.emptyList();
        }
    }

    @Override
    public BigInteger getAccountVestarAmount(String accountAddress) {
        return this.contractApiClient.getAccountVestarAmount(accountAddress);
    }

    @Override
    public BigInteger getAccountVestarAmountByStakeId(String accountAddress, Long stakeId, String tokenTypeTag) {
        return this.contractApiClient.getVestarAmountByTokenTypeAndStakeId(accountAddress, tokenTypeTag, stakeId);
    }

}