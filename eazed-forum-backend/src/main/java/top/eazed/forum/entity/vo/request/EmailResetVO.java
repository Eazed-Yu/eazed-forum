package top.eazed.forum.entity.vo.request;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

@lombok.Data
public class EmailResetVO {
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
    @Length(min = 6, max = 20)
    private String password;
    
}
