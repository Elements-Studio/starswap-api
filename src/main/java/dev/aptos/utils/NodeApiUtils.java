package dev.aptos.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aptos.bean.*;
import okhttp3.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeApiUtils {
    private NodeApiUtils() {
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

    public static Transaction getTransactionByHash(String baseUrl, String hash) throws IOException {
        Request request = newGetTransactionByHash(baseUrl, hash);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, Transaction.class);
        }
    }

    public static Transaction getTransactionByVersion(String baseUrl, String version) throws IOException {
        Request request = newGetTransactionByVersion(baseUrl, version);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, Transaction.class);
        }
    }

    public static List<Transaction> getAccountTransactions(String baseUrl, String address, Long start, Integer limit) throws IOException {
        Request request = newGetAccountTransactions(baseUrl, address, start, limit);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, new TypeReference<List<Transaction>>() {
            });
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

    public static GasEstimation estimateGasPrice(String baseUrl) {
        Request request = newEstimateGasPriceRequest(baseUrl);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body().string(), GasEstimation.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


    private static Request newGetTransactionByHash(String baseUrl, String hash) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_hash")
                .addPathSegment(hash);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetTransactionByVersion(String baseUrl, String version) {
        HttpUrl.Builder builder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("transactions")
                .addPathSegment("by_version")
                .addPathSegment(version);
        HttpUrl url = builder.build();
        return new Request.Builder().url(url).build();
    }

    private static Request newGetAccountTransactions(String baseUrl, String address, Long start, Integer limit) {
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

}
