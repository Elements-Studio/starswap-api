package org.starcoin.starswap.api.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.LiquidityPool;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.Token;
import org.starcoin.starswap.api.data.repo.LiquidityPoolRepository;
import org.starcoin.starswap.api.service.OnChainService;
import org.starcoin.starswap.api.service.TokenService;
import org.starcoin.starswap.api.utils.JsonRpcClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.List;

@Service
public class LiquidityPoolRefreshTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(LiquidityPoolRefreshTaskService.class);
    private final JsonRpcClient jsonRpcClient;
    private final LiquidityPoolRepository liquidityPoolRepository;
    private final TokenService tokenService;
    private final OnChainService onChainService;

    @Autowired
    public LiquidityPoolRefreshTaskService(
            @Autowired JsonRpcClient jsonRpcClient,
            @Autowired LiquidityPoolRepository liquidityPoolRepository,
            @Autowired TokenService tokenService,
            @Autowired OnChainService onChainService) throws MalformedURLException {
        this.jsonRpcClient = jsonRpcClient;
        this.liquidityPoolRepository = liquidityPoolRepository;
        this.tokenService = tokenService;
        this.onChainService = onChainService;
    }

    @Scheduled(fixedDelayString = "${starswap.liquidity-pool-refresh-task-service.fixed-delay}")
    public void task() {
        List<LiquidityPool> pools = liquidityPoolRepository.findByDeactivedIsFalse();
        for (LiquidityPool pool : pools) {
            LOG.info("Start refreshing pool info. Pool Id: " + pool.getLiquidityPoolId());
            Token tokenX = tokenService.getTokenOrElseThrow(pool.getLiquidityPoolId().getLiquidityTokenId().getTokenXId(), () -> new RuntimeException("Cannot find token by Id."));
            Token tokenY = tokenService.getTokenOrElseThrow(pool.getLiquidityPoolId().getLiquidityTokenId().getTokenYId(), () -> new RuntimeException("Cannot find token by Id."));

            boolean updated = false;
            try {
                Pair<BigInteger, BigInteger> reservePair = jsonRpcClient.tokenSwapRouterGetReserves(
                        pool.getLiquidityPoolId().getLiquidityTokenId().getLiquidityTokenAddress(),
                        tokenX.getTokenStructType().toTypeTagString(), tokenY.getTokenStructType().toTypeTagString());
                pool.setTokenXReserve(reservePair.getItem1());
                pool.setTokenYReserve(reservePair.getItem2());
                updated = true;
            } catch (RuntimeException e) {
                LOG.error("Update pool reserves error. Pool Id: " + pool.getLiquidityPoolId(), e);
            }
            try {
                BigDecimal tokenXReserveInUsd = onChainService.getTokenAmountInUsd(tokenX, pool.getTokenXReserve());
                BigDecimal tokenYReserveInUsd = onChainService.getTokenAmountInUsd(tokenY, pool.getTokenYReserve());
                pool.setTokenXReserveInUsd(tokenXReserveInUsd);
                pool.setTokenYReserveInUsd(tokenYReserveInUsd);
                updated = true;
            } catch (RuntimeException e) {
                LOG.error("Update pool reserves in USD error. Pool Id: " + pool.getLiquidityPoolId(), e);
            }
            if (updated) {
                pool.setUpdatedAt(System.currentTimeMillis());
                pool.setUpdatedBy("admin");
                liquidityPoolRepository.save(pool);
            }
        }
    }


}
