package top.eazed.forum.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationVO {
    Integer id;
    String title;
    String content;
    String type;
    String url;
    Date time;
}