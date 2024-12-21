package top.eazed.forum.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("db_article")
public class ArticleDTO implements Serializable {
    String topic;
    String title;
    String content;
}
