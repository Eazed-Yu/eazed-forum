package top.eazed.forum.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailRegisterVO {
    @Email
    @Length(min = 4, max = 50)
    private String email;
    @Length(max = 6, min = 6)
    private String code;
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    private String username;
    @Length(min = 6, max = 20)
    private String password;
}
