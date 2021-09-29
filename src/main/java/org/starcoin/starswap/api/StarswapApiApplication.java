package org.starcoin.starswap.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.starcoin.starswap.api.service.HandleEventService;
import org.starcoin.starswap.api.service.OnChainService;
import org.starcoin.starswap.api.service.TokenPriceService;
import org.starcoin.starswap.subscribe.StarcoinEventSubscribeHandler;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
@EnableScheduling
public class StarswapApiApplication {

    private static final Logger LOG = LoggerFactory.getLogger(StarswapApiApplication.class);

    @Value("${starcoin.seeds}")
    private String[] seeds;

    @Value("${starcoin.network}")
    private String network;

    @Autowired
    private HandleEventService handleEventService;

    @Autowired
    private OnChainService onChainService;

    @Autowired
    private TokenPriceService tokenPriceService;

    @Value("${starcoin.event-filter.from-address}")
    private String starcoinEventFilterAddress;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62";
    @Value("${starcoin.event-filter.add-liquidity-event-type-tag}")
    private String addLiquidityEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwap::AddLiquidityEvent";
    @Value("${starcoin.event-filter.add-farm-event-type-tag}")
    private String addFarmEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::AddFarmEvent";
    @Value("${starcoin.event-filter.stake-event-type-tag}")
    private String stakeEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::StakeEvent";

    public static void main(String[] args) {
        SpringApplication.run(StarswapApiApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runEventSubscribeHandler() {
        LOG.info("EXECUTING : EventsSubscribeHandler");
        //LOG.info("es url is " + esUrl);
        for (String seed : seeds) {
            Thread handlerThread = new Thread(new StarcoinEventSubscribeHandler(seed,
                    handleEventService, starcoinEventFilterAddress, addLiquidityEventTypeTag, addFarmEventTypeTag, stakeEventTypeTag));
            handlerThread.start();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void refreshTokenScalingFactors() {
        LOG.info("Refreshing token scaling factors...");
        try {
            onChainService.refreshOffChainScalingFactors();
        } catch (RuntimeException runtimeException) {
            LOG.info("Refreshing token scaling factors error.", runtimeException);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initTokenToUsdPricePairMappings() {
        LOG.info("Initializing token to USD price pair mappings...");
        try {
            tokenPriceService.initTokenToUsdPricePairMappings();
        } catch (RuntimeException runtimeException) {
            LOG.info("Initializing token to USD price pair mappings error.", runtimeException);
        }
    }
}
