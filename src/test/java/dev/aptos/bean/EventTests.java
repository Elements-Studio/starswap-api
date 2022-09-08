package dev.aptos.bean;

import java.io.IOException;
import java.util.List;

import static dev.aptos.utils.NodeApiUtils.getEvents;

public class EventTests {

    public static void main(String[] args) throws IOException {
        String baseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String accountAddress = "2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14";
        String eventHandleStruct = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageHolder";
        String eventHandleFieldName = "message_change_events";
        List<Event<HelloBlockchainMessageChangeEvent>> events = getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName);
        System.out.println(events);
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
