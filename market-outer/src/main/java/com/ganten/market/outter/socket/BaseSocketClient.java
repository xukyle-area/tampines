package com.ganten.market.outter.socket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.outter.writer.QuoteWriter;
import com.ganten.market.outter.writer.RedisQuoteWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSocketClient extends WebSocketClient {

    protected static final QuoteWriter redisWriter = RedisQuoteWriter.of("localhost:6379", "redispassword");

    public BaseSocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }

    @Override
    public void onOpen(ServerHandshake data) {
        log.info("WebSocket 连接已打开!");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Connection closed by {}, Code: {}, Reason: {}", (remote ? "remote peer" : "us"), code, reason);
        log.info("WebSocket连接已关闭, 准备重新连接...");
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket连接发生错误...", ex);
    }

    public void subscribe() {
        if (!this.isOpen()) {
            log.error("WebSocket is not open, cannot subscribe.");
            return;
        }
        log.info("WebSocket is open, proceeding to subscribe.");
        String subscription = this.buildSubscription(Arrays.asList(Contract.values()));
        log.info("subscribe message:{}", subscription);
        this.send(subscription);
    }

    protected abstract Consumer<String> getApiCallback();

    @Override
    public void onMessage(String message) {
        this.getApiCallback().accept(message);
    }

    protected abstract String buildSubscription(List<Contract> symbols);
}
