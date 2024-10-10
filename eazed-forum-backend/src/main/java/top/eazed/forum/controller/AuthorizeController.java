package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.entity.vo.request.ConfirmResetVO;
import top.eazed.forum.entity.vo.request.EmailRegisterVO;
import top.eazed.forum.entity.vo.request.EmailResetVO;
import top.eazed.forum.service.AccountService;
import top.eazed.forum.utils.ControllerUtils;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    
    @Resource
    AccountService service;
    
    @Resource
    ControllerUtils utils;
    
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                  @RequestParam @Pattern(regexp = "(register|reset|modify)") String type,
                                  HttpServletRequest request) {
        return utils.messageHandle(() -> service.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
    }
    
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo) {
        return utils.messageHandle(() -> service.registerEmailAccount(vo));
    }
    
    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo) {
        return utils.messageHandle(vo, service::resetConfirm);
    }
    
    @PostMapping("/reset-password")
    public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo) {
        return utils.messageHandle(vo, service::resetEmailAccountPassword);
    }
    
    
}
