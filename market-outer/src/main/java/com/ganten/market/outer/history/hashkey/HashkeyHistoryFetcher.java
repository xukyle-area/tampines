package com.ganten.market.outer.history.hashkey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganten.market.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Hashkey历史K线数据获取器
 */
@Slf4j
public class HashkeyHistoryFetcher {

    private static final String BASE_URL = "https://api-pro.hashkey.com";
    private static final String KLINES_ENDPOINT = "/quote/v1/klines";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取K线数据
     *
     * @param symbol   交易对符号，如 BTCUSD
     * @param interval K线间隔，如 1m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 12h, 1d, 1w
     * @param limit    返回数量限制
     * @return K线数据列表
     */
    public static List<HashkeyKline> fetchKlines(String symbol, String interval, int limit) throws IOException {
        return fetchKlines(symbol, interval, limit, null, null);
    }

    /**
     * 获取K线数据（带时间范围）
     *
     * @param symbol    交易对符号，如 BTCUSD
     * @param interval  K线间隔，如 1m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 12h, 1d, 1w
     * @param limit     返回数量限制
     * @param startTime 开始时间戳（毫秒），可为null
     * @param endTime   结束时间戳（毫秒），可为null
     * @return K线数据列表
     */
    public static List<HashkeyKline> fetchKlines(String symbol, String interval, int limit, Long startTime,
            Long endTime) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("interval", interval);
        params.put("limit", String.valueOf(limit));

        if (startTime != null) {
            params.put("startTime", String.valueOf(startTime));
        }
        if (endTime != null) {
            params.put("endTime", String.valueOf(endTime));
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");

        String url = BASE_URL + KLINES_ENDPOINT;
        String response = HttpUtils.getWithParams(url, params, headers);

        log.debug("Hashkey klines response: {}", response);

        return parseKlinesResponse(response, symbol, interval);
    }

    /**
     * 解析K线响应数据
     */
    private static List<HashkeyKline> parseKlinesResponse(String response, String symbol, String interval)
            throws IOException {
        List<List<Object>> rawData = objectMapper.readValue(response, new TypeReference<List<List<Object>>>() {});
        List<HashkeyKline> klines = new ArrayList<>();

        for (List<Object> item : rawData) {
            Object[] arr = item.toArray();
            HashkeyKline kline = HashkeyKline.fromArray(arr);
            kline.setSymbol(symbol);
            kline.setInterval(interval);
            klines.add(kline);
        }

        return klines;
    }
}
