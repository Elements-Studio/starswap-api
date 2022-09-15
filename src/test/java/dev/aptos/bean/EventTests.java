package dev.aptos.bean;

import dev.aptos.utils.NodeApiUtils;

import java.io.IOException;
import java.util.List;

public class EventTests {

    public static void main(String[] args) throws IOException {
        String baseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String accountAddress = "2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14";
        String eventHandleStruct = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageHolder";
        String eventHandleFieldName = "message_change_events";
        List<Event<?>> events_1 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName);
        System.out.println(events_1);
        events_1.forEach(event -> {
            //java.util.LinkedHashMap cannot be cast to dev.aptos.bean.EventTests$HelloBlockchainMessageChangeEvent!
            //System.out.println(event.getData().getClass());
            System.out.println(event.getData().getClass());
        });
        List<Event<HelloBlockchainMessageChangeEvent>> events_2 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, HelloBlockchainMessageChangeEvent.class);
        System.out.println(events_2);

        LedgerInfo ledgerInfo = NodeApiUtils.getLedgerInfo(baseUrl);
        System.out.println(ledgerInfo);
    }


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
