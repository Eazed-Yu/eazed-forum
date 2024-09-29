package top.eazed.forum.entity.vo.response;

import lombok.Data;

@Data
public class AccountVO {
    String username;
    String email;
    String role;
    String registerTime;
}
