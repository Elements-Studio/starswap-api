package org.starcoin.starswap.api.service;

import com.williamfiset.algorithms.graphtheory.DijkstrasShortestPathAdjacencyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.LiquidityToken;
import org.starcoin.starswap.api.data.model.LiquidityTokenId;
import org.starcoin.starswap.api.data.model.TokenIdPair;
import org.starcoin.starswap.api.data.repo.LiquidityTokenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiquidityTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(LiquidityTokenService.class);

    private final LiquidityTokenRepository liquidityTokenRepository;

    @Autowired
    public LiquidityTokenService(LiquidityTokenRepository liquidityTokenRepository) {
        this.liquidityTokenRepository = liquidityTokenRepository;
    }

    @Cacheable(cacheNames = "allLiquidityTokensCache", key = "'NO_KEY'", unless = "#result == null")
    public List<LiquidityToken> findByDeactivedIsFalse() {
        return liquidityTokenRepository.findByDeactivedIsFalse();
    }

    public LiquidityToken getLiquidityToken(LiquidityTokenId liquidityTokenId) {
        return liquidityTokenRepository.findById(liquidityTokenId).orElse(null);
    }

    @Cacheable(cacheNames = "oneLiquidityTokenByTokenIdPairCache", key = "#tokenXId + ' / ' + #tokenYId", unless="#result == null")
    public LiquidityToken findOneByTokenIdPair(String tokenXId, String tokenYId) {
        TokenIdPair tokenIdPair = new TokenIdPair(tokenXId, tokenYId);
        List<LiquidityToken> liquidityTokens = liquidityTokenRepository.findByLiquidityTokenIdTokenXIdAndLiquidityTokenIdTokenYId(
                tokenIdPair.tokenXId(), tokenIdPair.tokenYId());
        if (liquidityTokens.isEmpty()) {
            return null;
        }
        if (liquidityTokens.size() > 1) {
            throw new RuntimeException("Find more than one LiquidityToken by: " + tokenXId + " / " + tokenYId);
        }
        return liquidityTokens.get(0);
    }

    @Cacheable(cacheNames = "oneLiquidityTokenByTokenIdPairCache", key = "#tokenXId + ' / ' + #tokenYId", unless="#result == null")
    public LiquidityToken findOneByTokenIdPairOrElseThrow(String tokenXId, String tokenYId) {
        LiquidityToken liquidityToken = findOneByTokenIdPair(tokenXId, tokenYId);
        if (liquidityToken == null) {
            throw new RuntimeException("Cannot find LiquidityToken by: " + tokenXId + " / " + tokenYId);
        }
        return liquidityToken;
    }

    @Cacheable(cacheNames = "shortestIndirectSwapPathCache", key = "#tokenXId + ' / ' + #tokenYId", unless = "#result == null")
    public List<String> getShortestIndirectSwapPath(String tokenXId, String tokenYId) {
        List<LiquidityToken> liquidityTokens = liquidityTokenRepository.findByDeactivedIsFalse();
        List<String> tokenIdList = getTokenIdList(liquidityTokens);
        DijkstrasShortestPathAdjacencyList pathAdjacencyList = new DijkstrasShortestPathAdjacencyList(tokenIdList.size());
        for (LiquidityToken t : liquidityTokens) {
            String lpTokenXId = t.getLiquidityTokenId().getTokenXId();
            String lpTokenYId = t.getLiquidityTokenId().getTokenYId();
            if (new TokenIdPair(tokenXId, tokenYId).equals(new TokenIdPair(lpTokenXId, lpTokenYId))) {
                continue;
            }
            pathAdjacencyList.addEdge(tokenIdList.indexOf(lpTokenXId), tokenIdList.indexOf(lpTokenYId), 1);
            pathAdjacencyList.addEdge(tokenIdList.indexOf(lpTokenYId), tokenIdList.indexOf(lpTokenXId), 1);
        }
        List<Integer> path = pathAdjacencyList.reconstructPath(tokenIdList.indexOf(tokenXId), tokenIdList.indexOf(tokenYId));
        return path.stream().map(tokenIdList::get).collect(Collectors.toList());
    }

    private List<String> getTokenIdList(List<LiquidityToken> liquidityTokens) {
        List<String> tokenIdList = new ArrayList<>();
        for (LiquidityToken t : liquidityTokens) {
            if (!tokenIdList.contains(t.getLiquidityTokenId().getTokenXId())) {
                tokenIdList.add(t.getLiquidityTokenId().getTokenXId());
            }
            if (!tokenIdList.contains(t.getLiquidityTokenId().getTokenYId())) {
                tokenIdList.add(t.getLiquidityTokenId().getTokenYId());
            }
        }
        return tokenIdList;
    }

}
