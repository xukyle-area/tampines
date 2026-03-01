package com.ganten.market.outer.history.cryptocom;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganten.market.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Crypto.com历史K线数据获取器
 */
@Slf4j
public class CryptocomHistoryFetcher {

    private static final String BASE_URL = "https://api.crypto.com/exchange/v1/public";
    private static final String KLINES_ENDPOINT = "/get-candlestick";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取K线数据
     *
     * @param instrumentName 交易对名称，如 BTC_USDT
     * @param interval       K线间隔：1m, 5m, 15m, 30m, 1h, 4h, 6h, 12h, 1D, 7D, 14D, 1M
     * @param count          返回数量限制（最大300）
     * @return K线数据列表
     */
    public static List<CryptocomKline> fetchKlines(String instrumentName, String interval, int count)
            throws IOException {
        return fetchKlines(instrumentName, interval, count, null, null);
    }

    /**
     * 获取K线数据（带时间范围）
     *
     * @param instrumentName 交易对名称，如 BTC_USDT
     * @param interval       K线间隔：1m, 5m, 15m, 30m, 1h, 4h, 6h, 12h, 1D, 7D, 14D, 1M
     * @param count          返回数量限制（最大300）
     * @param startTs        开始时间戳（毫秒），可为null
     * @param endTs          结束时间戳（毫秒），可为null
     * @return K线数据列表
     */
    public static List<CryptocomKline> fetchKlines(String instrumentName, String interval, int count, Long startTs,
            Long endTs) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("instrument_name", instrumentName);
        params.put("timeframe", interval);
        params.put("count", String.valueOf(Math.min(count, 300)));

        if (startTs != null) {
            params.put("start_ts", String.valueOf(startTs));
        }
        if (endTs != null) {
            params.put("end_ts", String.valueOf(endTs));
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");

        String url = BASE_URL + KLINES_ENDPOINT;
        String response = HttpUtils.getWithParams(url, params, headers);

        log.debug("Crypto.com klines response: {}", response);

        return parseKlinesResponse(response, instrumentName, interval);
    }

    /**
     * 解析K线响应数据
     */
    private static List<CryptocomKline> parseKlinesResponse(String response, String instrumentName, String interval)
            throws IOException {
        JsonNode root = objectMapper.readTree(response);

        // 检查响应码
        int code = root.path("code").asInt(-1);
        if (code != 0) {
            throw new IOException("Crypto.com API error, code: " + code);
        }

        JsonNode dataArray = root.path("result").path("data");
        List<CryptocomKline> klines = new ArrayList<>();

        for (JsonNode item : dataArray) {
            CryptocomKline kline = CryptocomKline.builder().openTime(item.path("t").asLong())
                    .openPrice(new BigDecimal(item.path("o").asText()))
                    .highPrice(new BigDecimal(item.path("h").asText()))
                    .lowPrice(new BigDecimal(item.path("l").asText()))
                    .closePrice(new BigDecimal(item.path("c").asText())).volume(new BigDecimal(item.path("v").asText()))
                    .symbol(instrumentName).interval(interval).build();
            klines.add(kline);
        }

        return klines;
    }

    /**
     * 测试入口
     */
    public static void main(String[] args) {
        try {
            List<CryptocomKline> klines = fetchKlines("BTC_USDT", "1D", 3);
            for (CryptocomKline kline : klines) {
                System.out.println(kline);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
