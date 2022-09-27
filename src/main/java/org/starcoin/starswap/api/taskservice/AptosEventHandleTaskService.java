package org.starcoin.starswap.api.taskservice;

import dev.aptos.bean.Event;
import dev.aptos.utils.NodeApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.api.data.repo.AptosEventHandleRepository;
import org.starcoin.starswap.api.service.LiquidityAccountService;
import org.starcoin.starswap.api.service.TokenService;
import org.starcoin.starswap.aptos.bean.AddLiquidityEvent;
import org.starcoin.utils.HexUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@ConditionalOnProperty(value = "aptos.enabled", havingValue = "true", matchIfMissing = false)
public class AptosEventHandleTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(AptosEventHandleTaskService.class);

    private final Map<String, Pair<Function<AptosEventHandle, List>, BiConsumer<AptosEventHandle, Event>>> eventFetcherAndHandlerMap = new LinkedHashMap<>();

    private final AptosEventHandleRepository aptosEventHandleRepository;


    private final String aptosNodeApiBaseUrl;

    private final int eventFetchLimit = 2; //todo update to larger value

    private final TokenService tokenService;
    private final LiquidityAccountService liquidityAccountService;

    public AptosEventHandleTaskService(
            @Value("${aptos.node-api.base-url}") String aptosNodeApiBaseUrl,
            @Autowired TokenService tokenService,
            @Autowired AptosEventHandleRepository aptosEventHandleRepository,
            @Autowired LiquidityAccountService liquidityAccountService) {
        this.aptosNodeApiBaseUrl = aptosNodeApiBaseUrl;
        this.tokenService = tokenService;
        this.aptosEventHandleRepository = aptosEventHandleRepository;
        this.liquidityAccountService = liquidityAccountService;
        initEventFetcherMap();
    }

    private void initEventFetcherMap() {
        eventFetcherAndHandlerMap.put("AddLiquidityEvent", new Pair<>(
                h -> {
                    AptosEventHandleId hId = h.getAptosEventHandleId();
                    try {
                        return NodeApiUtils.getEventsByEventHandle(aptosNodeApiBaseUrl,
                                hId.getAccountAddress(), hId.getEventHandleStruct(), hId.getEventHandleFieldName(),
                                AddLiquidityEvent.class,
                                h.getNextSequenceNumber() != null ? h.getNextSequenceNumber().longValue() : 0,
                                this.eventFetchLimit);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (h, e) -> {
                    Event<AddLiquidityEvent> event = (Event<AddLiquidityEvent>) e;
                    AddLiquidityEvent addLiquidityEvent = event.getData();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Handle Aptos AddLiquidityEvent: {}", addLiquidityEvent);
                    }
                    if (false) { //todo do real job...
                        handleAddLiquidityEvent(h, addLiquidityEvent);
                    }
                    h.setNextSequenceNumber(new BigInteger(event.getSequenceNumber()).add(BigInteger.ONE));
                    aptosEventHandleRepository.save(h);
                }
        ));
//        // this a test entry, remove it later
//        eventFetcherAndHandlerMap.put("HelloBlockchainMessageChangeEvent", new Pair<>(
//                h -> {
//                    AptosEventHandleId hId = h.getAptosEventHandleId();
//                    try {
//                        return NodeApiUtils.getEventsByEventHandle(aptosNodeApiBaseUrl,
//                                hId.getAccountAddress(), hId.getEventHandleStruct(), hId.getEventHandleFieldName(),
//                                HelloBlockchainMessageChangeEvent.class,
//                                h.getNextSequenceNumber() != null ? h.getNextSequenceNumber().longValue() : 0,
//                                this.eventFetchLimit);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                },
//                (h, e) -> {
//                    Event<HelloBlockchainMessageChangeEvent> event = (Event<HelloBlockchainMessageChangeEvent>) e;
//                    LOG.info("HelloBlockchainMessageChangeEvent: {}", event);
//                    h.setNextSequenceNumber(new BigInteger(event.getSequenceNumber()).add(BigInteger.ONE));
//                    aptosEventHandleRepository.save(h);
//                }
//        ));
    }

    private void handleAddLiquidityEvent(AptosEventHandle h, AddLiquidityEvent addLiquidityEvent) {
        String xTokenTypeAddress = addLiquidityEvent.getXTypeInfo().getAccountAddress();
        String xTokenTypeModule = HexUtils.hexToString(addLiquidityEvent.getYTypeInfo().getModuleName());
        String xTokenTypeName = HexUtils.hexToString(addLiquidityEvent.getXTypeInfo().getStructName());
//        System.out.println(xTokenTypeAddress);
//        System.out.println(xTokenTypeModule);
//        System.out.println(xTokenTypeName);
        String yTokenTypeAddress = addLiquidityEvent.getYTypeInfo().getAccountAddress();
        String yTokenTypeModule = HexUtils.hexToString(addLiquidityEvent.getYTypeInfo().getModuleName());
        String yTokenTypeName = HexUtils.hexToString(addLiquidityEvent.getYTypeInfo().getStructName());
//        System.out.println(yTokenTypeAddress);
//        System.out.println(yTokenTypeModule);
//        System.out.println(yTokenTypeName);
        String accountAddress = addLiquidityEvent.getSigner();
        //BigInteger liquidity = new BigInteger(addLiquidityEvent.getLiquidity());

        Token xToken = tokenService.getTokenByStructType(xTokenTypeAddress, xTokenTypeModule, xTokenTypeName);
        if (xToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + xTokenTypeName);
        }
        Token yToken = tokenService.getTokenByStructType(yTokenTypeAddress, yTokenTypeModule, yTokenTypeName);
        if (yToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + yTokenTypeName);
        }
        String liquidityTokenAddress = h.getAptosEventHandleId().getAccountAddress();
        String liquidityPollAddress = h.getAptosEventHandleId().getAccountAddress();

        LiquidityAccountId liquidityAccountId = new LiquidityAccountId(accountAddress,
                new LiquidityPoolId(
                        new LiquidityTokenId(xToken.getTokenId(), yToken.getTokenId(), liquidityTokenAddress),
                        liquidityPollAddress));
        this.liquidityAccountService.activeLiquidityAccount(liquidityAccountId);

    }

    @Scheduled(fixedDelayString = "${aptos.event-handle.fixed-delay}")
    public void task() {
        // System.out.println("To handle aptos event by scheduled aptos.event-handle.fixed-delay.");
        List<AptosEventHandle> handles = aptosEventHandleRepository.findAll();
        for (AptosEventHandle h : handles) {
            Pair<Function<AptosEventHandle, List>, BiConsumer<AptosEventHandle, Event>> p = eventFetcherAndHandlerMap.get(h.getEventDataType());
            p.getItem1().apply(h).forEach(
                    e -> {
                        p.getItem2().accept(h, (Event) e);
                    }
            );
        }
    }

//    // this is a test class, to be removed.
//    public static class HelloBlockchainMessageChangeEvent {
//        public String from_message;
//        public String to_message;
//
//        @Override
//        public String toString() {
//            return "HelloBlockchainMessageChangeEvent{" +
//                    "from_message='" + from_message + '\'' +
//                    ", to_message='" + to_message + '\'' +
//                    '}';
//        }
//    }

}
