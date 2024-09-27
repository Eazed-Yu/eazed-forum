package top.eazed.forum.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(int code, String message, T data) {
    /*
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, "请求成功", data);
    }
    
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, "请求成功", null);
    }
    
    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, message, null);
    }
    
    public static <T> RestBean<T> unauthorized() {
        return failure(401, "未授权");
    }
    
    public static <T> RestBean<T> forbidden() {return failure(403, "禁止访问");}
    
    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
    
    
    public static <T> RestBean<T> forbidden(String message) {
        return failure(403, message);
    }
    */
    
    
    // 成功
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, "请求成功", data);
    }
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, "请求成功", null);
    }
    
    // 失败
    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, message, null);
    }
    
    
    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
