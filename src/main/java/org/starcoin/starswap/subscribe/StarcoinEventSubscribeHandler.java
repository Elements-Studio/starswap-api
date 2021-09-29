package org.starcoin.starswap.subscribe;

import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starcoin.bean.Event;
import org.starcoin.bean.EventNotification;
import org.starcoin.starswap.api.service.HandleEventService;
import org.web3j.protocol.websocket.WebSocketService;

import java.net.ConnectException;

public class StarcoinEventSubscribeHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(StarcoinEventSubscribeHandler.class);

    private final String webSocketSeed;

    //private final String network;

    private final HandleEventService handleEventService;

    private final String fromAddress;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62";
    private final String addLiquidityEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwap::AddLiquidityEvent";
    private final String addFarmEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::AddFarmEvent";
    private final String stakeEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::StakeEvent";

    public StarcoinEventSubscribeHandler(String seed, //String network,
                                         HandleEventService handleEventService,
                                         String fromAddress,
                                         String addLiquidityEventTypeTag,
                                         String addFarmEventTypeTag, String stakeEventTypeTag) {
        this.webSocketSeed = seed;
        //this.network = network;
        this.handleEventService = handleEventService;
        this.fromAddress = fromAddress;
        this.addLiquidityEventTypeTag = addLiquidityEventTypeTag;
        this.addFarmEventTypeTag = addFarmEventTypeTag;
        this.stakeEventTypeTag = stakeEventTypeTag;
    }

    private String getWebSocketSeed() {
        String wsUrl = webSocketSeed;
        String wsPrefix = "ws://";
        if (!wsUrl.startsWith(wsPrefix)) {
            wsUrl = wsPrefix + wsUrl;
        }
        if (wsUrl.lastIndexOf(":") == wsUrl.indexOf(":")) {
            wsUrl = wsUrl + ":9870";
        }
        LOG.debug("Get WebSocket URL: " + wsUrl);
        return wsUrl;
    }

    @Override
    public void run() {
        try {
            WebSocketService service = new WebSocketService(getWebSocketSeed(), true);
            service.connect();
            StarcoinEventSubscriber subscriber = new StarcoinEventSubscriber(service, fromAddress, addLiquidityEventTypeTag, addFarmEventTypeTag, stakeEventTypeTag);
            Flowable<EventNotification> flowableEvents = subscriber.eventNotificationFlowable();

            for (EventNotification notification : flowableEvents.blockingIterable()) {
                if (notification.getParams() == null || notification.getParams().getResult() == null) {
                    continue;
                }
                Event event = notification.getParams().getResult();
                LOG.debug("Received event: " + event);
                handleEventService.handleEvent(event, Event.getFromAddressFromEventKey(event.getEventKey()));
                //note： 目前是从 eventKey 中获取地址
            }
        } catch (ConnectException e) {
            LOG.info("handle subscribe exception", e);
        }
    }
}
