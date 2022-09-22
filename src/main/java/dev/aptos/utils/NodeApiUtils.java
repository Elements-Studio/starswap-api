package dev.aptos.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novi.serde.SerializationError;
import dev.aptos.bean.*;
import dev.aptos.types.SignedTransaction;
import okhttp3.*;
import okio.ByteString;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeApiUtils {
    private static final long DEFAULT_MAX_GAS_AMOUNT = 2000L;

    private NodeApiUtils() {
    }

    public static byte[] rawTransactionToSign(byte[] rawTransaction) {
        return com.google.common.primitives.Bytes
                .concat(HashUtils.hashWithAptosPrefix("RawTransaction"), rawTransaction);
    }

//    public static byte[] hashTransaction(byte[] signedTransaction) {
//        byte[] bytesToHash = com.google.common.primitives.Bytes
//                .concat(HashUtils.hashWithAptosPrefix("SignedTransaction"), signedTransaction);
//        return HashUtils.sha3Hash(bytesToHash);
//    }

    public static SubmitTransactionRequest toSubmitTransactionRequest(EncodeSubmissionRequest encodeSubmissionRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(encodeSubmissionRequest, new TypeReference<Map<String, Object>>() {
        });
        return objectMapper.convertValue(map, SubmitTransactionRequest.class);
    }

    public static LedgerInfo getLedgerInfo(String baseUrl) throws IOException {
        HttpUrl url = HttpUrl.parse(baseUrl);
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, LedgerInfo.class);
        }
    }

    /**
     * Get transaction by hash.
     *
     * @return If the transaction is not found, return null.
     * @throws IOException
     */
    public static Transaction getTransactionByHash(String baseUrl, String hash) throws IOException {
        Request request = newGetTransactionByHashRequest(baseUrl, hash);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 404) {
                return null;
            }
            if (response.code() >= 400) {
                throw new IOException("Failed to get transaction by hash: " + response.body().string());
            }
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, Transaction.class);
        }
    }

    public static boolean isTransactionPending(String baseUrl, String hash) throws IOException {
        Transaction transaction = getTransactionByHash(baseUrl, hash);
        return transaction == null
                || Transaction.TYPE_PENDING_TRANSACTION.equals(transaction.getType());
    }

    public static void waitForTransaction(String baseUrl, String hash) throws IOException {
        while (isTransactionPending(baseUrl, hash)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Transaction transaction = getTransactionByHash(baseUrl, hash);
        if (!(transaction.getSuccess() != null && transaction.getSuccess())) {
            throw new IOException("Transaction is not success.");
        }
    }

    public static Transaction getTransactionByVersion(String baseUrl, String version) throws IOException {
        Request request = newGetTransactionByVersionRequest(baseUrl, version);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, Transaction.class);
        }
    }

    public static List<Transaction> getAccountTransactions(String baseUrl, String address, Long start, Integer limit) throws IOException {
        Request request = newGetAccountTransactionsRequest(baseUrl, address, start, limit);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, new TypeReference<List<Transaction>>() {
            });
        }
    }


    public static Block getBlocksByHeight(String baseUrl, String height, Boolean withTransactions) throws IOException {
        Request request = newGetBlocksByHeightRequest(baseUrl, height, withTransactions);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, Block.class);
        }
    }

    public static Block getBlocksByVersion(String baseUrl, String version, Boolean withTransactions) throws IOException {
        Request request = newGetBlocksByVersionRequest(baseUrl, version, withTransactions);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, Block.class);
        }
    }


    public static List<Event<?>> getEvents(String baseUrl, String accountAddress,
                                           String eventHandleStruct, String eventHandleFieldName,
                                           Long start, Integer limit) throws IOException {
        Request request = newGetEventsRequest(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, start, limit);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(),
                    new TypeReference<List<Event<?>>>() {
                    });
        }
    }

    public static <TData> List<Event<TData>> getEvents(String baseUrl, String accountAddress,
                                                       String eventHandleStruct, String eventHandleFieldName,
                                                       Class<TData> eventDataType, Long start, Integer limit) throws IOException {
        Request request = newGetEventsRequest(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName, start, limit);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            return readEventList(response.body().string(), eventDataType);
        }
    }

    /**
     * Get events by event key.
     */
    public static <TData> List<Event<TData>> getEvents(String baseUrl, String eventKey,
                                                       Class<TData> eventDataType,
                                                       Long start, Integer limit) throws IOException {
        Request request = newGetEventsRequest(baseUrl, eventKey, start, limit);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            return readEventList(response.body().string(), eventDataType);
        }
    }

    private static <TData> List<Event<TData>> readEventList(String json, Class<TData> eventDataType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> mapList = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        return mapList.stream().map(map -> (Event<TData>) objectMapper.convertValue(map,
                        objectMapper.getTypeFactory().constructParametricType(Event.class, eventDataType)))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves high level information about an account such as its sequence number and authentication key.
     */
    public static Account getAccount(String baseUrl, String accountAddress) {
        Request request = newGetAccountRequest(baseUrl, accountAddress);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(), Account.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BigInteger getAccountBalance(String baseUrl, String accountAddress) throws IOException {
        String resourceType = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        AccountResource<Map> accountResource = getAccountResource(baseUrl, accountAddress, resourceType,
                Map.class, null);
        return new BigInteger(((Map) accountResource.getData().get("coin")).get("value").toString());
    }

    public static <TData> AccountResource<TData> getAccountResource(String baseUrl, String accountAddress, String resourceType,
                                                                    Class<TData> dataType, String ledgerVersion) throws IOException {
        Request request = newGetAccountResourceRequest(baseUrl, accountAddress, resourceType, ledgerVersion);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(),
                    objectMapper.getTypeFactory().constructParametricType(AccountResource.class, dataType));
        }
    }

    public static List<AccountResource<Object>> getAccountResources(String baseUrl, String accountAddress,
                                                                    String ledgerVersion) throws IOException {
        Request request = newGetAccountResourcesRequest(baseUrl, accountAddress, ledgerVersion);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, AccountResource.class));
        }
    }


    public static <T> T getTableItem(String baseUrl, String tableHandle, String keyType, String valueType, Object key,
                                     Class<T> valueJavaType, String ledgerVersion) throws IOException {
        Request request = newGetTableItemRequest(baseUrl, tableHandle, keyType, valueType, key, ledgerVersion);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(), valueJavaType);
        }
    }

    public static GasEstimation estimateGasPrice(String baseUrl) throws IOException {
        Request request = newEstimateGasPriceRequest(baseUrl);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(), GasEstimation.class);
        }
    }

    public static Transaction submitBcsTransaction(String baseUrl, SignedTransaction signedTransaction) throws IOException, SerializationError {
        Request httpRequest = newSubmitBcsTransactionHttpRequest(baseUrl, signedTransaction);
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(httpRequest).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(), Transaction.class);
        }
    }

    public static Transaction submitTransaction(String baseUrl, SubmitTransactionRequest submitTransactionRequest) throws IOException {
        Request httpRequest = newSubmitTransactionHttpRequest(baseUrl, submitTransactionRequest);
        //HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        //logInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder()
                //        .addNetworkInterceptor(logInterceptor)
                .build();
        try (Response response = client.newCall(httpRequest).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(), Transaction.class);
        }
    }

    //    public static class HttpLogger implements HttpLoggingInterceptor.Logger {
    //        @Override
    //        public void log(String message) {
    //            System.out.println("HttpLogInfo\t" + message);
    //        }
    //    }

    public static String encodeSubmission(String baseUrl, EncodeSubmissionRequest r) throws IOException {
        Request httpRequest = newEncodeSubmissionHttpRequest(baseUrl, r);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(httpRequest).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(), String.class);
        }
    }

    public static EncodeSubmissionRequest newEncodeSubmissionRequest(String baseUrl, String sender, Long expirationTimestampSecs, TransactionPayload payload, Long maxGasAmount) throws IOException {
        EncodeSubmissionRequest r = new EncodeSubmissionRequest();
        r.setSender(sender);
        r.setExpirationTimestampSecs(expirationTimestampSecs.toString());
        r.setMaxGasAmount("" + (maxGasAmount != null ? maxGasAmount : DEFAULT_MAX_GAS_AMOUNT));//todo
        r.setSequenceNumber(getAccountSequenceNumber(baseUrl, sender));
        r.setGasUnitPrice(estimateGasPrice(baseUrl).getGasEstimate().toString());
        r.setPayload(payload);
        return r;
    }

    private static Request newEncodeSubmissionHttpRequest(String baseUrl, EncodeSubmissionRequest request) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("encode_submission");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), ByteString.encodeUtf8(json));
        return new Request.Builder().url(url).post(body).build();
    }

    private static Request newSubmitTransactionHttpRequest(String baseUrl, SubmitTransactionRequest request) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions");
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), ByteString.encodeUtf8(json));
        return new Request.Builder().url(url).post(body)
                //.header("Content-Type", "application/json")
                .build();
    }

    private static Request newSubmitBcsTransactionHttpRequest(String baseUrl, SignedTransaction signedTransaction) throws SerializationError {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions");
        HttpUrl url = builder.build();
        RequestBody body = RequestBody.create(MediaType.parse("application/x.aptos.signed_transaction+bcs"),
                signedTransaction.bcsSerialize());
        return new Request.Builder().url(url).post(body)
                .build();
    }

    private static String getAccountSequenceNumber(String baseUrl, String sender) {
        return getAccount(baseUrl, sender).getSequenceNumber();
    }

    private static Request newEstimateGasPriceRequest(String baseUrl) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("estimate_gas_price");
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetTableItemRequest(String baseUrl, String tableHandle,
                                                  String keyType, String valueType, Object key, String ledgerVersion) throws JsonProcessingException {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("tables")
                .addPathSegment(tableHandle)
                .addPathSegment("item");
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("key_type", keyType);
        bodyMap.put("value_type", valueType);
        bodyMap.put("key", key);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(bodyMap));
        return new Request.Builder().url(url).post(body).build();
    }

    private static Request newGetAccountResourceRequest(String baseUrl, String accountAddress, String resourceType, String ledgerVersion) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("resource")
                .addPathSegment(resourceType);
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newGetAccountResourcesRequest(String baseUrl, String accountAddress, String ledgerVersion) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("resources");
        if (ledgerVersion != null) {
            builder.addQueryParameter("ledger_version", ledgerVersion);
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountRequest(String baseUrl, String accountAddress) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetEventsRequest(String baseUrl, String accountAddress,
                                               String eventHandleStruct, String eventHandleFieldName,
                                               Long start, Integer limit) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("events")
                .addPathSegment(eventHandleStruct)
                .addPathSegment(eventHandleFieldName);
        if (start != null) {
            builder.addQueryParameter("start", start.toString());
        }
        if (limit != null) {
            builder.addQueryParameter("limit", limit.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetEventsRequest(String baseUrl, String eventKey, Long start, Integer limit) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("events")
                .addPathSegment(eventKey);
        if (start != null) {
            builder.addQueryParameter("start", start.toString());
        }
        if (limit != null) {
            builder.addQueryParameter("limit", limit.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newGetTransactionByHashRequest(String baseUrl, String hash) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_hash")
                .addPathSegment(hash);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetTransactionByVersionRequest(String baseUrl, String version) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_version")
                .addPathSegment(version);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountTransactionsRequest(String baseUrl, String address, Long start, Integer limit) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(address)
                .addPathSegment("transactions");
        if (start != null) {
            builder.addQueryParameter("start", start.toString());
        }
        if (limit != null) {
            builder.addQueryParameter("limit", limit.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newGetBlocksByHeightRequest(String baseUrl, String height, Boolean withTransactions) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("blocks")
                .addPathSegment("by_height")
                .addPathSegment(height);
        if (withTransactions != null) {
            builder.addQueryParameter("with_transactions", withTransactions.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }


    private static Request newGetBlocksByVersionRequest(String baseUrl, String height, Boolean withTransactions) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("blocks")
                .addPathSegment("by_version")
                .addPathSegment(height);
        if (withTransactions != null) {
            builder.addQueryParameter("with_transactions", withTransactions.toString());
        }
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }
}
