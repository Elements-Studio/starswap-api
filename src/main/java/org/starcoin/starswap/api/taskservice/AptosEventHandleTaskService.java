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
import org.starcoin.starswap.api.data.model.AptosEventHandle;
import org.starcoin.starswap.api.data.model.AptosEventHandleId;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.repo.AptosEventHandleRepository;

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

    @Value("${aptos.node-api.base-url}")
    private String aptosNodeApiBaseUrl;

    private final int eventFetchLimit = 2;

    public AptosEventHandleTaskService(
            @Autowired AptosEventHandleRepository aptosEventHandleRepository) {
        this.aptosEventHandleRepository = aptosEventHandleRepository;
        initEventFetcherMap();
    }

    private void initEventFetcherMap() {
        //todo: this a test entry, remove it later
        eventFetcherAndHandlerMap.put("HelloBlockchainMessageChangeEvent", new Pair<>(
                h -> {
                    AptosEventHandleId hId = h.getAptosEventHandleId();
                    try {
                        return NodeApiUtils.getEvents(aptosNodeApiBaseUrl,
                                hId.getAccountAddress(), hId.getEventHandleStruct(), hId.getEventHandleFieldName(),
                                HelloBlockchainMessageChangeEvent.class,
                                h.getNextSequenceNumber() != null ? h.getNextSequenceNumber().longValue() : 0,
                                this.eventFetchLimit);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (h, e) -> {
                    Event<HelloBlockchainMessageChangeEvent> event = (Event<HelloBlockchainMessageChangeEvent>) e;
                    LOG.info("HelloBlockchainMessageChangeEvent: {}", event);
                    h.setNextSequenceNumber(new BigInteger(event.getSequenceNumber()).add(BigInteger.ONE));
                    aptosEventHandleRepository.save(h);
                }
        ));
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

    //todo: this is a test class, to be removed.
    public static class HelloBlockchainMessageChangeEvent {
        public String from_message;
        public String to_message;

        @Override
        public String toString() {
            return "HelloBlockchainMessageChangeEvent{" +
                    "from_message='" + from_message + '\'' +
                    ", to_message='" + to_message + '\'' +
                    '}';
        }
    }

}
