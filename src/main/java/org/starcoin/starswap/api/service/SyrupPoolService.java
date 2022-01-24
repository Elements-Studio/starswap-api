package org.starcoin.starswap.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.SyrupPool;
import org.starcoin.starswap.api.data.model.SyrupPoolId;
import org.starcoin.starswap.api.data.repo.SyrupPoolRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public class SyrupPoolService {

    private static final Logger LOG = LoggerFactory.getLogger(SyrupPoolService.class);

//    @Value("${starswap.lp-token-farming.default-reward-token-id}")
//    private String defaultRewardTokenId;

    @Autowired
    private SyrupPoolRepository syrupPoolRepository;

    @Cacheable(cacheNames = "allSyrupPoolsCache", key = "'NO_KEY'", unless = "#result == null")
    public List<SyrupPool> findByDeactivedIsFalse() {
        return syrupPoolRepository.findByDeactivedIsFalse();
    }

    @Cacheable(cacheNames = "oneSyrupPoolByTokenIdCache", key = "#tokenId", unless = "#result == null")
    public SyrupPool findOneByTokenId(String tokenId) {
        List<SyrupPool> syrupPools = syrupPoolRepository.findBySyrupPoolIdTokenId(tokenId);
        if (syrupPools.isEmpty()) {
            return null;
        }
        if (syrupPools.size() > 1) {
            throw new RuntimeException("Find more than one SyrupPool by: " + tokenId);
        }
        return syrupPools.get(0);
    }

    @Cacheable(cacheNames = "syrupPoolTvlInUsdCache", key = "'NO_KEY'", unless = "#result == null")
    public BigDecimal getTotalValueLockedInUsd() {
        final BigDecimal[] tvl = {BigDecimal.ZERO};
        syrupPoolRepository.findAll().forEach((f) -> {
            tvl[0] = tvl[0].add(f.getTvlInUsd());
        });
        return tvl[0];
    }

    @Transactional
    public SyrupPool addPool(SyrupPoolId syrupPoolId) {
        SyrupPool pool = this.syrupPoolRepository.findById(syrupPoolId).orElse(null);
        if (pool == null) {
            pool = new SyrupPool();
            pool.setSyrupPoolId(syrupPoolId);
            pool.setTotalStakeAmount(BigInteger.ZERO);
            pool.setSequenceNumber(999);
            pool.setDeactived(false);
            pool.setRewardTokenId(syrupPoolId.getTokenId()); //todo: is this ok??
            pool.setCreatedAt(System.currentTimeMillis());
            pool.setCreatedBy("admin");
            pool.setUpdatedAt(pool.getCreatedAt());
            pool.setUpdatedBy("admin");
        }
        this.syrupPoolRepository.save(pool);
        return pool;
    }
}
