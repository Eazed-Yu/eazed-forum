package top.eazed.forum.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.eazed.forum.entity.BaseData;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account") // 配置表名
public class AccountDTO implements BaseData {
    @TableId(type = IdType.AUTO) // 配置主键自增
    Integer id;
    
    
    String username;

    String password;

    String email;

    String role;

    Date registerTime;
    
    String avatar;
}
