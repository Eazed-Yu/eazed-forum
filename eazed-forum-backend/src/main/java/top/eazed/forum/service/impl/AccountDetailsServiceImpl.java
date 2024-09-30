package top.eazed.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.eazed.forum.entity.dto.Account;
import top.eazed.forum.entity.dto.AccountDetails;
import top.eazed.forum.entity.vo.request.AccountDetailsSaveVO;
import top.eazed.forum.mapper.AccountDetailsMapper;
import top.eazed.forum.service.AccountDetailsService;
import top.eazed.forum.service.AccountService;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {
    
    @Resource
    AccountService accountService;
    
    
    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById(id);
    }
    
    @Override
    public boolean saveAccountDetails(int id, AccountDetailsSaveVO vo) {
        Account account = accountService.findAccountByNameOrEmail(vo.getUsername());
        if (account != null && account.getId() != id) {
            return false;
        }
        accountService.update()
                .eq("id", id)
                .set("username", vo.getUsername())
                .update();
        this.saveOrUpdate(new AccountDetails(
                id,
                vo.getGender(),
                vo.getPhone(),
                vo.getQq(),
                vo.getWechat(),
                vo.getDescription()
        ));
        return true;
    }
}
