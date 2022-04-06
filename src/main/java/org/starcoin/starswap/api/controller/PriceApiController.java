package org.starcoin.starswap.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.api.service.*;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;
import org.starcoin.starswap.api.vo.GetBestPathAndAmountInResult;
import org.starcoin.starswap.api.vo.GetBestPathResult;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Api(tags = {"Starswap RESTful API"})
@RestController
@RequestMapping("v1/price-api")
public class PriceApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceApiController.class);

    @Resource
    private TokenPriceService tokenPriceService;

    @Resource
    private OnChainService onChainService;

    @GetMapping(path = "getProximateToUsdPriceRound")
    public Map<String, Object> getProximateToUsdPriceRound(@RequestParam("token") String token,
                                                           @RequestParam("timestamp") Long timestamp) {
        return tokenPriceService.getProximateToUsdPriceRound(token, timestamp);
    }

    @GetMapping(path = "getProximateToUsdPriceRounds")
    public List<Map<String, Object>> getProximateToUsdPriceRounds(@RequestParam("t") List<String> tokens,
                                                                  @RequestParam("timestamp") Long timestamp) {
        return tokenPriceService.getProximateToUsdPriceRounds(tokens, timestamp);
    }

    @GetMapping(path = "getMultiTimestampProximateToUsdPriceRounds")
    public Map<String, List<Map<String, Object>>> getMultiTimestampProximateToUsdPriceRounds(@RequestParam("t") List<String> tokens,
                                                                                             @RequestParam("timestamp") List<Long> timestamps) {
        return tokenPriceService.getMultiTimestampProximateToUsdPriceRounds(tokens, timestamps);
    }

    @GetMapping(path = "getAnyProximateToUsdExchangeRate")
    public Map<String, BigDecimal> getAnyProximateToUsdExchangeRate(@RequestParam("t") List<String> tokens,
                                                                    @RequestParam("timestamp") Long timestamp) {
        return tokenPriceService.getAnyProximateToUsdExchangeRate(tokens, timestamp);
    }

    @GetMapping(path = "getToUsdPriceGrowths")
    public List<Map<String, Object>> getToUsdPriceGrowths(@RequestParam("t") List<String> tokens) {
        return tokenPriceService.getToUsdPriceGrowths(tokens);
    }

    @GetMapping(path = "getToUsdExchangeRate")
    public BigDecimal getToUsdExchangeRate(@RequestParam("token") String token) {
        return onChainService.getToUsdExchangeRateOffChainFirst(token);
    }

    @GetMapping(path = "getAnyToUsdExchangeRate")
    public Map<String, BigDecimal> getAnyToUsdExchangeRate(@RequestParam("t") List<String> tokens) {
        return onChainService.getAnyToUsdExchangeRateOffChainFirst(tokens);
    }


}
