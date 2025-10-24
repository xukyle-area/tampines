package com.ganten.market.common.utils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

@Slf4j
public final class HttpClientUtils {

    private static final OkHttpClient sharedClient;

    static {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder().dispatcher(dispatcher).pingInterval(20, TimeUnit.SECONDS).build();
    }

    public static String executePostRequest(String url, String jsonBody, String authorizationToken) throws IOException {
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request request =
                new Request.Builder().url(url).post(requestBody).addHeader("Authorization", authorizationToken).build();
        try (Response response = sharedClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            return Objects.requireNonNull(response.body()).string();
        }
    }

    public static String executeGetRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = sharedClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
