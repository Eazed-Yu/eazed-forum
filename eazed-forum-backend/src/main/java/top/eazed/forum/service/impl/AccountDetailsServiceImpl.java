package top.eazed.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.eazed.forum.entity.dto.AccountDTO;
import top.eazed.forum.entity.dto.AccountDetailsDTO;
import top.eazed.forum.entity.vo.request.AccountDetailsSaveVO;
import top.eazed.forum.mapper.AccountDetailsMapper;
import top.eazed.forum.service.AccountDetailsService;
import top.eazed.forum.service.AccountService;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetailsDTO> implements AccountDetailsService {
    
    @Resource
    AccountService accountService;
    
    
    @Override
    public AccountDetailsDTO findAccountDetailsById(int id) {
        return this.getById(id);
    }
    
    @Override
    public boolean saveAccountDetails(int id, AccountDetailsSaveVO vo) {
        AccountDTO account = accountService.findAccountByNameOrEmail(vo.getUsername());
        if (account != null && account.getId() != id) {
            return false;
        }
        accountService.update()
                .eq("id", id)
                .set("username", vo.getUsername())
                .update();
        this.saveOrUpdate(new AccountDetailsDTO(
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
