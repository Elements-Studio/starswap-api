package dev.aptos.bean;

import com.novi.bcs.BcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.SerializationError;
import dev.aptos.types.*;
import dev.aptos.utils.NodeApiUtils;
import org.starcoin.starswap.api.utils.SignatureUtils;
import org.starcoin.utils.HexUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class EventTests {

    public static void main(String[] args) throws IOException {
        String baseUrl = "https://fullnode.devnet.aptoslabs.com/v1";
        String accountAddress = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14";
        String eventHandleStruct = "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageHolder";
        String eventHandleFieldName = "message_change_events";
        //byte chainId = 32;
        long maxGasAmount = 4003243L;
        long expirationTimestampSecs = System.currentTimeMillis() / 1000L + 600;
        ChainId chainId = new ChainId((byte) 32);

//        LedgerInfo ledgerInfo = NodeApiUtils.getLedgerInfo(baseUrl);
//        System.out.println(ledgerInfo);
//        if (true) return;

//        java.math.BigInteger balance = NodeApiUtils.getAccountBalance(baseUrl, accountAddress + "1");
//        System.out.println(balance);
//        if (true) return;

//        List<Transaction> transactions = NodeApiUtils.getAccountTransactions(baseUrl, accountAddress, null, 100);
//        System.out.println(transactions);
        //if (true) return;

        TransactionPayload transactionPayload = new TransactionPayload();
        transactionPayload.setType(TransactionPayload.TYPE_ENTRY_FUNCTION_PAYLOAD);
        transactionPayload.setFunction("0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::set_message");
        List<Object> transactionArgs = Collections.singletonList("hello world!");
        transactionPayload.setArguments(transactionArgs);
        //transactionPayload.setTypeArguments();
        EncodeSubmissionRequest encodeSubmissionRequest = NodeApiUtils.newEncodeSubmissionRequest(baseUrl, accountAddress,
                expirationTimestampSecs, transactionPayload, maxGasAmount, null, null);
        String toSign = NodeApiUtils.encodeSubmission(baseUrl, encodeSubmissionRequest);
        System.out.println(toSign);
        //if (true) return;

        ModuleId module = new ModuleId(AccountAddress.valueOf(HexUtils.hexToByteArray(accountAddress)), new Identifier("message"));
        Identifier function = new Identifier("set_message");
        List<TypeTag> typeArgs = Collections.emptyList();
        List<Bytes> set_message_args = Collections.singletonList(encode_u8vector_argument(Bytes.valueOf("hello world!".getBytes(StandardCharsets.UTF_8))));
        EntryFunction entryFunction = new EntryFunction(module, function, typeArgs, set_message_args);
        dev.aptos.types.TransactionPayload.EntryFunction typesTransactionPayload = new dev.aptos.types.TransactionPayload.EntryFunction(entryFunction);
        RawTransaction rawTransaction = new RawTransaction(
                AccountAddress.valueOf(HexUtils.hexToByteArray(accountAddress)),
                Long.parseLong(encodeSubmissionRequest.getSequenceNumber()),
                typesTransactionPayload,
                Long.parseLong(encodeSubmissionRequest.getMaxGasAmount()),
                Long.parseLong(encodeSubmissionRequest.getGasUnitPrice()),
                Long.parseLong(encodeSubmissionRequest.getExpirationTimestampSecs()),
                chainId
        );
        try {
            byte[] rawTxnToSign = NodeApiUtils.rawTransactionToSign(rawTransaction.bcsSerialize());
            //System.out.println(HexUtils.byteArrayToHexWithPrefix(HashUtils.sha3Hash(rawTxnToSign)));
            String rawTxnToSignHex = HexUtils.byteArrayToHexWithPrefix(rawTxnToSign);
            System.out.println(rawTxnToSignHex);
            if (toSign.equals(rawTxnToSignHex)) {
                System.out.println("Node encoded toSign equals rawTxnToSignHex");
            } else {
                System.out.println("Node encoded toSign not equals rawTxnToSignHex");
                throw new RuntimeException("Node encoded toSign not equals rawTxnToSignHex");
            }
            //System.out.println(HexUtils.byteArrayToHexWithPrefix(HashUtils.sha3Hash(rawTxnToSign)));
        } catch (SerializationError e) {
            throw new RuntimeException(e);
        }
        //if (true) return;

        byte[] publicKey = HexUtils.hexToByteArray("0xa76e9dd1a2d9101de47e69e52e0232060b95cd7d80265d61c3fa25e406389b75");
        byte[] privateKey = HexUtils.hexToByteArray("0x09cc77f21e471431df54280da75749069b54bfe42e3cd2b532a1024262339090");
        byte[] signature = SignatureUtils.ed25519Sign(privateKey, HexUtils.hexToByteArray(toSign));

        boolean submitBcsTxn = false;
        Transaction submitTransactionResult;
        if (submitBcsTxn) {
            SignedUserTransaction signedTransaction = new SignedUserTransaction(rawTransaction,
                    new TransactionAuthenticator.Ed25519(
                            new Ed25519PublicKey(Bytes.valueOf(publicKey)),
                            new Ed25519Signature(Bytes.valueOf(signature))
                    ));
            try {
                System.out.println("Client got transaction_hash: " + HexUtils.byteArrayToHexWithPrefix(
                        NodeApiUtils.getTransactionHash(signedTransaction)));
                submitTransactionResult = NodeApiUtils.submitBcsTransaction(baseUrl, signedTransaction);
            } catch (SerializationError e) {
                throw new RuntimeException(e);
            }
        } else {
            Signature s = new Signature();
            s.setType(Signature.TYPE_ED25519_SIGNATURE);
            s.setPublicKey(HexUtils.byteArrayToHexWithPrefix(publicKey));
            s.setSignature(HexUtils.byteArrayToHexWithPrefix(signature));
            SubmitTransactionRequest submitTransactionRequest = NodeApiUtils.toSubmitTransactionRequest(encodeSubmissionRequest);
            submitTransactionRequest.setSignature(s);
            System.out.println(submitTransactionRequest);
//            // ////////////////////// Simulate transaction //////////////////////
//            submitTransactionRequest.getSignature().setSignature(HexUtils.byteArrayToHexWithPrefix( NodeApiUtils.ZERO_PADDED_SIGNATURE));
//            List<Transaction> simulatedTransactions = NodeApiUtils.simulateTransaction(baseUrl, submitTransactionRequest, true, true);
//            System.out.println(simulatedTransactions);
//            if (true) return;
//            // //////////////////////////////////////////////////////////////////
            submitTransactionResult = NodeApiUtils.submitTransaction(baseUrl, submitTransactionRequest);
        }
        System.out.println(submitTransactionResult);
        NodeApiUtils.waitForTransaction(baseUrl, submitTransactionResult.getHash());
        Transaction transaction = NodeApiUtils.getTransactionByHash(baseUrl, submitTransactionResult.getHash());
        System.out.println(transaction);
        System.out.println(transaction.getSuccess());
        System.out.println(transaction.getVmStatus());
        //        List<Event<?>> events_0 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, null, null);
        //        System.out.println(events_0);
        //if (true) return;

        Block block = NodeApiUtils.getBlocksByHeight(baseUrl, "1", true);
        System.out.println(block);
        Block block2 = NodeApiUtils.getBlocksByVersion(baseUrl, "1", true);
        System.out.println(block2);
        //if (true) return;

        GasEstimation gasEstimation = NodeApiUtils.estimateGasPrice(baseUrl);
        System.out.println(gasEstimation);
        //if (true) return;

//        Transaction transaction2 = NodeApiUtils.getTransactionByVersion(baseUrl, "1");
//        System.out.println(transaction2);
//        //if (true) return;

        Account account = NodeApiUtils.getAccount(baseUrl, accountAddress);
        System.out.println(account);
        //if (true) return;
        List<MoveModuleBytecode> moveModuleBytecodeList = NodeApiUtils.getAccountModules(baseUrl, accountAddress, null);
        System.out.println(moveModuleBytecodeList);
        //if (true) return;

        MoveModuleBytecode moveModuleBytecode = NodeApiUtils.getAccountModule(baseUrl, accountAddress, "message", null);
        System.out.println(moveModuleBytecode);
        //if (true) return;

//        List<Event<HelloBlockchainMessageChangeEvent>> eventList = NodeApiUtils.getEvents(baseUrl,
//                "0x04000000000000002b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14",
//                HelloBlockchainMessageChangeEvent.class, null, null);
//        System.out.println(eventList);
//        //if (true) return;

        List<AccountResource<Object>> resources = NodeApiUtils.getAccountResources(baseUrl, accountAddress, null);
        System.out.println(resources);
        //if (true) return;

//        String hex = NodeApiUtils.getTableItem(baseUrl,
//                "0xb0239bb1d99e33fd9897f219b9767fd68b7b486f1fda4628765ab91e3851b364",
//                "vector<u8>", "vector<u8>",
//                HexUtils.byteArrayToHexWithPrefix("hello".getBytes()), String.class, null);
//        System.out.println(hex);
//        System.out.println(new String(HexUtils.hexToByteArray(hex)));
//        //if (true) return;

//        AccountResource<TestTableHolder> resource = NodeApiUtils.getAccountResource(baseUrl, accountAddress,
//                "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::hello_table::TableHolder",
//                TestTableHolder.class, null);
//        System.out.println(resource.getData().getTable().getHandle());
//        //if (true) return;

        List<Event<?>> events_1 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, null, null);
        System.out.println(events_1);
        events_1.forEach(event -> {
            //java.util.LinkedHashMap cannot be cast to dev.aptos.bean.EventTests$HelloBlockchainMessageChangeEvent!
            //System.out.println(event.getData().getClass());
            System.out.println(event.getData().getClass());
        });
        List<Event<HelloBlockchainMessageChangeEvent>> events_2 = NodeApiUtils.getEvents(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, HelloBlockchainMessageChangeEvent.class, 1L, 1);
        System.out.println(events_2);

        System.out.println("Seem all Ok.");
    }

    private static Bytes encode_u8vector_argument(Bytes arg) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_bytes(arg);
            return Bytes.valueOf(s.get_bytes());
        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8vector");
        }
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
