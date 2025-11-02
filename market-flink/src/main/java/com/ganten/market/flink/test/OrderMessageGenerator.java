package com.ganten.market.flink.test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import com.ganten.market.common.enums.Action;
import com.ganten.market.common.enums.Side;
import com.ganten.market.common.flink.input.Order;

/**
 * 订单消息生成器 - 用于测试 OrderBookProcessor
 * 生成各种订单操作的示例数据
 */
public class OrderMessageGenerator {

    public static void main(String[] args) {
        List<Order> orders = generateSampleOrders();
        printOrdersAsJson(orders);
    }

    /**
     * 生成示例订单数据
     * 模拟一个合约的订单簿操作
     */
    public static List<Order> generateSampleOrders() {
        long contractId = 1001L;
        long baseTime = System.currentTimeMillis();

        return Arrays.asList(
                // 初始买单
                createOrder(contractId, baseTime + 1000, new BigDecimal("100.00"), new BigDecimal("10"), Side.BID,
                        Action.INSERT),
                createOrder(contractId, baseTime + 2000, new BigDecimal("99.50"), new BigDecimal("5"), Side.BID,
                        Action.INSERT),
                createOrder(contractId, baseTime + 3000, new BigDecimal("101.00"), new BigDecimal("8"), Side.BID,
                        Action.INSERT),

                // 初始卖单
                createOrder(contractId, baseTime + 4000, new BigDecimal("102.00"), new BigDecimal("12"), Side.ASK,
                        Action.INSERT),
                createOrder(contractId, baseTime + 5000, new BigDecimal("103.00"), new BigDecimal("6"), Side.ASK,
                        Action.INSERT),
                createOrder(contractId, baseTime + 6000, new BigDecimal("101.50"), new BigDecimal("15"), Side.ASK,
                        Action.INSERT),

                // 买单更新 - 增加数量
                createOrder(contractId, baseTime + 7000, new BigDecimal("100.00"), new BigDecimal("5"), Side.BID,
                        Action.INSERT),

                // 卖单部分成交 - 减少数量
                createOrder(contractId, baseTime + 8000, new BigDecimal("102.00"), new BigDecimal("7"), Side.ASK,
                        Action.DELETE),

                // 新增买单
                createOrder(contractId, baseTime + 9000, new BigDecimal("100.50"), new BigDecimal("20"), Side.BID,
                        Action.INSERT),

                // 卖单完全成交
                createOrder(contractId, baseTime + 10000, new BigDecimal("103.00"), new BigDecimal("6"), Side.ASK,
                        Action.DELETE),

                // 新增卖单
                createOrder(contractId, baseTime + 11000, new BigDecimal("104.00"), new BigDecimal("25"), Side.ASK,
                        Action.INSERT));
    }

    /**
     * 创建订单对象
     */
    private static Order createOrder(long contractId, long timestamp, BigDecimal price, BigDecimal quantity, Side side,
            Action action) {
        Order order = new Order();
        order.setContractId(contractId);
        order.setTimestamp(timestamp);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setAmount(price.multiply(quantity)); // 计算金额
        order.setSide(side.name());
        order.setAction(action.name());
        return order;
    }

    /**
     * 将订单列表打印为 JSON 格式
     */
    private static void printOrdersAsJson(List<Order> orders) {
        System.out.println("[");
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.println("  {");
            System.out.println("    \"contractId\": " + order.getContractId() + ",");
            System.out.println("    \"timestamp\": " + order.getTimestamp() + ",");
            System.out.println("    \"price\": " + order.getPrice() + ",");
            System.out.println("    \"quantity\": " + order.getQuantity() + ",");
            System.out.println("    \"amount\": " + order.getAmount() + ",");
            System.out.println("    \"side\": \"" + order.getSide() + "\",");
            System.out.println("    \"action\": \"" + order.getAction() + "\"");
            System.out.println("  }" + (i < orders.size() - 1 ? "," : ""));
        }
        System.out.println("]");
    }
}
