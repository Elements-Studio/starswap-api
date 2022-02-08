package org.starcoin.starswap.api.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.SyrupPool;
import org.starcoin.starswap.api.data.repo.SyrupPoolRepository;
import org.starcoin.starswap.api.service.OnChainService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SyrupPoolRefreshTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(SyrupPoolRefreshTaskService.class);
    private final SyrupPoolRepository syrupPoolRepository;
    private final OnChainService onChainService;

    public SyrupPoolRefreshTaskService(
            @Autowired SyrupPoolRepository syrupPoolRepository,
            @Autowired OnChainService onChainService) {
        this.syrupPoolRepository = syrupPoolRepository;
        this.onChainService = onChainService;
    }

    @Scheduled(fixedDelayString = "${starswap.syrup-pool-refresh-task-service.fixed-delay}")
    public void task() {
        List<SyrupPool> pools = syrupPoolRepository.findByDeactivedIsFalse();
        for (SyrupPool pool : pools) {
            LOG.info("Start refreshing syrup pool info. Pool Id: " + pool.getSyrupPoolId());
            boolean updated = false;
            try {
                pool.setTotalStakeAmount(onChainService.getSyrupPoolTotalStakeAmount(pool));
                updated = true;
                LOG.debug("Update syrup pool total stake amount Ok. Pool Id: " + pool.getSyrupPoolId());
            } catch (RuntimeException e) {
                LOG.error("Update syrup pool total stake amount error. Pool Id: " + pool.getSyrupPoolId(), e);
            }
            BigDecimal tvlInUsd = null;
            try {
                tvlInUsd = onChainService.getSyrupPoolTvlInUsd(pool);
                pool.setTvlInUsd(tvlInUsd);
                updated = true;
                LOG.debug("Update syrup pool TVL in USD Ok. Pool Id: " + pool.getSyrupPoolId());
            } catch (RuntimeException e) {
                LOG.error("Update syrup pool TVL in USD error. Pool Id: " + pool.getSyrupPoolId(), e);
            }
            if (tvlInUsd != null) {
                try {
                    BigDecimal estimatedApy = onChainService.getSyrupPoolEstimatedApy(pool, tvlInUsd);
                    pool.setEstimatedApy(estimatedApy);
                    updated = true;
                    LOG.debug("Update syrup pool estimated APY Ok. Pool Id: " + pool.getSyrupPoolId());
                } catch (RuntimeException e) {
                    LOG.error("Update syrup pool estimated APY error. Pool Id: " + pool.getSyrupPoolId(), e);
                }
            }
//            try {
//                Integer multiplier = onChainService.getSyrupPoolRewardMultiplier(pool);
//                pool.setRewardMultiplier(multiplier);
//                updated = true;
//            } catch (RuntimeException e) {
//                LOG.error("Update syrup pool reward multiplier error. Farm Id: " + pool.getSyrupPoolId(), e);
//            }
            if (updated) {
                pool.setUpdatedAt(System.currentTimeMillis());
                pool.setUpdatedBy("admin");
                syrupPoolRepository.save(pool);
            }
        }
    }

}
