package com.ganten.market.common.utils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

/**
 * HTTP请求工具类，基于OkHttp
 */
public class HttpUtils {

    private static final OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");

    static {
        client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build();
    }

    /**
     * GET请求
     *
     * @param url 请求URL
     * @return 响应内容
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * GET请求，带请求头
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 响应内容
     */
    public static String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        addHeaders(builder, headers);
        Request request = builder.build();
        return execute(request);
    }

    /**
     * GET请求，带查询参数
     *
     * @param url     请求URL
     * @param params  查询参数
     * @param headers 请求头
     * @return 响应内容
     */
    public static String getWithParams(String url, Map<String, String> params, Map<String, String> headers)
            throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request.Builder builder = new Request.Builder().url(urlBuilder.build());
        addHeaders(builder, headers);
        Request request = builder.build();
        return execute(request);
    }

    /**
     * POST请求，JSON格式
     *
     * @param url  请求URL
     * @param json JSON字符串
     * @return 响应内容
     */
    public static String postJson(String url, String json) throws IOException {
        return postJson(url, json, null);
    }

    /**
     * POST请求，JSON格式，带请求头
     *
     * @param url     请求URL
     * @param json    JSON字符串
     * @param headers 请求头
     * @return 响应内容
     */
    public static String postJson(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder().url(url).post(body);
        addHeaders(builder, headers);
        Request request = builder.build();
        return execute(request);
    }

    /**
     * POST请求，表单格式
     *
     * @param url    请求URL
     * @param params 表单参数
     * @return 响应内容
     */
    public static String postForm(String url, Map<String, String> params) throws IOException {
        return postForm(url, params, null);
    }

    /**
     * POST请求，表单格式，带请求头
     *
     * @param url     请求URL
     * @param params  表单参数
     * @param headers 请求头
     * @return 响应内容
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers)
            throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request.Builder builder = new Request.Builder().url(url).post(formBuilder.build());
        addHeaders(builder, headers);
        Request request = builder.build();
        return execute(request);
    }

    /**
     * PUT请求，JSON格式
     *
     * @param url     请求URL
     * @param json    JSON字符串
     * @param headers 请求头
     * @return 响应内容
     */
    public static String putJson(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder().url(url).put(body);
        addHeaders(builder, headers);
        Request request = builder.build();
        return execute(request);
    }

    /**
     * DELETE请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 响应内容
     */
    public static String delete(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).delete();
        addHeaders(builder, headers);
        Request request = builder.build();
        return execute(request);
    }

    /**
     * 异步GET请求
     *
     * @param url      请求URL
     * @param headers  请求头
     * @param callback 回调
     */
    public static void getAsync(String url, Map<String, String> headers, Callback callback) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeaders(builder, headers);
        Request request = builder.build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 异步POST请求
     *
     * @param url      请求URL
     * @param json     JSON字符串
     * @param headers  请求头
     * @param callback 回调
     */
    public static void postJsonAsync(String url, String json, Map<String, String> headers, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder().url(url).post(body);
        addHeaders(builder, headers);
        Request request = builder.build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 添加请求头
     */
    private static void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 执行请求
     */
    private static String execute(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }

    /**
     * 获取OkHttpClient实例（用于自定义请求）
     */
    public static OkHttpClient getClient() {
        return client;
    }
}
