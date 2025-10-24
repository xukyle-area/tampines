package com.ganten.market.outter.socket.cryptocom;

import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import com.ganten.market.common.constants.Constants;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.model.RealTimeQuote;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.utils.JsonUtils;
import com.ganten.market.outter.socket.BaseSocketClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoSocketClient extends BaseSocketClient {

    private long id;

    public CryptoSocketClient() throws URISyntaxException {
        super(Constants.CRYPTO_URL);
        id = 1L;
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("public/heartbeat")) {
            CryptoRequest request = JsonUtils.fromJson(message, CryptoRequest.class);
            request.setMethod("public/respond-heartbeat");
            request.setNonce(System.currentTimeMillis());
            this.send(JsonUtils.toJson(request));
        } else {
            super.onMessage(message);
        }
    }

    @Override
    protected Consumer<String> getApiCallback() {
        return text -> {
            try {
                CryptoEvent cryptoEvent = JsonUtils.fromJson(text, CryptoEvent.class);
                CryptoEvent.Dat data = cryptoEvent.getResult().getData()[0];
                String symbol = cryptoEvent.getResult().getSubscription();
                Long contractId = Contract.getContractIdBySymbol(symbol.split("ticker.")[1].replace("_", ""));
                if (Objects.isNull(contractId)) {
                    log.error("Can not find contractId for symbol:{}", symbol);
                    return;
                }
                RealTimeQuote realTimeQuote = new RealTimeQuote(System.currentTimeMillis(), contractId,
                        Market.CRYPTO_COM, data.getLast(), data.getAsk(), data.getBid());
                log.info("sinking tick to redis.{}", realTimeQuote);
                redisWriter.updateRealTimeQuote(realTimeQuote);
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected String buildSubscription(List<Contract> symbols) {
        List<String> channels = new ArrayList<>();
        for (Contract s : symbols) {
            channels.add("ticker." + s.getBase() + "_" + s.getQuote());
        }
        Map<String, Object> channelsMap = Collections.singletonMap(Constants.CRYPTO_CHANNELS, channels);
        CryptoRequest request =
                new CryptoRequest(id++, Constants.CRYPTO_COM_SUBSCRIBE, channelsMap, System.currentTimeMillis());
        return JsonUtils.toJson(request);
    }
}
