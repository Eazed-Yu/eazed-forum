package top.eazed.forum.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class TopicDetailVO {
    Integer id;
    String title;
    String content;
    Integer type;
    Date Time;
    User user;
    
    @Data
    public static class User {
        Integer id;
        String username;
        String avatar;
        String description;
        boolean gender;
        String qq;
        String wechat;
        String phone;
        String email;
    }
}
