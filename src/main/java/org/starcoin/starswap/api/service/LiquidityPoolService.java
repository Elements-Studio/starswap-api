package org.starcoin.starswap.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.LiquidityPool;
import org.starcoin.starswap.api.data.model.LiquidityPoolId;
import org.starcoin.starswap.api.data.model.TokenIdPair;
import org.starcoin.starswap.api.data.repo.LiquidityPoolRepository;

import java.util.List;
import java.util.Map;

@Service
public class LiquidityPoolService {

    private static final Logger LOG = LoggerFactory.getLogger(LiquidityPoolService.class);

    private final LiquidityPoolRepository liquidityPoolRepository;

    @Autowired
    public LiquidityPoolService(LiquidityPoolRepository liquidityPoolRepository) {
        this.liquidityPoolRepository = liquidityPoolRepository;
    }

    @Cacheable(cacheNames = "allLiquidityPoolsCache", key = "'NO_KEY'", unless = "#result == null")
    public List<LiquidityPool> findByDeactivedIsFalse() {
        return liquidityPoolRepository.findByDeactivedIsFalse();
    }

    public LiquidityPool getLiquidityPool(LiquidityPoolId liquidityPoolId) {
        return liquidityPoolRepository.findById(liquidityPoolId).orElse(null);
    }

    @Cacheable(cacheNames = "oneLiquidityPoolByTokenIdPairCache", key = "#tokenXId + ' / ' + #tokenYId", unless = "#result == null")
    public LiquidityPool findOneByTokenIdPair(String tokenXId, String tokenYId) {
        TokenIdPair tokenIdPair = new TokenIdPair(tokenXId, tokenYId);
        List<LiquidityPool> liquidityPools = liquidityPoolRepository.findByLiquidityPoolIdTokenXIdAndLiquidityPoolIdTokenYId(
                tokenIdPair.tokenXId(), tokenIdPair.tokenYId());
        if (liquidityPools.isEmpty()) {
            return null;
        }
        if (liquidityPools.size() > 1) {
            throw new RuntimeException("Find more than one LiquidityPool by: " + tokenXId + ":" + tokenYId);
        }
        return liquidityPools.get(0);
    }

    @Cacheable(cacheNames = "sumReservesGroupByTokenCache", key = "'NO_KEY'", unless = "#result == null")
    public List<Map<String, Object>> sumReservesGroupByToken() {
        return liquidityPoolRepository.sumReservesGroupByToken();
    }
}
