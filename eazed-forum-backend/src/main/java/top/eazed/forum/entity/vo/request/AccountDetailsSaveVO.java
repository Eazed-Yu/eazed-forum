package top.eazed.forum.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AccountDetailsSaveVO {
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = "用户名不合法")
    @Length(min = 1, max = 10)
    String username;
    @Pattern(regexp = "^[0-2]$", message = "性别不合")
    int gender;
    @Pattern(regexp = "^[1-9]\\d{10}$", message = "手机号不合法")
    String phone;
    @Length(max = 100)
    String qq;
    @Length(max = 100)
    String wechat;
    @Length(max = 100)
    String description;
}
