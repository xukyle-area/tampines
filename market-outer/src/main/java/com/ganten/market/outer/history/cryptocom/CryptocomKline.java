package com.ganten.market.outer.history.cryptocom;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Crypto.com K线数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CryptocomKline {
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
     * 交易对符号
     */
    private String symbol;

    /**
     * K线间隔
     */
    private String interval;
}
