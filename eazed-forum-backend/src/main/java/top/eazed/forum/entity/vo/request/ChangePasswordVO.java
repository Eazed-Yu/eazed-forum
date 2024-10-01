package top.eazed.forum.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChangePasswordVO {
    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    String origin_password;
    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    String Password;
}
