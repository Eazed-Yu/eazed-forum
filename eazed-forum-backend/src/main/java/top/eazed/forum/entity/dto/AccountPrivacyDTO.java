package top.eazed.forum.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.eazed.forum.entity.BaseData;

@Data
@TableName("db_account_privacy")
public class AccountPrivacyDTO implements BaseData {
    @TableId(type = IdType.AUTO)
    final Integer id;
    boolean phone = false;
    boolean email = false;
    boolean wechat = false;
    boolean qq = false;
    boolean gender = false;
}
