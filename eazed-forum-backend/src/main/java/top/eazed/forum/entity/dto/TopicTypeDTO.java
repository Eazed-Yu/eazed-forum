package top.eazed.forum.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.eazed.forum.entity.BaseData;

@Data
@TableName("db_topic_type")
public class TopicTypeDTO implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String name;
    String description;
    String color;
}
