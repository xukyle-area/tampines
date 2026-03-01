package com.ganten.market.outer.history.hashkey;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * K线数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class HashkeyKline {
    /**
     * K线开盘时间戳（毫秒）
     */
    private Long openTime;

    /**
     * 开盘价
     */
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    private BigDecimal lowPrice;

    /**
     * 收盘价
     */
    private BigDecimal closePrice;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * K线收盘时间戳（毫秒）
     */
    private Long closeTime;

    /**
     * 报价资产成交量
     */
    private BigDecimal quoteAssetVolume;

    /**
     * 成交笔数
     */
    private Integer numberOfTrades;

    /**
     * 主动买入基础资产成交量
     */
    private BigDecimal takerBuyBaseAssetVolume;

    /**
     * 主动买入报价资产成交量
     */
    private BigDecimal takerBuyQuoteAssetVolume;

    /**
     * 交易对符号
     */
    private String symbol;

    /**
     * K线间隔
     */
    private String interval;

    /**
     * 从Hashkey API返回的数组解析Kline对象
     * 数组格式: [openTime, open, high, low, close, volume, closeTime, quoteVolume, trades, takerBuyBase, takerBuyQuote]
     */
    public static HashkeyKline fromArray(Object[] arr) {
        if (arr == null || arr.length < 11) {
            throw new IllegalArgumentException("Invalid kline array data");
        }
        return HashkeyKline.builder().openTime(parseLong(arr[0])).openPrice(parseBigDecimal(arr[1]))
                .highPrice(parseBigDecimal(arr[2])).lowPrice(parseBigDecimal(arr[3]))
                .closePrice(parseBigDecimal(arr[4])).volume(parseBigDecimal(arr[5])).closeTime(parseLong(arr[6]))
                .quoteAssetVolume(parseBigDecimal(arr[7])).numberOfTrades(parseInteger(arr[8]))
                .takerBuyBaseAssetVolume(parseBigDecimal(arr[9])).takerBuyQuoteAssetVolume(parseBigDecimal(arr[10]))
                .build();
    }

    private static Long parseLong(Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return Long.parseLong(obj.toString());
    }

    private static Integer parseInteger(Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return Integer.parseInt(obj.toString());
    }

    private static BigDecimal parseBigDecimal(Object obj) {
        if (obj == null)
            return null;
        return new BigDecimal(obj.toString());
    }
}
