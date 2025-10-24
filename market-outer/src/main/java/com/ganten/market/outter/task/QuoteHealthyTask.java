package com.ganten.market.outter.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ganten.market.common.RedisUtils;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.outter.writer.RedisQuoteWriter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.commands.JedisCommands;

@Slf4j
@Service
public class QuoteHealthyTask {
    private final static double stdThreshold = 0.1;
    private static JedisCommands jedis;

    private static String redisNodes;
    private static String redisPassword;

    @Value("${app.redis.cluster.nodes:localhost:6379}")
    public void setRedisNodes(String redisNodes) {
        QuoteHealthyTask.redisNodes = redisNodes;
    }

    @Value("${app.redis.cluster.password:}")
    public void setRedisPassword(String redisPassword) {
        QuoteHealthyTask.redisPassword = redisPassword;
    }

    static {
        // 初始化将在Spring上下文加载后通过setter设置
    }

    @PostConstruct
    public void init() {
        if (jedis == null) {
            jedis = RedisQuoteWriter.getJedis(redisNodes, redisPassword);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void run() {
        QuoteHealthyTask.calculateAllContract();
    }

    private static void calculateAllContract() {
        for (Contract contract : Contract.values()) {

            Map<Market, BigDecimal> priceMap = QuoteHealthyTask.getPriceMap(contract.getId());
            if (!priceMap.containsKey(Market.EXODUS) || priceMap.size() < 2) {
                log.info("Skip calculating std for contract {}, missing EXODUS market data", contract.getSymbol());
                continue;
            }
            BigDecimal std = QuoteHealthyTask.calculateStd(priceMap);
            if (std.compareTo(new BigDecimal(stdThreshold)) > 0) {
                log.error("orderBookMetrics alert, thirdpartyQuoteStd, {} = {}", contract.getSymbol(), std);
            }
        }
    }

    /**
     * 获取指定合约 id 在各个市场的最新价格
     *
     * @param contractId 合约 id
     * @return 各市场的最新价格映射
     */
    private static Map<Market, BigDecimal> getPriceMap(long contractId) {
        Map<Market, BigDecimal> priceMap = new HashMap<>();

        // 获取市场价格并记录失败市场
        for (Market market : Market.values()) {
            String key = RedisUtils.generateRedisQuoteKey(contractId, market);
            try {
                BigDecimal last = new BigDecimal(jedis.hget(key, "last"));
                priceMap.put(market, last);
            } catch (Exception e) {
                log.error("Failed to get price for contractId: {}, market: {}", contractId, market);
            }
        }
        return priceMap;
    }

    /**
     * 计算价格的标准差
     *
     * @param priceMap 各市场的价格映射
     * @return 标准差
     */
    private static BigDecimal calculateStd(Map<Market, BigDecimal> priceMap) {
        // 计算有效价格的数量
        int size = priceMap.size();
        // 计算总和和平均值
        BigDecimal sum = priceMap.values().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal avg = sum.divide(new BigDecimal(size), RoundingMode.HALF_EVEN);

        // 计算平方差和
        BigDecimal squaredDiffSum = BigDecimal.ZERO;
        for (BigDecimal price : priceMap.values()) {
            BigDecimal diff = price.subtract(avg);
            squaredDiffSum = squaredDiffSum.add(diff.pow(2));
        }
        // 计算标准差
        BigDecimal std = BigDecimal.valueOf(Math.sqrt(squaredDiffSum.doubleValue() / size));
        BigDecimal stdRate = std.divide(avg, RoundingMode.HALF_EVEN);
        return stdRate;
    }
}
