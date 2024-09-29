package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.eazed.forum.entity.dto.Account;
import top.eazed.forum.entity.vo.request.ConfirmResetVO;
import top.eazed.forum.entity.vo.request.EmailRegisterVO;
import top.eazed.forum.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    
    String registerEmailVerifyCode(String type, String email, String ip);
    
    String registerEmailAccount(EmailRegisterVO vo);
    
    String resetConfirm(ConfirmResetVO vo);
    
    String resetEmailAccountPassword(EmailResetVO vo);
    
    Account findAccountById (int id);
}
