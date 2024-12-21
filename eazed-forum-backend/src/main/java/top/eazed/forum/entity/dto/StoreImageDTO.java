package top.eazed.forum.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName("db_image_store")
public class StoreImageDTO {
    @TableId
    Integer uid;
    String name;
    Date time;
}
