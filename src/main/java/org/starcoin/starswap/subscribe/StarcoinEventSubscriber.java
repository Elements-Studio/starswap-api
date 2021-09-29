package org.starcoin.starswap.subscribe;

import io.reactivex.Flowable;
import org.starcoin.bean.EventNotification;
import org.starcoin.bean.Kind;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthSubscribe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StarcoinEventSubscriber {

    private final String fromAddress;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62";
    private final String addLiquidityEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwap::AddLiquidityEvent";
    private final String addFarmEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::AddFarmEvent";
    private final String stakeEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::StakeEvent";


    private final Web3jService web3jService;

    public StarcoinEventSubscriber(Web3jService web3jService, String fromAddress, String addLiquidityEventTypeTag, String addFarmEventTypeTag, String stakeEventTypeTag) {
        this.fromAddress = fromAddress;
        this.addLiquidityEventTypeTag = addLiquidityEventTypeTag;
        this.addFarmEventTypeTag = addFarmEventTypeTag;
        this.stakeEventTypeTag = stakeEventTypeTag;
        this.web3jService = web3jService;
    }

    private Map<String, Object> createEventFilterMap() {
        Map<String, Object> eventFilter = new HashMap<>();
        eventFilter.put("addr", fromAddress);
        eventFilter.put("type_tags", Arrays.asList(addLiquidityEventTypeTag, addFarmEventTypeTag, stakeEventTypeTag));
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


//    public Flowable<PendingTransactionNotification> newPendingTransactionsNotifications() {
//        return web3jService.subscribe(
//                new Request<>(
//                        "starcoin_subscribe",
//                        Arrays.asList(Kind.PendingTxn),
//                        web3jService,
//                        EthSubscribe.class),
//                "starcoin_unsubscribe",
//                PendingTransactionNotification.class);
//    }

}
