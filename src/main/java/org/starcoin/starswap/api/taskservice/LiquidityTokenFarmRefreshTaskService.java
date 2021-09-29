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
            } catch (RuntimeException e) {
                LOG.error("Update farm total stake amount error. Farm Id: " + farm.getLiquidityTokenFarmId());
            }
            BigDecimal tvlInUsd = null;
            try {
                tvlInUsd = onChainService.getFarmTvlInUsd(farm);
                farm.setTvlInUsd(tvlInUsd);
                updated = true;
            } catch (RuntimeException e) {
                LOG.error("Update farm TVL in USD error. Farm Id: " + farm.getLiquidityTokenFarmId());
            }
            if (tvlInUsd != null) {
                try {
                    BigDecimal estimatedApy = onChainService.getFarmEstimatedApy(farm, tvlInUsd);
                    farm.setEstimatedApy(estimatedApy);
                    updated = true;
                } catch (RuntimeException e) {
                    LOG.error("Update farm estimated APY error. Farm Id: " + farm.getLiquidityTokenFarmId());
                }
            }
            if (updated) {
                farm.setUpdatedAt(System.currentTimeMillis());
                farm.setUpdatedBy("admin");
                liquidityTokenFarmRepository.save(farm);
            }
        }
    }


}
