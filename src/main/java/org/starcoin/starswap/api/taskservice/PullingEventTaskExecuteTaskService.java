package org.starcoin.starswap.api.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.bean.Event;
import org.starcoin.starswap.api.data.model.PullingEventTask;
import org.starcoin.starswap.api.data.repo.NodeHeartbeatRepository;
import org.starcoin.starswap.api.service.HandleEventService;
import org.starcoin.starswap.api.service.NodeHeartbeatService;
import org.starcoin.starswap.api.service.PullingEventTaskService;
import org.starcoin.starswap.api.utils.JsonRpcClient;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.*;

import static org.starcoin.starswap.api.data.model.PullingEventTask.PULLING_BLOCK_MAX_COUNT;

@Service
public class PullingEventTaskExecuteTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(PullingEventTaskExecuteTaskService.class);

    private final PullingEventTaskService pullingEventTaskService;
    private final HandleEventService handleEventService;
    private final JsonRpcClient jsonRpcClient;
    private final NodeHeartbeatRepository nodeHeartbeatRepository;

    private final String fromAddress;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62";
    private final String addLiquidityEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwap::AddLiquidityEvent";
    private final String addFarmEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::AddFarmEvent";
    private final String stakeEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::StakeEvent";
    private final String syrupPoolStakeEventTypeTag;

    public PullingEventTaskExecuteTaskService(
            @Autowired JsonRpcClient jsonRpcClient,
            @Autowired PullingEventTaskService pullingEventTaskService,
            @Autowired HandleEventService handleEventService,
            @Autowired NodeHeartbeatRepository nodeHeartbeatRepository,
            @Value("${starcoin.event-filter.from-address}") String fromAddress,
            @Value("${starcoin.event-filter.add-liquidity-event-type-tag}") String addLiquidityEventTypeTag,
            @Value("${starcoin.event-filter.add-farm-event-type-tag}") String addFarmEventTypeTag,
            @Value("${starcoin.event-filter.stake-event-type-tag}") String stakeEventTypeTag,
            @Value("${starcoin.event-filter.syrup-pool-stake-event-type-tag}") String syrupPoolStakeEventTypeTag) throws MalformedURLException {
        this.jsonRpcClient = jsonRpcClient;
        this.pullingEventTaskService = pullingEventTaskService;
        this.handleEventService = handleEventService;
        this.nodeHeartbeatRepository = nodeHeartbeatRepository;
        this.fromAddress = fromAddress;
        this.addLiquidityEventTypeTag = addLiquidityEventTypeTag;
        this.addFarmEventTypeTag = addFarmEventTypeTag;
        this.stakeEventTypeTag = stakeEventTypeTag;
        this.syrupPoolStakeEventTypeTag = syrupPoolStakeEventTypeTag;
    }

    @Scheduled(fixedDelayString = "${starswap.pulling-event-task-execute.fixed-delay}")
    public void task() {
        List<PullingEventTask> pullingEventTasks = pullingEventTaskService.getPullingEventTaskToProcess();
        if (pullingEventTasks == null || pullingEventTasks.isEmpty()) {
            return;
        }
        for (PullingEventTask t : pullingEventTasks) {
            executeTask(t);
        }
    }

    private void executeTask(PullingEventTask t) {
        BigInteger fromBlockNumber = t.getFromBlockNumber();
        // use a new individual nodeId to record heartbeats.
        NodeHeartbeatService nodeHeartbeatService = new NodeHeartbeatService(nodeHeartbeatRepository);
        nodeHeartbeatService.beat(t.getFromBlockNumber());
        while (fromBlockNumber.compareTo(t.getToBlockNumber()) < 0) {
            BigInteger toBlockNumber = fromBlockNumber.add(BigInteger.valueOf(PULLING_BLOCK_MAX_COUNT));
            if (toBlockNumber.compareTo(t.getToBlockNumber()) > 0) {
                toBlockNumber = t.getToBlockNumber();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("JSON RPC getting events... from block: " + fromBlockNumber + ", to block: " + toBlockNumber);
            }
            Map<String, Object> eventFilter = createEventFilterMap(fromBlockNumber, toBlockNumber);
            Event[] events = jsonRpcClient.getEvents(eventFilter);
            if (events == null) {
                break;
            }
            for (Event e : events) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Processing a event: " + e);
                }
                handleEventService.handleEvent(e, Event.getFromAddressFromEventKey(e.getEventKey()));//note：Get address from eventKey?
            }
            fromBlockNumber = toBlockNumber;
        }
        pullingEventTaskService.updateStatusDone(t);
        nodeHeartbeatService.beat(t.getToBlockNumber());
    }


    private Map<String, Object> createEventFilterMap(BigInteger fromBlockNumber, BigInteger toBlockNumber) {
        Map<String, Object> eventFilter = new HashMap<>();
        // params: `from_block`, `to_block`, `event_keys`, `addrs`, `type_tags`, `limit`.
        eventFilter.put("addrs", Collections.singletonList(fromAddress));
        eventFilter.put("type_tags", Arrays.asList(addLiquidityEventTypeTag, addFarmEventTypeTag, stakeEventTypeTag, syrupPoolStakeEventTypeTag));
        //eventFilter.put("decode", true);
        eventFilter.put("from_block", fromBlockNumber);
        eventFilter.put("to_block", toBlockNumber);
        return eventFilter;
    }
}
