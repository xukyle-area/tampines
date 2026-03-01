package com.ganten.market.outer.history;

import com.ganten.market.common.enums.Contract;
import com.ganten.market.outer.history.cryptocom.CryptocomHistoryTask;

public class HistoryRunner {

    private BaseHistoryTask baseHistoryTask = new CryptocomHistoryTask();

    public static void main(String[] args) {
        HistoryRunner historyRunner = new HistoryRunner();
        historyRunner.baseHistoryTask.run();

        historyRunner.baseHistoryTask.recover(Contract.BTC_USD, 20260202, 20260225);
    }

}
