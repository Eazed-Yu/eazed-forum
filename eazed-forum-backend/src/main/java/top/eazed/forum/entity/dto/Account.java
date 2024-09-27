package top.eazed.forum.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
@TableName("db_account") // 配置表名
public class Account {
    @TableId(type = IdType.AUTO) // 配置主键自增
    Integer id;

    String username;

    String password;

    String email;

    String role;

    Date registerTime;

}
