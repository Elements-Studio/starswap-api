package dev.aptos.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class NodeApiUtils {
    private NodeApiUtils() {
    }

    public static <T> List<T> getEvents(String baseUrl, String accountAddress,
                                        String eventHandleStruct, String eventHandleFieldName) throws IOException {
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("accounts")
                .addPathSegment(accountAddress)
                .addPathSegment("events")
                .addPathSegment(eventHandleStruct)
                .addPathSegment(eventHandleFieldName)
                .build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        List<T> events;
        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            events = objectMapper.readValue(response.body().string(),
                    new TypeReference<List<T>>() {
                    });
        }
        return events;
    }

}
