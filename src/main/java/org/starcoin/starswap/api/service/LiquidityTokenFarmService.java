package org.starcoin.starswap.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarm;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmId;
import org.starcoin.starswap.api.data.model.TokenIdPair;
import org.starcoin.starswap.api.data.repo.LiquidityTokenFarmRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public class LiquidityTokenFarmService {

    private static final Logger LOG = LoggerFactory.getLogger(LiquidityTokenFarmService.class);

    @Value("${starswap.lp-token-farming.default-reward-token-id}")
    private String defaultRewardTokenId;

    @Autowired
    private LiquidityTokenFarmRepository liquidityTokenFarmRepository;

    @Cacheable(cacheNames = "allLiquidityTokenFarmsCache", key = "'NO_KEY'", unless = "#result == null")
    public List<LiquidityTokenFarm> findByDeactivedIsFalse() {
        return liquidityTokenFarmRepository.findByDeactivedIsFalse();
    }

    @Cacheable(cacheNames = "oneLiquidityTokenFarmByTokenIdPairCache", key = "#tokenXId + ' / ' + #tokenYId", unless = "#result == null")
    public LiquidityTokenFarm findOneByTokenIdPair(String tokenXId, String tokenYId) {
        TokenIdPair tokenIdPair = new TokenIdPair(tokenXId, tokenYId);
        List<LiquidityTokenFarm> liquidityTokenFarms = liquidityTokenFarmRepository.findByLiquidityTokenFarmIdTokenXIdAndLiquidityTokenFarmIdTokenYId(
                tokenIdPair.tokenXId(), tokenIdPair.tokenYId());
        if (liquidityTokenFarms.isEmpty()) {
            return null;
        }
        if (liquidityTokenFarms.size() > 1) {
            throw new RuntimeException("Find more than one LiquidityPool by: " + tokenXId + ":" + tokenYId);
        }
        return liquidityTokenFarms.get(0);
    }

    @Cacheable(cacheNames = "farmingTvlInUsdCache", key = "'NO_KEY'", unless = "#result == null")
    public BigDecimal getTotalValueLockedInUsd() {
        final BigDecimal[] tvl = {BigDecimal.ZERO};
        liquidityTokenFarmRepository.findAll().forEach((f) -> {
            tvl[0] = tvl[0].add(f.getTvlInUsd());
        });
        return tvl[0];
    }

    @Transactional
    public LiquidityTokenFarm addFarm(LiquidityTokenFarmId farmId) {
        LiquidityTokenFarm farm = this.liquidityTokenFarmRepository.findById(farmId).orElse(null);
        if (farm == null) {
            farm = new LiquidityTokenFarm();
            farm.setLiquidityTokenFarmId(farmId);
            farm.setTotalStakeAmount(BigInteger.ZERO);
            farm.setSequenceNumber(999);
            farm.setDeactived(false);
            farm.setRewardTokenId(defaultRewardTokenId);
            farm.setCreatedAt(System.currentTimeMillis());
            farm.setCreatedBy("admin");
            farm.setUpdatedAt(farm.getCreatedAt());
            farm.setUpdatedBy("admin");
        }
        this.liquidityTokenFarmRepository.save(farm);
        return farm;
    }
}
