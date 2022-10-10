package org.starcoin.starswap.api.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarm;
import org.starcoin.starswap.api.data.repo.LiquidityTokenFarmRepository;
import org.starcoin.starswap.api.service.OnChainService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.List;

@Service
public class LiquidityTokenFarmRefreshTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(LiquidityTokenFarmRefreshTaskService.class);
    //private final JsonRpcClient jsonRpcClient;
    private final LiquidityTokenFarmRepository liquidityTokenFarmRepository;
    //private final TokenService tokenService;
    private final OnChainService onChainService;

    public LiquidityTokenFarmRefreshTaskService(
            //@Value("${starcoin.json-rpc-url}") String jsonRpcUrl,
            @Autowired LiquidityTokenFarmRepository liquidityTokenFarmRepository,
            //@Autowired TokenService tokenService,
            @Autowired OnChainService onChainService) throws MalformedURLException {
        //this.jsonRpcClient = new JsonRpcClient(jsonRpcUrl);
        this.liquidityTokenFarmRepository = liquidityTokenFarmRepository;
        //this.tokenService = tokenService;
        this.onChainService = onChainService;
    }

    @Scheduled(fixedDelayString = "${starswap.lp-token-farm-refresh-task-service.fixed-delay}")
    public void task() {
        List<LiquidityTokenFarm> farms = liquidityTokenFarmRepository.findByDeactivedIsFalse();
        for (LiquidityTokenFarm farm : farms) {
            LOG.info("Start refreshing farm info. Farm Id: " + farm.getLiquidityTokenFarmId());
            boolean updated = false;
            try {
                farm.setTotalStakeAmount(onChainService.getFarmTotalStakeAmount(farm));
                updated = true;
                LOG.debug("Update farm total stake amount Ok. Farm Id: " + farm.getLiquidityTokenFarmId());
            } catch (RuntimeException e) {
                LOG.error("Update farm total stake amount error. Farm Id: " + farm.getLiquidityTokenFarmId(), e);
            }
            BigDecimal tvlInUsd = null;
            try {
                tvlInUsd = onChainService.getFarmTvlInUsd(farm);
                farm.setTvlInUsd(tvlInUsd);
                updated = true;
                LOG.debug("Update farm TVL in USD Ok. Farm Id: " + farm.getLiquidityTokenFarmId());
            } catch (RuntimeException e) {
                LOG.error("Update farm TVL in USD error. Farm Id: " + farm.getLiquidityTokenFarmId(), e);
            }
            //boolean farmingBoostEnabled = true; // Now enable farming boost!
            if (tvlInUsd != null) {
                try {
                    BigDecimal estimatedApy = // farmingBoostEnabled ?
                            onChainService.getFarmEstimatedApyV2(farm, tvlInUsd);
                    //: onChainService.getFarmEstimatedApy(farm, tvlInUsd);
                    farm.setEstimatedApy(estimatedApy);
                    updated = true;
                    LOG.debug("Update farm estimated APY Ok. Farm Id: " + farm.getLiquidityTokenFarmId());
                } catch (RuntimeException e) {
                    LOG.error("Update farm estimated APY error. Farm Id: " + farm.getLiquidityTokenFarmId(), e);
                }
            }
            try {
                Integer multiplier = onChainService.getFarmRewardMultiplier(farm);
                farm.setRewardMultiplier(multiplier);
                updated = true;
                LOG.debug("Update farm reward multiplier Ok. Farm Id: " + farm.getLiquidityTokenFarmId());
            } catch (RuntimeException e) {
                LOG.error("Update farm reward multiplier error. Farm Id: " + farm.getLiquidityTokenFarmId(), e);
            }

            try {
                BigInteger dailyReward = //farmingBoostEnabled ?
                        onChainService.getFarmDailyRewardV2(farm);
                //: onChainService.getFarmDailyReward(farm);
                farm.setDailyReward(dailyReward);
                updated = true;
                LOG.debug("Update farm reward dailyReward Ok. Farm Id: " + farm.getLiquidityTokenFarmId());
            } catch (RuntimeException e) {
                LOG.error("Update farm reward dailyReward error. Farm Id: " + farm.getLiquidityTokenFarmId(), e);
            }

            if (updated) {
                farm.setUpdatedAt(System.currentTimeMillis());
                farm.setUpdatedBy("admin");
                liquidityTokenFarmRepository.save(farm);
            }
        }
    }


}
