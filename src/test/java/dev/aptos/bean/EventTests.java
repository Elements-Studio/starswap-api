package dev.aptos.bean;

import dev.aptos.utils.NodeApiUtils;
import org.starcoin.utils.HexUtils;

import java.io.IOException;
import java.util.List;

public class EventTests {

    public static void main(String[] args) throws IOException {
        String baseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String accountAddress = "2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14";
        String eventHandleStruct = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageHolder";
        String eventHandleFieldName = "message_change_events";

        List<Transaction> transactions = NodeApiUtils.getAccountTransactions(baseUrl, accountAddress, null, 100);
        System.out.println(transactions);
        if (true) return;

        Transaction transaction = NodeApiUtils.getTransactionByHash(baseUrl, "0xbcaac6583ecd9ce75ed65b1fbef6f530d4d40c57b0d6c1672d5f2584e7ca9752");
        System.out.println(transaction);
        Transaction transaction2 = NodeApiUtils.getTransactionByVersion(baseUrl, "11742804");
        System.out.println(transaction2);
        if (true) return;

        Account account = NodeApiUtils.getAccount(baseUrl, accountAddress);
        System.out.println(account);
        if (true) return;

        List<Event<HelloBlockchainMessageChangeEvent>> eventList = NodeApiUtils.getEvents(baseUrl,
                "0x04000000000000002b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14",
                HelloBlockchainMessageChangeEvent.class, null, null);
        System.out.println(eventList);
        //if (true) return;

        List<AccountResource<Object>> resources = NodeApiUtils.getAccountResources(baseUrl, accountAddress, null);
        System.out.println(resources);
        //if (true) return;

        String hex = NodeApiUtils.getTableItem(baseUrl,
                "0xb0239bb1d99e33fd9897f219b9767fd68b7b486f1fda4628765ab91e3851b364",
                "vector<u8>", "vector<u8>",
                HexUtils.byteArrayToHexWithPrefix("hello".getBytes()), String.class, null);
        System.out.println(hex);
        System.out.println(new String(HexUtils.hexToByteArray(hex)));
        //if (true) return;

        AccountResource<TestTableHolder> resource = NodeApiUtils.getAccountResource(baseUrl, accountAddress,
                "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::hello_table::TableHolder",
                TestTableHolder.class, null);
        System.out.println(resource.getData().getTable().getHandle());
        //if (true) return;

        List<Event<?>> events_1 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, null, null);
        System.out.println(events_1);
        events_1.forEach(event -> {
            //java.util.LinkedHashMap cannot be cast to dev.aptos.bean.EventTests$HelloBlockchainMessageChangeEvent!
            //System.out.println(event.getData().getClass());
            System.out.println(event.getData().getClass());
        });
        List<Event<HelloBlockchainMessageChangeEvent>> events_2 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, HelloBlockchainMessageChangeEvent.class, 1L, 1);
        System.out.println(events_2);

        LedgerInfo ledgerInfo = NodeApiUtils.getLedgerInfo(baseUrl);
        System.out.println(ledgerInfo);


    }


    public static class TestTableHolder {
        private Table table;

        public Table getTable() {
            return table;
        }

        public void setTable(Table table) {
            this.table = table;
        }

        @Override
        public String toString() {
            return "TestTableHolder{" +
                    "table=" + table +
                    '}';
        }
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
