package org.starcoin.starswap.api.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.utils.AptosContractApiClient;
import org.starcoin.starswap.api.utils.ContractApiClient;
import org.starcoin.starswap.api.vo.SyrupStakeVO;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Api(tags = {"Starswap RESTful API"})
@RestController
@RequestMapping("v1/contract-api")
public class ContractApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractApiController.class);
    @Resource
    private ContractApiClient contractApiClient;

    public AptosContractApiClient getAptosContractApiClient() {
        if (contractApiClient instanceof AptosContractApiClient) {
            return (AptosContractApiClient) contractApiClient;
        } else {
            throw new UnsupportedOperationException("This is NOT a Aptos version service.");
        }
    }

    @GetMapping(path = "getTokenScalingFactor")
    public BigInteger getTokenScalingFactor(@RequestParam("token") String token) {
        return contractApiClient.getTokenScalingFactor(token);
    }

    @GetMapping(path = "computeBoostFactor")
    public BigInteger computeBoostFactor(@RequestParam("userLockedVestarAmount") BigInteger userLockedVestarAmount,
                                         @RequestParam("userLockedFarmAmount") BigInteger userLockedFarmAmount,
                                         @RequestParam("totalFarmAmount") BigInteger totalFarmAmount) {
        return getAptosContractApiClient().computeBoostFactor(userLockedVestarAmount, userLockedFarmAmount,
                totalFarmAmount);
    }

    @GetMapping(path = "getBoostLockedVestarAmount")
    public BigInteger getBoostLockedVestarAmount(@RequestParam("tokenX") String tokenX,
                                                 @RequestParam("tokenY") String tokenY,
                                                 @RequestParam("accountAddress") String accountAddress) {
        return getAptosContractApiClient().getBoostLockedVestarAmount(tokenX, tokenY, accountAddress);
    }

    @GetMapping(path = "getPoolReserves")
    public Pair<BigInteger, BigInteger> getPoolReserves(@RequestParam("tokenX") String tokenX,
                                                        @RequestParam("tokenY") String tokenY) {
        return getAptosContractApiClient().tokenSwapRouterGetReserves(tokenX, tokenY);
    }

    @GetMapping(path = "getTotalLiquidity")
    public BigInteger getTotalLiquidity(@RequestParam("tokenX") String tokenX,
                                        @RequestParam("tokenY") String tokenY) {
        return getAptosContractApiClient().tokenSwapRouterGetTotalLiquidity(tokenX, tokenY);
    }

    @GetMapping(path = "getFarmStakedLiquidity")
    public BigInteger getFarmStakedLiquidity(@RequestParam("tokenX") String tokenX,
                                             @RequestParam("tokenY") String tokenY,
                                             @RequestParam("accountAddress") String accountAddress) {
        return getAptosContractApiClient().tokenSwapFarmGetAccountStakedLiquidity(tokenX, tokenY, accountAddress);
    }

    @GetMapping(path = "lookupFarmGain")
    public BigInteger lookupFarmGain(@RequestParam("tokenX") String tokenX,
                                     @RequestParam("tokenY") String tokenY,
                                     @RequestParam("accountAddress") String accountAddress) {
        return getAptosContractApiClient().tokenSwapFarmLookupGain(tokenX, tokenY, accountAddress);
    }

    @GetMapping("getSyrupPoolStakeList")
    public List<SyrupStakeVO> getSyrupPoolStakeList(@RequestParam("token") String token,
                                                    @RequestParam("accountAddress") String accountAddress) {
        return getAptosContractApiClient().syrupPoolQueryStakeList(token, accountAddress);
    }

    @GetMapping(path = "getCoinSupply")
    public BigInteger getCoinSupply(@RequestParam("token") String token) {
        return getAptosContractApiClient().getCoinSupply(token);
    }

    @GetMapping(path = "getVestarAmountByTokenTypeAndStakeId")
    public BigInteger getVestarAmountByTokenTypeAndStakeId(@RequestParam("accountAddress") String accountAddress,
                                                           @RequestParam("token") String token,
                                                           @RequestParam("stakeId") Long stakeId) {
        return contractApiClient.getVestarAmountByTokenTypeAndStakeId(accountAddress, token, stakeId);
    }
}