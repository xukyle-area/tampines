package com.ganten.market.outer.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ganten.market.outer.history.BaseHistoryTask;
import com.ganten.market.outer.history.cryptocom.CryptocomHistoryTask;
import com.ganten.market.outer.history.hashkey.HashkeyHistoryTask;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class HistoryTask {

    private BaseHistoryTask cryptocomHistoryTask = new CryptocomHistoryTask();

    private BaseHistoryTask hashkeyHistoryTask = new HashkeyHistoryTask();

    @Scheduled(fixedDelay = 90000, initialDelay = 10000)
    public void hashkey() {
        cryptocomHistoryTask.run();
    }

    @Scheduled(fixedDelay = 90000, initialDelay = 10000)
    public void cryptoCom() {
        hashkeyHistoryTask.run();
    }
}
