package top.eazed.forum.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.eazed.forum.entity.BaseData;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account_details")
public class AccountDetailsDTO implements BaseData {
    @TableId
    Integer id;
    int gender;
    String phone;
    String qq;
    String wechat;
    String description;
    
}
