package com.ganten.market.outter.socket.binance;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.model.RealTimeQuote;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.utils.JsonUtils;
import com.ganten.market.outter.socket.BaseSocketClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceSocketClient extends BaseSocketClient {
    private final static String SUBSCRIBE = "SUBSCRIBE";
    private final static String BINANCE_URL = "wss://stream.binance.com:443";

    private long id;

    public BinanceSocketClient() throws URISyntaxException {
        super(BINANCE_URL, Market.BINANCE);
        log.info(BINANCE_URL);
        id = 1L;
    }

    @Override
    protected Consumer<String> getApiCallback() {
        return text -> {
            try {
                BinanceEvent binanceEvent = JsonUtils.fromJson(text, BinanceEvent.class);
                BinanceTicker data = binanceEvent.getData();
                String symbol = data.getSymbol();
                Contract contract = Contract.getContractBySymbol(symbol);
                if (Objects.isNull(contract)) {
                    log.error("Can not find contractId for symbol:{}", symbol);
                    return;
                }
                RealTimeQuote realTimeQuote = new RealTimeQuote(data.getEventTime(), contract, Market.BINANCE,
                        data.getLastTradedPrice(), data.getBestAskPrice(), data.getBestBidPrice());
                log.info("sinking tick to redis.{}", realTimeQuote);
                redisWriter.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected String buildSubscription(List<Contract> symbols) {
        List<String> params = new ArrayList<>();
        for (Contract s : symbols) {
            String symbol = s.getBase() + s.getQuote();
            String x = symbol.toUpperCase() + "@ticker";
            params.add(x.toLowerCase());
        }
        BinanceRequest request = new BinanceRequest(SUBSCRIBE, params.toArray(new String[0]), id++);
        return JsonUtils.toJson(request);
    }
}


