package dev.aptos.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aptos.bean.Event;
import dev.aptos.bean.LedgerInfo;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeApiUtils {
    private NodeApiUtils() {
    }

    public static LedgerInfo getLedgerInfo(String baseUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = HttpUrl.parse(baseUrl);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, LedgerInfo.class);
    }

    public static List<Event<?>> getEvents(String baseUrl, String accountAddress,
                                             String eventHandleStruct, String eventHandleFieldName) throws IOException {
        Request request = newGetEventsRequest(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName);
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
                                                       Class<TData> eventDataType) throws IOException {
        Request request = newGetEventsRequest(baseUrl, accountAddress, eventHandleStruct, eventHandleFieldName);
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> mapList = objectMapper.readValue(response.body().string(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            return mapList.stream().map(map -> (Event<TData>) objectMapper.convertValue(map,
                            objectMapper.getTypeFactory().constructParametricType(Event.class, eventDataType)))
                    .collect(Collectors.toList());
        }
    }

    private static Request newGetEventsRequest(String baseUrl, String accountAddress, String eventHandleStruct, String eventHandleFieldName) {
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("events")
                .addPathSegment(eventHandleStruct)
                .addPathSegment(eventHandleFieldName)
                .build();
        return new Request.Builder()
                .url(url)
                .build();
    }

}
