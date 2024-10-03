package top.eazed.forum.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVO {
    String username;
    String email;
    String role;
    Date registerTime;
    String avatar;
}
