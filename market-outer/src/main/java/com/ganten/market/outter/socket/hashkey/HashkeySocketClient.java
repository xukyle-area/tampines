package com.ganten.market.outter.socket.hashkey;

import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.model.RealTimeQuote;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.utils.JsonUtils;
import com.ganten.market.outter.socket.BaseSocketClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HashkeySocketClient extends BaseSocketClient {

    private final static String BINARY = "binary";
    private static String webSocketUrl = "wss://stream-pro.sim.hashkeydev.com/quote/ws/v1";
    private volatile long messageId = 1;

    public HashkeySocketClient() throws URISyntaxException {
        super(webSocketUrl, Market.HASHKEY);
    }

    @Override
    public void onMessage(String message) {
        try {
            if (message.contains("ping")) {
                HashkeyPing request = JsonUtils.fromJson(message, HashkeyPing.class);
                Long ping = request.getPing();
                HashkeyPong pong = new HashkeyPong();
                pong.setPong(ping);
                this.send(JsonUtils.toJson(pong));
            } else {
                super.onMessage(message);
            }
        } catch (Exception e) {
            log.error("handle message exception, message:{}", message, e);
        }
    }

    @Override
    protected Consumer<String> getApiCallback() {
        return text -> {
            try {
                HashkeyEvent hashkeyEvent = JsonUtils.fromJson(text, HashkeyEvent.class);
                if (hashkeyEvent == null || hashkeyEvent.getData() == null || hashkeyEvent.getData().length == 0) {
                    return;
                }

                Data data = hashkeyEvent.getData()[0];
                String symbol = hashkeyEvent.getSymbolName();
                Contract contract = Contract.getContractBySymbol(symbol);
                if (Objects.isNull(contract)) {
                    log.error("Can not find contractId for symbol:{}", symbol);
                    return;
                }

                RealTimeQuote realTimeQuote = new RealTimeQuote(System.currentTimeMillis(), contract, Market.HASHKEY,
                        data.getClose(), null, null);
                log.info("Hashkey real-time: {}", realTimeQuote);
                redisWriter.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected List<String> buildSubscription(List<Contract> symbols) {
        List<String> arrayList = new ArrayList<String>();
        for (Contract contract : symbols) {
            arrayList.add(buildSubscription(contract));
        }
        return arrayList;
    }


    private String buildSubscription(Contract pair) {
        HashkeyRequest request = new HashkeyRequest();
        Map<String, Object> paramsMap = Collections.singletonMap(BINARY, false);
        request.setParams(paramsMap);
        request.setSymbol(pair.getSymbol());
        request.setId(messageId++);
        return JsonUtils.toJson(request);
    }
}
