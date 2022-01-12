package org.starcoin.starswap.api.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.api.utils.JsonRpcClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.*;

@Service
public class OnChainService {
    public static final int MAX_SWAP_DEPTH = 3;

    private static final Logger LOG = LoggerFactory.getLogger(OnChainService.class);

    private final JsonRpcClient jsonRpcClient;


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
    public OnChainService(JsonRpcClient jsonRpcClient) throws MalformedURLException {
        this.jsonRpcClient = jsonRpcClient;
    }

    public Pair<List<String>, BigInteger> getBestSwapPathAndAmountOut(String tokenInId, String tokenOutId, BigInteger amountIn) {
        Pair<Token, Token> tokenPair = getTokenPairByIds(tokenInId, tokenOutId);
        Token tokenIn = tokenPair.getItem1(), tokenOut = tokenPair.getItem2();
        LiquidityToken directLiquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenInId, tokenOutId);
        List<String> indirectSwapPath = liquidityTokenService.getShortestIndirectSwapPath(tokenInId, tokenOutId);
        BigInteger directAmountOut = null;
        if (directLiquidityToken != null) {
            // todo call On-Chain contract twice??
            directAmountOut = jsonRpcClient.tokenSwapRouterGetAmountOut(directLiquidityToken.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(), tokenOut.getTokenStructType().toTypeTagString(), amountIn);
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

    public Pair<List<String>, BigInteger> getBestSwapPathAndAmountIn(String tokenInId, String tokenOutId, BigInteger amountOut) {
        Pair<Token, Token> tokenPair = getTokenPairByIds(tokenInId, tokenOutId);
        Token tokenIn = tokenPair.getItem1(), tokenOut = tokenPair.getItem2();
        LiquidityToken directLiquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenInId, tokenOutId);
        List<String> indirectSwapPath = liquidityTokenService.getShortestIndirectSwapPath(tokenInId, tokenOutId);
        BigInteger directAmountIn = null;
        if (directLiquidityToken != null) {
            // todo call On-Chain contract twice??
            directAmountIn = jsonRpcClient.tokenSwapRouterGetAmountIn(directLiquidityToken.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(), tokenOut.getTokenStructType().toTypeTagString(), amountOut);
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
            amountOut = jsonRpcClient.tokenSwapRouterGetAmountOut(liquidityTokenInOut.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(),
                    tokenOut.getTokenStructType().toTypeTagString(),
                    currentAmountIn);
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
            amountIn = jsonRpcClient.tokenSwapRouterGetAmountIn(liquidityTokenInOut.getLiquidityTokenId().getLiquidityTokenAddress(),
                    tokenIn.getTokenStructType().toTypeTagString(),
                    tokenOut.getTokenStructType().toTypeTagString(),
                    currentAmountOut);
        }
        if (amountIn == null) {
            throw new IllegalArgumentException("Path error. Size of path: " + pathTokens.size());
        }
        return amountIn;
    }

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
        BigInteger stakeAmount = jsonRpcClient.tokenSwapFarmQueryTotalStake(farmAddress, tokenX, tokenY);
        return stakeAmount;
    }

    public Pair<BigInteger, BigInteger> getFarmStakedReservesByTokenIdPair(String tokenXId, String tokenYId) {
        //LiquidityToken liquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenXId, tokenYId);
        Token tokenX = tokenService.getToken(tokenXId);
        Token tokenY = tokenService.getToken(tokenYId);
        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenXId, tokenYId);
        LiquidityTokenFarmId liquidityTokenFarmId = liquidityTokenFarm.getLiquidityTokenFarmId();
        return jsonRpcClient.getTokenSwapFarmStakedReserves(liquidityTokenFarmId.getFarmAddress(),
                liquidityTokenFarmId.getLiquidityTokenId().getLiquidityTokenAddress(),
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString());
    }

    public BigDecimal getFarmEstimatedApy(LiquidityTokenFarm liquidityTokenFarm) {
        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
        return getFarmEstimatedApyAndTvlInUsd(tokenX, tokenY, liquidityTokenFarm).getItem1();
    }

    public Pair<BigDecimal, BigDecimal> getFarmEstimatedApyAndTvlInUsd(LiquidityTokenFarm liquidityTokenFarm) {
        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
        return getFarmEstimatedApyAndTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
    }

    public BigDecimal getFarmTvlInUsd(LiquidityTokenFarm liquidityTokenFarm) {
        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
        return getFarmTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
    }

    public BigDecimal getFarmEstimatedApy(LiquidityTokenFarm liquidityTokenFarm, BigDecimal tvlInUsd) {
        Token tokenX = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find Token by Id"));
        Token tokenY = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find Token by Id"));
        return getFarmEstimatedApy(tokenX, tokenY, liquidityTokenFarm, tvlInUsd);
    }

    public BigDecimal getFarmEstimatedApyByTokenIdPair(String tokenXId, String tokenYId) {
        Token tokenX = tokenService.getTokenOrElseThrow(tokenXId, () -> new RuntimeException("Cannot find Token by Id: " + tokenXId));
        Token tokenY = tokenService.getTokenOrElseThrow(tokenYId, () -> new RuntimeException("Cannot find Token by Id: " + tokenYId));
        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenX.getTokenId(), tokenY.getTokenId());
        if (liquidityTokenFarm == null) {
            throw new RuntimeException("Cannot find LiquidityTokenFarm by tokenId pair: " + tokenXId + " / " + tokenYId);
        }
        return getFarmEstimatedApyAndTvlInUsd(tokenX, tokenY, liquidityTokenFarm).getItem1();
    }

    private Pair<BigDecimal, BigDecimal> getFarmEstimatedApyAndTvlInUsd(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
        BigDecimal totalTvlInUsd = getFarmTvlInUsd(tokenX, tokenY, liquidityTokenFarm);
        BigDecimal estimatedApy = getFarmEstimatedApy(tokenX, tokenY, liquidityTokenFarm, totalTvlInUsd);
        return new Pair<>(estimatedApy, totalTvlInUsd);
    }

    private BigDecimal getFarmEstimatedApy(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm, BigDecimal totalTvlInUsd) {
        BigDecimal rewardPerYearInUsd = getFarmRewardPerYearInUsd(tokenX, tokenY, liquidityTokenFarm);
        int scale = 10;//Math.max(tokenXScalingFactor.toString().length(), tokenYScalingFactor.toString().length()) - 1;
        return rewardPerYearInUsd
                .divide(totalTvlInUsd, scale, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal getFarmRewardPerYearInUsd(Token tokenX, Token tokenY, LiquidityTokenFarm liquidityTokenFarm) {
        String farmAddress = liquidityTokenFarm.getLiquidityTokenFarmId().getFarmAddress();

        BigInteger rewardReleasePerSecond = jsonRpcClient.tokenSwapFarmQueryReleasePerSecond(farmAddress,
                tokenX.getTokenStructType().toTypeTagString(),
                tokenY.getTokenStructType().toTypeTagString());
        BigInteger rewardPerYear = rewardReleasePerSecond.multiply(BigInteger.valueOf(60L * 60 * 24 * 365));

        Token rewardToken = tokenService.getTokenOrElseThrow(liquidityTokenFarm.getRewardTokenId(),
                () -> new RuntimeException("Cannot find Token by Id: " + liquidityTokenFarm.getRewardTokenId()));
        return getTokenAmountInUsd(rewardToken, rewardPerYear);
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
        Pair<BigInteger, BigInteger> reservePair = jsonRpcClient.getTokenSwapFarmStakedReserves(
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

    public BigDecimal getTokenAmountInUsd(Token token, BigInteger tokenAmount) {
        BigInteger tokenScalingFactor = getTokenScalingFactorOffChainFirst(token);
        BigDecimal tokenToUsdRate = getToUsdExchangeRate(token);
        return new BigDecimal(tokenAmount)
                .divide(new BigDecimal(tokenScalingFactor), tokenScalingFactor.toString().length() - 1, RoundingMode.HALF_UP)
                .multiply(tokenToUsdRate);
    }

    // get token scaling factor from database first, or else get from on-chain.
    private BigInteger getTokenScalingFactorOffChainFirst(Token token) {
        if (token.getScalingFactor() != null) {
            return token.getScalingFactor();
        }
        return jsonRpcClient.tokenGetScalingFactor(token.getTokenStructType().toTypeTagString());
    }

    public BigInteger getTokenScalingFactor(String typeTag) {
        return jsonRpcClient.tokenGetScalingFactor(typeTag);
    }

    public BigDecimal getToUsdExchangeRate(String tokenTypeTag) {
        StructType tokenStructType = StructType.parse(tokenTypeTag);
        Token token = tokenService.getTokenByStructType(tokenStructType);
        return getToUsdExchangeRate(token);
    }

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

    public BigDecimal getToUsdExchangeRateOffChainFirst(String tokenTypeTag) {
        StructType tokenStructType = StructType.parse(tokenTypeTag);
        Token token = tokenService.getTokenByStructType(tokenStructType);
        return getToUsdExchangeRateOffChainFirst(token);
    }

    public BigDecimal getToUsdExchangeRateByTokenId(String tokenId) {
        Token token = tokenService.getTokenOrElseThrow(tokenId, () -> new RuntimeException("Cannot find Token by Id: " + tokenId));
        return getToUsdExchangeRate(token);
    }

    private BigDecimal getToUsdExchangeRateOffChainFirst(Token token) {
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
        if (rate == null) {
            rate = getToUsdExchangeRate(token);
        }
        return rate;
    }

    /**
     * Get 'THE_TOKEN'-to-USD price based on swap pool.
     *
     * @param token token
     * @return to USD price.
     */
    private BigDecimal getToUsdExchangeRate(Token token) {
        if (tokenPriceService.isUsdEquivalentToken(token)) {
            return BigDecimal.ONE;
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
            Token toToken = tokenService.getTokenOrElseThrow(toTokenId, () -> new RuntimeException("Cannot find token by Id. : " + toTokenId));
            BigDecimal er = getExchangeRate(fromToken, toToken);
            r = i == 0 ? er : r.multiply(er);
            fromToken = toToken;
        }
        if (r == null) {
            throw new RuntimeException("token.getToUsdExchangeRatePath() is empty. Token Id.: " + token.getTokenId());
        }
        return r;
    }

    /**
     * Get 'FROM_TOKEN'-to-'TO_TOKEN' price based on swap pool.
     * @param fromToken from Token
     * @param toToken to Token
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
        BigDecimal r = jsonRpcClient.getExchangeRate(liquidityToken.getLiquidityTokenId().getLiquidityTokenAddress(),
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
    public void refreshOffChainScalingFactors() {
        tokenService.findByScalingFactorIsNull().forEach((t) -> {
            if (t.getScalingFactor() == null) {
                t.setScalingFactor(jsonRpcClient.tokenGetScalingFactor(t.getTokenStructType().toTypeTagString()));
                t.setUpdatedAt(System.currentTimeMillis());
                t.setUpdatedBy("admin");
                tokenService.save(t);
            }
        });
    }
}
