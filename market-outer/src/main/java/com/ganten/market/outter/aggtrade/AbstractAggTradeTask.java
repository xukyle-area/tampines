package com.ganten.market.outter.aggtrade;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.model.CurrencyPair;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.utils.ScheduledThreadPool;
import com.ganten.market.common.utils.TimestampUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAggTradeTask {
    private long lastExecuteTime = 0L;

    public void start() {
        // 启动调度之前，先执行一次
        this.doUpload();
        ScheduledThreadPool.scheduleAtFixedRate(this::uploadWithTimecheck, Constants.ZERO,
                TimestampUtils.MILLIS_OF_ONE_HOUR, TimeUnit.MILLISECONDS);
    }

    private void uploadWithTimecheck() {
        log.info("AggTradeTask: lastExecuteTime:{}", lastExecuteTime);
        // 判断今天的任务是否已经执行过：执行时间在每天的 1.AM 点之后
        if (TimestampUtils.midnightTimestampToday() + TimestampUtils.MILLIS_OF_ONE_DAY < lastExecuteTime) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("agg trade upload start.");
        this.doUpload();
    }

    private void doUpload() {
        Set<CurrencyPair> pairSet = new HashSet<>();
        for (Contract values : Contract.values()) {
            pairSet.add(values.toCurrencyPair());
        }
        for (CurrencyPair pair : pairSet) {
            String date = TimestampUtils.utcDateBefore(1);
            try {
                String url = this.constructUrl(pair.getSymbol(), date);
                String path = this.generatePath(pair.getSymbol(), date);
                this.fetchFileAndUpload(url, path);
            } catch (Exception e) {
                log.error("Failed to upload agg trade for symbol: {}, date: {}, error: {}", pair.getSymbol(), date,
                        e.getMessage(), e);
            }
        }
        lastExecuteTime = System.currentTimeMillis();
    }

    public void fetchFileAndUpload(String url, String path) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Invalid URL: {}", url);
                return;
            }

            long contentLength = connection.getContentLengthLong();
            if (contentLength <= 0) {
                log.error("Invalid content length for URL: {}", url);
                return;
            }
        } catch (Exception e) {
            log.error("Error streaming CSV to S3 for url: {}", url, e);
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String generatePath(String symbol, String date) {
        return String.format("thirdparty/%s/aggTrades/%s/%s_%s.csv", this.getMarket().name(), date, symbol, date);
    }

    protected abstract String constructUrl(String symbol, String date);

    protected abstract Market getMarket();
}
