package top.eazed.forum.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVO {
    Integer id;
    String username;
    String email;
    String role;
    Date registerTime;
    String avatar;
}
