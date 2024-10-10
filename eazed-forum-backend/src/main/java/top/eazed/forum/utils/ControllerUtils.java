package top.eazed.forum.utils;

import org.springframework.stereotype.Component;
import top.eazed.forum.entity.RestBean;

import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class ControllerUtils {
    public <T> RestBean<Void> messageHandle(T vo, Function<T, String> action) {
        return messageHandle(() -> action.apply(vo));
    }
    
    public RestBean<Void> messageHandle(Supplier<String> action) {
        String message = action.get();
        if (message != null) return RestBean.failure(400, message);
        return RestBean.success();
    }
}
