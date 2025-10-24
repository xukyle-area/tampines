package com.ganten.market.common.pojo;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ResultEventHolder {
    private ResultEventType result_event_type;
    private long contractId;

    private Tick tick;
    private TradeInfo trade;
    private OrderBook orderBook;

    // the kafka consumer offset of this event
    private long offset;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ResultEventType getResult_event_type() {
        return result_event_type;
    }

    public void setResult_event_type(ResultEventType result_event_type) {
        this.result_event_type = result_event_type;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public Tick getTick() {
        return tick;
    }

    public void setTick(Tick tick) {

        this.tick = tick;
    }

    public TradeInfo getTrade() {
        return trade;
    }

    public void setTrade(TradeInfo trade) {
        this.trade = trade;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public byte[] toByteArray() {
        // Serialize core fields and optionally some nested summaries. Format:
        // [int typeLen][type UTF-8][long contractId][long offset][long timestamp][byte flags]
        // then for each nested present (tick/trade/orderBook): [int len][UTF-8 summary]
        String typeStr = this.result_event_type != null ? this.result_event_type.name() : "";
        byte[] typeBytes = typeStr.getBytes(StandardCharsets.UTF_8);

        long contractId = this.contractId;
        long offset = this.offset;
        long ts = this.timestamp;

        byte flags = 0;
        byte[] tickBytes = null;
        byte[] tradeBytes = null;
        byte[] orderBookBytes = null;

        if (this.tick != null) {
            flags |= 1;
            String s = renderTick(this.tick);
            tickBytes = s.getBytes(StandardCharsets.UTF_8);
        }
        if (this.trade != null) {
            flags |= 2;
            String s = renderTrade(this.trade);
            tradeBytes = s.getBytes(StandardCharsets.UTF_8);
        }
        if (this.orderBook != null) {
            flags |= 4;
            String s = renderOrderBook(this.orderBook);
            orderBookBytes = s.getBytes(StandardCharsets.UTF_8);
        }

        int capacity = Integer.BYTES + typeBytes.length + Long.BYTES * 3 + 1;
        capacity += (tickBytes == null ? 0 : Integer.BYTES + tickBytes.length);
        capacity += (tradeBytes == null ? 0 : Integer.BYTES + tradeBytes.length);
        capacity += (orderBookBytes == null ? 0 : Integer.BYTES + orderBookBytes.length);

        ByteBuffer bb = ByteBuffer.allocate(capacity);
        bb.putInt(typeBytes.length);
        if (typeBytes.length > 0)
            bb.put(typeBytes);
        bb.putLong(contractId);
        bb.putLong(offset);
        bb.putLong(ts);
        bb.put(flags);

        if (tickBytes != null) {
            bb.putInt(tickBytes.length);
            bb.put(tickBytes);
        }
        if (tradeBytes != null) {
            bb.putInt(tradeBytes.length);
            bb.put(tradeBytes);
        }
        if (orderBookBytes != null) {
            bb.putInt(orderBookBytes.length);
            bb.put(orderBookBytes);
        }

        return bb.array();
    }

    private String renderTick(Tick t) {
        StringBuilder sb = new StringBuilder();
        if (t.getBid() != null)
            sb.append("bid=").append(t.getBid().toPlainString()).append(';');
        if (t.getAsk() != null)
            sb.append("ask=").append(t.getAsk().toPlainString()).append(';');
        if (t.getLast() != null)
            sb.append("last=").append(t.getLast().toPlainString()).append(';');
        return sb.toString();
    }

    private String renderTrade(TradeInfo ti) {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(ti.getId()).append(';');
        if (ti.getPrice() != null)
            sb.append("price=").append(ti.getPrice().toPlainString()).append(';');
        if (ti.getVolume() != null)
            sb.append("volume=").append(ti.getVolume().toPlainString()).append(';');
        sb.append("isBuyerMaker=").append(ti.isBuyerMaker()).append(';');
        sb.append("time=").append(ti.getTime()).append(';');
        return sb.toString();
    }

    private String renderOrderBook(OrderBook ob) {
        StringBuilder sb = new StringBuilder();
        java.util.List<OrderBookTuple> bids = ob.getBids();
        java.util.List<OrderBookTuple> asks = ob.getAsks();
        sb.append("bids=").append(bids == null ? 0 : bids.size()).append(';');
        sb.append("asks=").append(asks == null ? 0 : asks.size()).append(';');
        sb.append("updateId=").append(ob.getUpdateId()).append(';');
        sb.append("firstId=").append(ob.getFirstId()).append(';');
        sb.append("lastId=").append(ob.getLastId()).append(';');
        return sb.toString();
    }
}
