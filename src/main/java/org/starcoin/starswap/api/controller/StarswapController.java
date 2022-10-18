package org.starcoin.starswap.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.api.service.*;
import org.starcoin.starswap.api.vo.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = {"Starswap RESTful API"})
@RestController
@RequestMapping("v1")
public class StarswapController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StarswapController.class);

    @Resource
    private Map<String, String> farmingBoostWhitelist;

    @Resource
    private TokenService tokenService;

    @Resource
    private LiquidityTokenService liquidityTokenService;

    @Resource
    private LiquidityPoolService liquidityPoolService;

    @Resource
    private LiquidityAccountService liquidityAccountService;

    @Resource
    private PullingEventTaskService pullingEventTaskService;

    @Resource
    private LiquidityTokenFarmService liquidityTokenFarmService;

    @Resource
    private LiquidityTokenFarmAccountService liquidityTokenFarmAccountService;

    @Resource
    private SyrupPoolService syrupPoolService;

    @Resource
    private SyrupPoolAccountService syrupPoolAccountService;

    @Resource
    private NodeHeartbeatService nodeHeartbeatService;

    @Resource
    private OnChainService onChainService;

    @Resource
    private TokenPriceService tokenPriceService;

    @Resource
    private List<Pair<String, String>> invisibleTokenPairs;

    private boolean isVisibleTokenPair(String tokenXId, String tokenYId) {
        return !isInvisibleTokenPair(tokenXId, tokenYId);
    }

    private boolean isInvisibleTokenPair(String tokenXId, String tokenYId) {
        if (invisibleTokenPairs == null) {
            return false;
        }
        for (Pair<String, String> pair : invisibleTokenPairs) {
            if (pair.getItem1().equals(tokenXId) && pair.getItem2().equals(tokenYId)
                    || pair.getItem1().equals(tokenYId) && pair.getItem2().equals(tokenXId)) {
                return true;
            }
        }
        return false;
    }

    @GetMapping(path = "farmingBoostWhitelist")
    public Map<String, String> getFarmingBoostWhitelist() {
        return this.farmingBoostWhitelist;
    }

    @GetMapping(path = "tokens")
    public List<Token> getTokens() {
        return tokenService.findByDeactivedIsFalse();
    }

    @GetMapping(path = "tokens/{tokenId}")
    public Token getToken(@PathVariable(name = "tokenId") String tokenId) {
        Token token = tokenService.getToken(tokenId);
        return token;
    }

    @GetMapping(path = "liquidityTokens")
    public List<LiquidityToken> getLiquidityTokens() {
        return liquidityTokenService.findByDeactivedIsFalse().stream().filter(
                p -> isVisibleTokenPair(p.getLiquidityTokenId().getTokenXId(), p.getLiquidityTokenId().getTokenYId())
        ).collect(java.util.stream.Collectors.toList());
    }


    @GetMapping(path = "liquidityTokens/{id}")
    public LiquidityToken getLiquidityToken(@PathVariable(name = "id") @ApiParam("Token pair Id., for example 'BTC:STC'") String id) {
        String[] tokenXYId = parseTokenIdPair(id);
        return liquidityTokenService.findOneByTokenIdPair(tokenXYId[0], tokenXYId[1]);
    }

    @GetMapping(path = "liquidityPools")
    public List<LiquidityPool> getLiquidityPools() {
        return liquidityPoolService.findByDeactivedIsFalse().stream().filter(
                p -> isVisibleTokenPair(p.getLiquidityPoolId().getLiquidityTokenId().getTokenXId(),
                        p.getLiquidityPoolId().getLiquidityTokenId().getTokenYId())
        ).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping(path = "liquidityPools/{id}")
    public LiquidityPool getLiquidityPool(@PathVariable(name = "id") @ApiParam("Pool Id., for example 'BTC:STC'") String id) {
        String[] tokenXYId = parseTokenIdPair(id);
        return liquidityPoolService.findOneByTokenIdPair(tokenXYId[0], tokenXYId[1]);
    }

    @GetMapping(path = "liquidityAccounts")
    public List<LiquidityAccount> getLiquidityAccounts(
            @RequestParam(value = "accountAddress", required = true) String accountAddress) {
        return liquidityAccountService.findByAccountAddress(accountAddress);
    }

    @ApiOperation("Get LP Token farm list")
    @GetMapping(path = "lpTokenFarms")
    public List<LiquidityTokenFarm> getLiquidityTokenFarms() {
        return liquidityTokenFarmService.findByDeactivedIsFalse().stream().filter(
                p -> isVisibleTokenPair(p.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenXId(),
                        p.getLiquidityTokenFarmId().getLiquidityTokenId().getTokenYId())
        ).collect(java.util.stream.Collectors.toList());
    }

    @ApiOperation("Get LP Token farm info.")
    @GetMapping(path = "lpTokenFarms/{id}")
    public LiquidityTokenFarm getLiquidityTokenFarms(@PathVariable(name = "id") @ApiParam("Farm Id., for example 'BTC:STC'") String id) {
        String[] tokenXYId = parseTokenIdPair(id);
        return liquidityTokenFarmService.findOneByTokenIdPair(tokenXYId[0], tokenXYId[1]);
    }

    @ApiOperation("Get Total Valued Locked in all farms")
    @GetMapping(path = "farmingTvlInUsd")
    public BigDecimal getFarmingTvlInUsd() {
        return liquidityTokenFarmService.getTotalValueLockedInUsd();
    }

    @GetMapping(path = "lpTokenFarmAccounts")
    public List<LiquidityTokenFarmAccount> getFarmAccounts(
            @RequestParam(value = "accountAddress", required = true) String accountAddress) {
        return liquidityTokenFarmAccountService.findByAccountAddress(accountAddress);
    }

    @GetMapping(path = "getAccountFarmStakeInfo")
    public AccountFarmStakeInfo getAccountFarmStakeInfo(@RequestParam(value = "tokenXId", required = true) String tokenXId,
                                                        @RequestParam(value = "tokenYId", required = true) String tokenYId,
                                                        @RequestParam(value = "accountAddress", required = true) String accountAddress) {
        return onChainService.getAccountFarmStakeInfo(tokenXId, tokenYId, accountAddress);
    }

    @GetMapping(path = "getAccountFarmBoostFactor")
    public Long getAccountFarmBoostFactor(@RequestParam(value = "tokenXId", required = true) String tokenXId,
                                          @RequestParam(value = "tokenYId", required = true) String tokenYId,
                                          @RequestParam(value = "accountAddress", required = true) String accountAddress) {
        return onChainService.getAccountFarmBoostFactor(tokenXId, tokenYId, accountAddress);
    }

    @GetMapping(path = "syrupPools")
    public List<SyrupPoolVO> getSyrupPools(
            @RequestParam(value = "embedSyrupMultiplierPools", required = false) Boolean embedSyrupMultiplierPools) {
        return syrupPoolService.findByDeactivedIsFalse().stream()
                .map(s -> SyrupPoolVO.toSyrupPoolVO(s,
                        (embedSyrupMultiplierPools != null && embedSyrupMultiplierPools) ?
                                onChainService.getSyrupMultiplierPools(s, true) : null))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "syrupPools/{id}")
    public SyrupPoolVO getSyrupPool(@PathVariable(name = "id") String id,
                                    @RequestParam(value = "embedSyrupMultiplierPools", required = false) Boolean embedSyrupMultiplierPools) {
        SyrupPool s = syrupPoolService.findOneByTokenId(id);
        return SyrupPoolVO.toSyrupPoolVO(s,
                (embedSyrupMultiplierPools != null && embedSyrupMultiplierPools) ?
                        onChainService.getSyrupMultiplierPools(s, true) : null);
    }

    @ApiOperation("Get Total Valued Locked in all syrup pools")
    @GetMapping(path = "syrupPoolTvlInUsd")
    public BigDecimal getSyrupPoolTvlInUsd() {
        return syrupPoolService.getTotalValueLockedInUsd();
    }

    @GetMapping(path = "syrupPoolAccounts")
    public List<SyrupPoolAccount> getSyrupPoolAccounts(
            @RequestParam(value = "accountAddress", required = true) String accountAddress) {
        return syrupPoolAccountService.findByAccountAddress(accountAddress);
    }

    @GetMapping(path = "syrupStakes")
    public List<SyrupStakeVO> getSyrupStakes(
            @RequestParam(value = "accountAddress", required = true) String accountAddress,
            @RequestParam(value = "tokenId", required = true) String tokenId
    ) {
        SyrupPool syrupPool = this.syrupPoolService.findOneByTokenId(tokenId);
        if (syrupPool == null) {
            return null;
        }
        return onChainService.getSyrupPoolStakeList(syrupPool, accountAddress);
    }

    @GetMapping("syrupMultiplierPools")
    public List<SyrupMultiplierPoolInfo> getSyrupMultiplierPools(
            @RequestParam(value = "tokenId", required = true) String tokenId,
            @RequestParam(value = "estimateApr", required = false) Boolean estimateApr) {
        SyrupPool syrupPool = this.syrupPoolService.findOneByTokenId(tokenId);
        if (syrupPool == null) {
            Collections.emptyList();
        }
        return onChainService.getSyrupMultiplierPools(syrupPool, estimateApr);
    }

    @GetMapping(path = "getAccountSyrupStakes")
    public List<SyrupStakeVO> getAccountSyrupStakes(
            @RequestParam(value = "accountAddress", required = true) String accountAddress,
            @RequestParam(value = "tokenId", required = true) String tokenId
    ) {
        SyrupPool syrupPool = this.syrupPoolService.findOneByTokenId(tokenId);
        if (syrupPool == null) {
            return null;
        }
        List<SyrupStakeVO> stakeList = onChainService.getSyrupPoolStakeList(syrupPool, accountAddress);
        stakeList.forEach(s -> {
            BigInteger vestarAmount = onChainService.getAccountVestarAmountByTokenTypeAndStakeId(accountAddress, s.getTokenTypeTag(), s.getId());
            s.setVeStarAmount(vestarAmount);
        });
        return stakeList;
    }

    @GetMapping(path = {"getAccountVeStarAmount", "getAccountVestarAmount"})
    public BigInteger getAccountVestarAmount(
            @RequestParam(value = "accountAddress", required = true) String accountAddress
    ) {
        return onChainService.getAccountVestarAmount(accountAddress);
    }

    @GetMapping(path = {"getAccountVeStarAmountAndBoostSignature", "getAccountVestarAmountAndBoostSignature"})
    public VeStarAmountAndSignature getAccountVestarAmountAndBoostSignature(
            @RequestParam(value = "accountAddress", required = true) String accountAddress
    ) {
        BigInteger amount = onChainService.getAccountVestarAmount(accountAddress);
        String signature = this.farmingBoostWhitelist.get(accountAddress);
        return new VeStarAmountAndSignature(amount, signature);
    }

    @GetMapping(path = {"getAccountStakedVeStarAmount", "getAccountStakedveStarAmount"})
    public BigInteger getAccountStakedVestarAmount(
            @RequestParam(value = "accountAddress", required = true) String accountAddress,
            @RequestParam(value = "id", required = true) Long stakeId,
            @RequestParam(value = "t", required = true) String tokenTypeTag) {
        return onChainService.getAccountVestarAmountByTokenTypeAndStakeId(accountAddress, tokenTypeTag, stakeId);
    }

    @PostMapping(path = "getTokenPairReservesList")
    public List<BigInteger[]> getReservesListByTokenTypeTagPairs(@RequestBody String[][] tokenTypeTagPairs) {
        return onChainService.getReservesListByTokenTypeTagPairs(tokenTypeTagPairs);
    }

    @GetMapping(path = "getBestSwapPath")
    public GetBestPathResult getBestSwapPath(@RequestParam("from") String tokenInId,
                                             @RequestParam("to") String tokenOutId,
                                             @RequestParam("amount") BigInteger amountIn) {
        Pair<List<String>, BigInteger> bestPath = onChainService.getBestSwapPathAndAmountOut(tokenInId, tokenOutId, amountIn);
        return new GetBestPathResult(bestPath.getItem1(), bestPath.getItem2());
    }

    @GetMapping(path = "getBestSwapPathAndAmountIn")
    public GetBestPathAndAmountInResult getBestSwapPathAndAmountIn(@RequestParam("from") String tokenInId,
                                                                   @RequestParam("to") String tokenOutId,
                                                                   @RequestParam("amountOut") BigInteger amountOut) {
        Pair<List<String>, BigInteger> bestPath = onChainService.getBestSwapPathAndAmountIn(tokenInId, tokenOutId, amountOut);
        return new GetBestPathAndAmountInResult(bestPath.getItem1(), bestPath.getItem2());
    }

    @GetMapping(path = "sumReservesGroupByToken")
    public List<Map<String, Object>> sumReservesGroupByToken() {
        return liquidityPoolService.sumReservesGroupByToken();
    }

    @GetMapping(path = "tokenToUsdPricePairMappings")
    public List<TokenToUsdPricePairMapping> getTokenToUsdPricePairMappings() {
        return tokenPriceService.getTokenToUsdPricePairMappings();
    }

    @ApiOperation("Get token to USD exchange rate computed by swap pool")
    @GetMapping(path = "getToUsdExchangeRate")
    public BigDecimal getToUsdExchangeRate(@RequestParam("t") String tokenId) {
        return onChainService.getToUsdExchangeRateByTokenId(tokenId);
    }

    @PostMapping(path = "pullingEventTasks")
    public void postPullingEventTask(@RequestBody PullingEventTask pullingEventTask) {
        pullingEventTaskService.createOrUpdatePullingEventTask(pullingEventTask);
    }

    @GetMapping(path = "heartbeatBreakIntervals")
    public List<Pair<BigInteger, BigInteger>> getBreakIntervals() {
        return nodeHeartbeatService.findBreakIntervals();
    }

    private String[] parseTokenIdPair(String tokenPairId) {
        String[] xy = tokenPairId.split(":");
        if (xy.length < 2) throw new IllegalArgumentException();
        TokenIdPair tokenIdPair = new TokenIdPair(xy[0], xy[1]);
        return tokenIdPair.toStringArray();
    }


}
