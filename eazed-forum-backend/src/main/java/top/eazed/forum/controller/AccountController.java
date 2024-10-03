package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.entity.dto.AccountDTO;
import top.eazed.forum.entity.dto.AccountDetailsDTO;
import top.eazed.forum.entity.vo.request.AccountDetailsSaveVO;
import top.eazed.forum.entity.vo.request.ChangePasswordVO;
import top.eazed.forum.entity.vo.request.ModifyEmailVO;
import top.eazed.forum.entity.vo.request.PrivacySaveVO;
import top.eazed.forum.entity.vo.response.AccountDetailsVO;
import top.eazed.forum.entity.vo.response.AccountPrivacyVO;
import top.eazed.forum.entity.vo.response.AccountVO;
import top.eazed.forum.service.AccountDetailsService;
import top.eazed.forum.service.AccountPrivacyService;
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
    
    @Resource
    AccountPrivacyService accountPrivacyService;
    
    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountDTO account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }
    
    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountDetailsDTO details = Optional.ofNullable(detailsService.findAccountDetailsById(id)).orElseGet(AccountDetailsDTO::new);
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
    
    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid ModifyEmailVO vo) {
        String message = service.modifyEmail(id, vo);
        if (message != null) return RestBean.failure(400, message);
        return RestBean.success();
    }
    
    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                         @RequestBody @Valid ChangePasswordVO vo,
                                         HttpServletRequest request
    ) {
        String message = service.changePassword(id, vo, request);
        if (message != null) return RestBean.failure(400, message);
        return RestBean.success();
    }
    
    @PostMapping("/save-privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid PrivacySaveVO privacySaveVO) {
        accountPrivacyService.savePrivacy(id, privacySaveVO);
        return RestBean.success();
    }
    
    @GetMapping("/privacy")
    public RestBean<AccountPrivacyVO> savePrivacy(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        return RestBean.success(accountPrivacyService.accountPrivacy(id).asViewObject(AccountPrivacyVO.class));
    }
}
