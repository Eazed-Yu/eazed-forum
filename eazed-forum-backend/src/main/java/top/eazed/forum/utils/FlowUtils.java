package top.eazed.forum.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FlowUtils {
    
    @Resource
    StringRedisTemplate stringRedisTemplate;
    
    public boolean limitOnceCheck(String key, int blockTime) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return false;
        } else {
            stringRedisTemplate.opsForValue().set(key,"", blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
