package org.starcoin.starswap.api.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.bean.Event;
import org.starcoin.starswap.api.data.model.PullingEventTask;
import org.starcoin.starswap.api.data.repo.NodeHeartbeatRepository;
import org.starcoin.starswap.api.service.HandleEventService;
import org.starcoin.starswap.api.service.NodeHeartbeatService;
import org.starcoin.starswap.api.service.PullingEventTaskService;
import org.starcoin.starswap.api.service.StarcoinEventFilter;
import org.starcoin.starswap.api.utils.JsonRpcClient;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.starcoin.starswap.api.data.model.PullingEventTask.PULLING_BLOCK_MAX_COUNT;

@Service
public class PullingEventTaskExecuteTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(PullingEventTaskExecuteTaskService.class);

    private final PullingEventTaskService pullingEventTaskService;
    private final HandleEventService handleEventService;
    private final JsonRpcClient jsonRpcClient;
    private final NodeHeartbeatRepository nodeHeartbeatRepository;
    private final StarcoinEventFilter starcoinEventFilter;

    public PullingEventTaskExecuteTaskService(
            @Autowired JsonRpcClient jsonRpcClient,
            @Autowired PullingEventTaskService pullingEventTaskService,
            @Autowired HandleEventService handleEventService,
            @Autowired NodeHeartbeatRepository nodeHeartbeatRepository,
            @Autowired StarcoinEventFilter starcoinEventFilter) {
        this.jsonRpcClient = jsonRpcClient;
        this.pullingEventTaskService = pullingEventTaskService;
        this.handleEventService = handleEventService;
        this.nodeHeartbeatRepository = nodeHeartbeatRepository;
        this.starcoinEventFilter = starcoinEventFilter;
    }

    @Scheduled(fixedDelayString = "${starswap.pulling-event-task-execute.fixed-delay}")
    public void task() {
        PullingEventTask pullingEventTask = pullingEventTaskService.getPullingEventTaskToProcess();
        if (pullingEventTask == null) {
            return;
        }
        executeTask(pullingEventTask);
    }

    private void executeTask(PullingEventTask t) {
        BigInteger fromBlockNumber = t.getFromBlockNumber();
        while (fromBlockNumber.compareTo(t.getToBlockNumber()) <= 0) {
            BigInteger toBlockNumber = getFilterToBlockNumber(fromBlockNumber, t.getToBlockNumber());
            Map<String, Object> eventFilter = createEventFilterMap(fromBlockNumber, toBlockNumber);
            Event[] events = jsonRpcClient.getEvents(eventFilter);
            if (events != null) {
                for (Event e : events) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Processing a event: " + e);
                    }
                    try {
                        handleEventService.handleEvent(e, Event.getFromAddressFromEventKey(e.getEventKey()));//note：Get address from eventKey?
                    } catch (RuntimeException exception) {
                        LOG.error("Error handling event: " + e, exception);
                        throw exception;
                    }
                }
            }
            fromBlockNumber = toBlockNumber.add(BigInteger.ONE);
            try {
                pullingEventTaskService.updateProcessing(t.getFromBlockNumber());
            } catch (RuntimeException exception) {
                LOG.error("Updating processing information error: " + t, exception);
                throw exception;
            }
        }
        try {
            pullingEventTaskService.updateStatusDone(t.getFromBlockNumber());
        } catch (RuntimeException exception) {
            LOG.error("Updating status to done error: " + t, exception);
            throw exception;
        }
        try {
            // use a new individual nodeId to record heartbeats.
            NodeHeartbeatService nodeHeartbeatService = new NodeHeartbeatService(nodeHeartbeatRepository);
            nodeHeartbeatService.beat(t.getFromBlockNumber());
            nodeHeartbeatService.beat(t.getToBlockNumber());
        } catch (RuntimeException exception) {
            LOG.error("Updating node heartbeat error: " + t, exception);
            throw exception;
        }
    }

    private static BigInteger getFilterToBlockNumber(BigInteger fromBlockNumber, BigInteger maxToBlockNumber) {
        BigInteger toBlockNumber = fromBlockNumber.add(BigInteger.valueOf(PULLING_BLOCK_MAX_COUNT));
        if (toBlockNumber.compareTo(maxToBlockNumber) > 0) {
            toBlockNumber = maxToBlockNumber;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("JSON RPC getting events... from block: " + fromBlockNumber + ", to block: " + toBlockNumber);
        }
        return toBlockNumber;
    }


    private Map<String, Object> createEventFilterMap(BigInteger fromBlockNumber, BigInteger toBlockNumber) {
        Map<String, Object> eventFilter = new HashMap<>();
        // params: `from_block`, `to_block`, `event_keys`, `addrs`, `type_tags`, `limit`.
        eventFilter.put("addrs", Collections.singletonList(starcoinEventFilter.getFromAddress()));
        eventFilter.put("type_tags", Arrays.asList(starcoinEventFilter.getEventTypeTags()));
        //eventFilter.put("decode", true);
        eventFilter.put("from_block", fromBlockNumber);
        eventFilter.put("to_block", toBlockNumber);
        return eventFilter;
    }
}
