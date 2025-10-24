package com.ganten.market.outter.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.outter.socket.SocketConnecter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SocketTask {

    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void binance() {
        SocketConnecter binanceConnecter = new SocketConnecter(Market.BINANCE);
        binanceConnecter.checkAndSubscribe();
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void cryptoCom() {
        SocketConnecter cryptoComConnecter = new SocketConnecter(Market.CRYPTO_COM);
        cryptoComConnecter.checkAndSubscribe();
    }
}
