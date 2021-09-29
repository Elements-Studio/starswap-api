package org.starcoin.starswap.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.starcoin.starswap.api.data.model.StructType;
import org.starcoin.starswap.api.data.model.Token;
import org.starcoin.starswap.api.data.model.TokenToUsdPricePairMapping;
import org.starcoin.starswap.api.data.repo.TokenToUsdPricePairMappingRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.starcoin.starswap.api.data.model.TokenToUsdPricePairMapping.DEFAULT_MAPPING_RULE_TOKEN_ID;

/**
 * Token price (off-chain)service proxy.
 */
@Service
public class TokenPriceService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenPriceService.class);

    private static final String URL_PATH_PAIR_ID_PLACEHOLDER = "{pairId}";
    private static final String STC_TOKEN_ID = "STC";
    private static final String DEFAULT_STC_TO_USD_PAIR_ID = "STCUSD";


    /**
     * (Remote)Price service base URL.
     */
    private final String baseUrl;

    private final String getPairPriceUrl;

    private final String getProximatePriceRoundUrl;

    private final String getPriceGrowthsUrl;

    //@Value("${starswap.token-price-service.to-usd-pair-id-mappings}") //todo config to-usd-pair-id-mappings?
    private final Map<String, String> toUsdPairIdMappings;

    @Value("${starswap.default-usd-equivalent-token-id}")
    private String defaultUsdEquivalentTokenId;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenToUsdPricePairMappingRepository tokenToUsdPricePairMappingRepository;

    @Autowired
    public TokenPriceService(@Value("${starswap.token-price-service.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.getPairPriceUrl = this.baseUrl + "/priceFeeds/" + URL_PATH_PAIR_ID_PLACEHOLDER;
        this.getProximatePriceRoundUrl = this.baseUrl + "/getProximatePriceRound?pairId={pairId}&timestamp={timestamp}";
        this.getPriceGrowthsUrl = this.baseUrl + "/priceGrowths";
        toUsdPairIdMappings = new HashMap<>();
        toUsdPairIdMappings.put(STC_TOKEN_ID, DEFAULT_STC_TO_USD_PAIR_ID);
    }

    public static String getDefaultToUsdPricePairIdByTokenId(String tokenId) {
        return tokenId + "_USD";
    }

    public List<Map<String, Object>> getToUsdPriceGrowths(List<String> tokens) {
        //
        //https://price-api.starcoin.org/barnard/v1/priceGrowths?p=ETH_USD&p=STCUSD
        //
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.getPriceGrowthsUrl);
        Map<String, String> tokenToPairIdMap = new HashMap<>();
        tokens.forEach(t -> {
            Token tokenObj = tokenService.getTokenByStructType(StructType.parse(t));
            if (tokenObj == null) {
                // If token not exists, return null.
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cannot find token by type tag: " + t);
                }
            } else {
                String pairId = getToUsdPricePairIdByTokenId(tokenObj.getTokenId());
                tokenToPairIdMap.put(t, pairId);
                builder.queryParam("p", pairId);
            }
        });
        List<Map<String, Object>> pairPrices = restTemplate.getForObject(builder.build().toUri(), List.class);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String t : tokens) {
            if (!tokenToPairIdMap.containsKey(t)) {
                result.add(null);
                continue;
            }
            String pairId = tokenToPairIdMap.get(t);
            // if token to USD price not exists, return null.
            Map<String, Object> p = pairPrices.stream().filter(m -> pairId.equals(m.get("pairId"))).findFirst().orElse(null);
            result.add(p);
        }
        return result;
    }

    public List<Map<String, Object>> getProximateToUsdPriceRounds(List<String> tokens, Long timestamp) {
        List<Map<String, Object>> prs = new ArrayList<>();
        for (String t : tokens) {
            Map<String, Object> pr = getProximateToUsdPriceRound(t, timestamp);
            prs.add(pr);
        }
        return prs;
    }

    public Map<String, Object> getProximateToUsdPriceRound(String token, Long timestamp) {
        //
        //https://price-api.starcoin.org/barnard/v1/getProximatePriceRound?pairId=ETH_USD&timestamp=1
        //
        Map<String, Object> params = new HashMap<>();
        Token tokenObj = tokenService.getTokenByStructType(StructType.parse(token));
        if (tokenObj == null) {
            // If token not exists, return null.
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot find token by type tag: " + token);
            }
            return null;
        }
        params.put("pairId", getToUsdPricePairIdByTokenId(tokenObj.getTokenId()));
        params.put("timestamp", timestamp);
        return restTemplate.getForObject(getProximatePriceRoundUrl, Map.class, params);
    }

    public Map<String, BigDecimal> getAnyProximateToUsdExchangeRate(List<String> tokens, Long timestamp) {
        Map<String, BigDecimal> m = new HashMap<>();
        for (String token : tokens) {
            Map<String, Object> pr = getProximateToUsdPriceRound(token, timestamp);
            //{"pairName":"STC / USD","roundId":1631972219671,"decimals":6,"price":156700,"updatedAt":1631972219671,"pairId":"STCUSD"}
            if (pr != null) {
                BigDecimal toUsdExRate = new BigDecimal(pr.get("price").toString()).divide(
                        BigDecimal.TEN.pow(Integer.parseInt(pr.get("decimals").toString())), 18, RoundingMode.HALF_UP
                );
                m.put(token, toUsdExRate);
                break;
            }
        }
        return m;
    }

    @Cacheable(cacheNames = "tokenToUsdExchangeRateCache", key = "#tokenId", unless = "#result == null")
    public BigDecimal getToUsdExchangeRateByTokenId(String tokenId) {
        String pairId = getToUsdPricePairIdByTokenId(tokenId);
        Map<String, Object> priceInfo = restTemplate.getForObject(getPairPriceUrl.replace(URL_PATH_PAIR_ID_PLACEHOLDER, pairId), Map.class);
        if (priceInfo == null) {
            return null;
        }
        BigInteger latestPrice = new BigInteger(priceInfo.get("latestPrice").toString());
        int decimals = Integer.parseInt(priceInfo.get("decimals").toString());
        return new BigDecimal(latestPrice).divide(BigDecimal.TEN.pow(decimals), 10, RoundingMode.HALF_UP);
    }

    public boolean isUsdEquivalentToken(Token token) {
        return isUsdEquivalentTokenId(token.getTokenId());
    }

    public boolean isUsdEquivalentTokenId(String tokenId) {
        if (this.defaultUsdEquivalentTokenId.equals(tokenId)) {
            return true;
        }
        TokenToUsdPricePairMapping m = tokenToUsdPricePairMappingRepository.findById(tokenId).orElse(null);
        return m != null && m.getUsdEquivalentToken() != null && m.getUsdEquivalentToken();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getDefaultUsdEquivalentTokenId() {
        return defaultUsdEquivalentTokenId;
    }

    public String getToUsdPricePairIdByTokenId(String tokenId) {
        TokenToUsdPricePairMapping m = tokenToUsdPricePairMappingRepository.findById(tokenId).orElse(null);
        if (m != null) {
            return m.getPairId();
        }
        TokenToUsdPricePairMapping defaultMappingRule = tokenToUsdPricePairMappingRepository.findById(DEFAULT_MAPPING_RULE_TOKEN_ID).orElse(null);
        if (defaultMappingRule != null) {
            return defaultMappingRule.getPairId().replace(DEFAULT_MAPPING_RULE_TOKEN_ID, tokenId);
        }
        return toUsdPairIdMappings.containsKey(tokenId) ? toUsdPairIdMappings.get(tokenId) : getDefaultToUsdPricePairIdByTokenId(tokenId);
    }

    public void initTokenToUsdPricePairMappings() {
        TokenToUsdPricePairMapping defaultMappingRule = tokenToUsdPricePairMappingRepository.findById(
                DEFAULT_MAPPING_RULE_TOKEN_ID).orElse(null);
        if (defaultMappingRule == null) {
            createTokenToUsdPricePairMappingAndSave(DEFAULT_MAPPING_RULE_TOKEN_ID,
                    getDefaultToUsdPricePairIdByTokenId(DEFAULT_MAPPING_RULE_TOKEN_ID), false);
        }
        TokenToUsdPricePairMapping usdEquivalentTokenMapping = tokenToUsdPricePairMappingRepository.findById(
                defaultUsdEquivalentTokenId).orElse(null);
        if (usdEquivalentTokenMapping == null) {
            createTokenToUsdPricePairMappingAndSave(defaultUsdEquivalentTokenId,
                    getDefaultToUsdPricePairIdByTokenId(defaultUsdEquivalentTokenId), true);
        }
        TokenToUsdPricePairMapping stcToUsdMapping = tokenToUsdPricePairMappingRepository.findById(STC_TOKEN_ID).orElse(null);
        if (stcToUsdMapping == null) {
            createTokenToUsdPricePairMappingAndSave(STC_TOKEN_ID, DEFAULT_STC_TO_USD_PAIR_ID, false);
        }
    }

    private void createTokenToUsdPricePairMappingAndSave(String tokenId, String pairId, Boolean isUsdEquivalentToken) {
        TokenToUsdPricePairMapping m = new TokenToUsdPricePairMapping();
        m.setTokenId(tokenId);
        m.setPairId(pairId);
        m.setUsdEquivalentToken(isUsdEquivalentToken);
        m.setCreatedAt(System.currentTimeMillis());
        m.setCreatedBy("admin");
        m.setUpdatedAt(m.getCreatedAt());
        m.setUpdatedBy(m.getCreatedBy());
        tokenToUsdPricePairMappingRepository.save(m);
    }

    @Cacheable(cacheNames = "allTokenToUsdPricePairMappingsCache", key = "'NO_KEY'", unless = "#result == null")
    public List<TokenToUsdPricePairMapping> getTokenToUsdPricePairMappings() {
        return tokenToUsdPricePairMappingRepository.findAll();
    }
}
