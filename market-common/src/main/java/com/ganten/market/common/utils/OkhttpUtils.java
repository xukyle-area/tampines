package com.ganten.market.common.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class OkhttpUtils {

    private static final OkHttpClient sharedClient;

    static {
        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder().dispatcher(dispatcher).pingInterval(20, TimeUnit.SECONDS).build();
    }

    public static ResponseBody get(String url) {
        Request request = new Request.Builder().get().url(url).build();
        Call call = sharedClient.newCall(request);

        try {
            Response response = call.execute();
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException("http get method exception, url: " + url, e);
        }
    }
}
