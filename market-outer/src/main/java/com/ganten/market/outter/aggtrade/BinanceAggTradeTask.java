package com.ganten.market.outter.aggtrade;

import com.ganten.market.common.pojo.Market;

public class BinanceAggTradeTask extends AbstractAggTradeTask {

    /**
     * <a href="https://data.binance.vision/data/spot/daily/aggTrades/BTCUSDT/BTCUSDT-aggTrades-2025-05-22.zip" />
     *
     * @param symbol 交易对符号，例如 BTCUSDT
     * @param date   日期，格式为 yyyy-MM-dd，例如 2025-05-22
     * @return 构造的 URL 字符串
     */
    @Override
    protected String constructUrl(String symbol, String date) {
        return "https://data.binance.vision/data/spot/daily/aggTrades/" + symbol + "/" + symbol + "-aggTrades-" + date
                + ".zip";
    }

    @Override
    protected Market getMarket() {
        return Market.BINANCE;
    }
}
