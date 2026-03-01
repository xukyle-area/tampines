package com.ganten.market.outer.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ganten.market.common.enums.Market;
import com.ganten.market.outer.socket.SocketConnecter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SocketTask {

    private final SocketConnecter hashkeyConnecter = new SocketConnecter(Market.HASHKEY);
    private final SocketConnecter cryptoComConnecter = new SocketConnecter(Market.CRYPTO_COM);

    @Scheduled(fixedDelay = 90000, initialDelay = 10000)
    public void hashkey() {
        hashkeyConnecter.checkAndSubscribe();
    }

    @Scheduled(fixedDelay = 90000, initialDelay = 10000)
    public void cryptoCom() {
        cryptoComConnecter.checkAndSubscribe();
    }
}
