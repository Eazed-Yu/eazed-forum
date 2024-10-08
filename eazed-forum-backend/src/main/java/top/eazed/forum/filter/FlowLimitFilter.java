package top.eazed.forum.filter;


import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.eazed.forum.utils.Const;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.ORDER_LIMIT)
@Slf4j
public class FlowLimitFilter extends HttpFilter {
    
    @Resource
    StringRedisTemplate template;
    
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.tryCount(request.getRemoteAddr())) {
            chain.doFilter(request, response);
        } else {
            this.Forbidden(response);
        }
    }
    
    private boolean tryCount(String ip) {
        synchronized (ip.intern()) {
            if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + ip))) {
                return false;
            }
            if (!Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUNTER + ip))) {
                template.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 3, TimeUnit.SECONDS);
            }
            long increment = Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip, 1)).orElse(0L);
            if (increment > 20) {
                log.info("IP: {} 请求过于频繁", ip);
                template.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 1, TimeUnit.MINUTES);
                return false;
            }
            return true;
        }
    }
    
    private void Forbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("请求过于频繁，请稍后再试");
    }
    
    public boolean limitPeriodCounterCheck(String counterKey, int frequency, int period) {
        if (Boolean.TRUE.equals(template.hasKey(counterKey))) {
            long increment = Optional.ofNullable(template.opsForValue().increment(counterKey, 1)).orElse(0L);
            return increment <= frequency;
        } else {
            template.opsForValue().set(counterKey, "1", period, TimeUnit.SECONDS);
        }
        return true;
    }
    
    
    
    
    // 感觉这一段教程里面写的稀巴烂，好好的逻辑拆分得乱七八糟
    /*

    
    private boolean tryCount(String ip) {
        synchronized (ip.intern()) {
            if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + ip))) {
                return false;
            } else {
                return this.limitPeriodCheck(ip);
            }
        }
    }
    
    private boolean limitPeriodCheck(String ip) {
        if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUNTER))) {
            long increment = Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip, 1)).orElse(0L);
            if (increment > 10) {
                template.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 1, TimeUnit.MINUTES);
                return false;
            }
        } else {
            template.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 3, TimeUnit.SECONDS);
        }
        return true;
    }
     */

    
}
