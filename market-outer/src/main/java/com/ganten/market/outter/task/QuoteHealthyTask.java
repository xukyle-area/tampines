package com.ganten.market.outter.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ganten.market.common.KeyGenerator;
import com.ganten.market.common.enums.Contract;
import com.ganten.market.common.pojo.Market;
import com.ganten.market.common.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
@Service
public class QuoteHealthyTask {

    static {
        // 初始化将在Spring上下文加载后通过setter设置
    }

    @Scheduled(fixedRate = 30000)
    public void run() {
        QuoteHealthyTask.calculateAllContract();
    }

    private static void calculateAllContract() {
        for (Contract contract : Contract.values()) {

            Map<Market, BigDecimal> priceMap = QuoteHealthyTask.getPriceMap(contract);
            log.info("Contract {} price map : {}", contract.getSymbol(), priceMap);
            if (priceMap.size() < 2) {
                continue;
            }
            BigDecimal std = QuoteHealthyTask.calculateStd(priceMap);
            log.info("Contract {} std rate: {}", contract.getSymbol(), std.toPlainString());
        }
    }

    /**
     * 获取指定合约 id 在各个市场的最新价格
     *
     * @param contractId 合约 id
     * @return 各市场的最新价格映射
     */
    private static Map<Market, BigDecimal> getPriceMap(Contract contract) {
        Map<Market, BigDecimal> priceMap = new HashMap<>();

        // 获取市场价格并记录失败市场
        try (Jedis jedis = RedisClient.getResource()) {
            for (Market market : Market.values()) {
                String key = KeyGenerator.realtimeKey(contract, market);
                String lastStr = jedis.hget(key, "last");
                if (StringUtils.isBlank(lastStr)) {
                    continue;
                }
                BigDecimal last = new BigDecimal(lastStr);
                priceMap.put(market, last);
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
