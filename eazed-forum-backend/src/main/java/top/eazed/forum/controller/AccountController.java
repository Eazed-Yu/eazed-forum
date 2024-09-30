package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.entity.dto.Account;
import top.eazed.forum.entity.dto.AccountDetails;
import top.eazed.forum.entity.vo.request.AccountDetailsSaveVO;
import top.eazed.forum.entity.vo.response.AccountDetailsVO;
import top.eazed.forum.entity.vo.response.AccountVO;
import top.eazed.forum.service.AccountDetailsService;
import top.eazed.forum.service.AccountService;
import top.eazed.forum.utils.Const;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    
    @Resource
    AccountService service;
    
    @Resource
    AccountDetailsService detailsService;
    
    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        Account account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }
    
    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountDetails details = Optional.ofNullable(detailsService.findAccountDetailsById(id)).orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVO.class));
    }
    
    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody AccountDetailsSaveVO vo) {
        if (detailsService.saveAccountDetails(id, vo)) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, "用户名已存在");
        }
    }
}
