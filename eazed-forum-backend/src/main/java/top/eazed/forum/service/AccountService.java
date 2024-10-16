package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import top.eazed.forum.entity.dto.AccountDTO;
import top.eazed.forum.entity.vo.request.*;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AccountService extends IService<AccountDTO>, UserDetailsService {
    AccountDTO findAccountByNameOrEmail(String text);
    
    String registerEmailVerifyCode(String type, String email, String ip);
    
    String registerEmailAccount(EmailRegisterVO vo);
    
    String resetConfirm(ConfirmResetVO vo);
    
    String resetEmailAccountPassword(EmailResetVO vo);
    
    AccountDTO findAccountById(int id);
    
    String modifyEmail(int id, ModifyEmailVO vo);
    
    String changePassword(int id, @Valid ChangePasswordVO vo, HttpServletRequest request);
}
