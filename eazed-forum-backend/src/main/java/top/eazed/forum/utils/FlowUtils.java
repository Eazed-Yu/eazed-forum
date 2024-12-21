package top.eazed.forum.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FlowUtils {
    
    private static final LimitAction defaultAction = overclock -> !overclock;
    @Resource
    StringRedisTemplate template;
    
    /**
     * 针对于单次频率限制，请求成功后，在冷却时间内不得再次进行请求，如3秒内不能再次发起请求
     *
     * @param key       键
     * @param blockTime 限制时间
     * @return 是否通过限流检查
     */
    public boolean limitOnceCheck(String key, int blockTime) {
        return this.internalCheck(key, 1000, blockTime, defaultAction);
    }
    
    /**
     * 针对于在时间段内多次请求限制，如3秒内20次请求
     *
     * @param counterKey 计数键
     * @param frequency  请求频率
     * @param period     计数周期
     * @return 是否通过限流检查
     */
    public boolean limitPeriodCounterCheck(String counterKey, int frequency, int period) {
        return this.internalCheck(counterKey, frequency, period, defaultAction);
    }
    
    /**
     * 内部使用请求限制主要逻辑
     *
     * @param key       计数键
     * @param frequency 请求频率
     * @param period    计数周期
     * @param action    限制行为与策略
     * @return 是否通过限流检查
     */
    private boolean internalCheck(String key, int frequency, int period, LimitAction action) {
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            Long value = Optional.ofNullable(template.opsForValue().increment(key)).orElse(0L);
            return action.run(value > frequency);
        } else {
            template.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }
    
    /**
     * 内部使用，限制行为与策略
     */
    private interface LimitAction {
        boolean run(boolean overclock);
    }
}