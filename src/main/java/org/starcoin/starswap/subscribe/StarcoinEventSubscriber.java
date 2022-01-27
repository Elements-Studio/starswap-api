package org.starcoin.starswap.subscribe;

import io.reactivex.Flowable;
import org.starcoin.bean.EventNotification;
import org.starcoin.bean.Kind;
import org.starcoin.starswap.api.service.StarcoinEventFilter;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthSubscribe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StarcoinEventSubscriber {


    private final StarcoinEventFilter starcoinEventFilter;
    private final Web3jService web3jService;

    public StarcoinEventSubscriber(Web3jService web3jService, StarcoinEventFilter starcoinEventFilter) {
        this.web3jService = web3jService;
        this.starcoinEventFilter = starcoinEventFilter;
    }

    private Map<String, Object> createEventFilterMap() {
        Map<String, Object> eventFilter = new HashMap<>();
        eventFilter.put("addr", starcoinEventFilter.getFromAddress());
        eventFilter.put("type_tags", Arrays.asList(starcoinEventFilter.getEventTypeTags()));
        //eventFilter.put("decode", true);
        return eventFilter;
    }

    public Flowable<EventNotification> eventNotificationFlowable() {
        Map<String, Object> eventFilter = createEventFilterMap();
        return web3jService.subscribe(
                new Request<>(
                        "starcoin_subscribe",
                        Arrays.asList(Kind.Events, eventFilter),
                        web3jService,
                        EthSubscribe.class),
                "starcoin_unsubscribe",
                EventNotification.class);
    }


}
