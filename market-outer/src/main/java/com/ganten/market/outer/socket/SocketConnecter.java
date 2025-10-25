package com.ganten.market.outer.socket;

import java.net.URISyntaxException;
import com.ganten.market.common.enums.Market;
import com.ganten.market.outer.socket.cryptocom.CryptoSocketClient;
import com.ganten.market.outer.socket.hashkey.HashkeySocketClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketConnecter {
    private BaseSocketClient curClient = null;
    private final Market market;

    public SocketConnecter(Market market) {
        this.market = market;
        this.checkAndSubscribe();
    }

    public void checkAndSubscribe() {
        log.info("[{}] Checking websocket connection.", market);
        if (curClient == null || !curClient.isOpen()) {
            log.info("[{}] websocket is not open, try to reconnect.", market);
            reconnect();
        }
        if (curClient == null || !curClient.isOpen()) {
            log.error("[{}] websocket is still not open after reconnecting.", market);
            return;
        }
        curClient.subscribe();
    }

    public synchronized void reconnect() {
        BaseSocketClient nextClient;
        try {
            if (market.equals(Market.HASHKEY)) {
                nextClient = new HashkeySocketClient();
            } else if (market.equals(Market.CRYPTO_COM)) {
                nextClient = new CryptoSocketClient();
            } else {
                log.error("[{}] Unsupported QuoteEnum type: {}", market);
                return;
            }
        } catch (URISyntaxException e) {
            log.error("[{}] URL format is invalid!", market, e);
            return;
        }
        log.info("[{}] build websocket client success!", market);
        try {
            if (curClient != null) {
                curClient.close();
            }
            nextClient.connect();
            curClient = nextClient;
        } catch (Exception e) {
            log.error("[{}] Error reconnecting: {}", market, e.getMessage(), e);
        }
    }
}
