package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.entity.dto.Account;
import top.eazed.forum.entity.vo.response.AccountVO;
import top.eazed.forum.service.AccountService;
import top.eazed.forum.utils.Const;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    
    @Resource
    AccountService service;
    
    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        Account account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }
}
