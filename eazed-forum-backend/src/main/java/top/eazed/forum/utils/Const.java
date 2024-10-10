package top.eazed.forum.utils;

public class Const {
    public static final String JWT_BLOCK_LIST = "jwt:blacklist:";
    
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit;";
    
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";
    
    public static final int ORDER_CORS_FILTER = -102;
    
    public static final String ATTR_USER_ID = "userId";
    
    public static final String FORBIDDEN = "禁止访问";
    
    public static final String UNAUTHORIZED = "未授权";
    
    public static final int ORDER_LIMIT = -101;
    
    public static String FLOW_LIMIT_COUNTER = "flow:counter:";
    
    public static String FLOW_LIMIT_BLOCK = "flow:block:";
    
    public static final String FORUM_WEATHER_CACHE = "weather:cache:";
    
    public static final String FORUM_IMAGE_COUNTER = "forum:image:";
    //JWT令牌
    public final static String JWT_BLACK_LIST = "jwt:blacklist:";
    public final static String JWT_FREQUENCY = "jwt:frequency:";
    //过滤器优先级
    public final static int ORDER_FLOW_LIMIT = -101;
    public final static int ORDER_CORS = -102;
    //消息队列
    public final static String MQ_MAIL = "mail";
    //用户角色
    public final static String ROLE_DEFAULT = "user";
    //论坛相关
    public final static String FORUM_TOPIC_CREATE_COUNTER = "forum:topic:create:";
    public final static String FORUM_TOPIC_COMMENT_COUNTER = "forum:topic:comment:";
    public final static String FORUM_TOPIC_PREVIEW_CACHE = "topic:preview:";
}
