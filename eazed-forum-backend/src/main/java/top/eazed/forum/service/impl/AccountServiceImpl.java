package top.eazed.forum.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.eazed.forum.entity.dto.Account;
import top.eazed.forum.entity.vo.request.ConfirmResetVO;
import top.eazed.forum.entity.vo.request.EmailRegisterVO;
import top.eazed.forum.entity.vo.request.EmailResetVO;
import top.eazed.forum.mapper.AccountMapper;
import top.eazed.forum.service.AccountService;
import top.eazed.forum.utils.Const;
import top.eazed.forum.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    
    @Resource
    AmqpTemplate amqpTemplate;
    
    @Resource
    StringRedisTemplate stringRedisTemplate;
    
    @Resource
    FlowUtils flowUtils;
    
    @Resource
    PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }
    
    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }
    
    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if (type.equals("reset")) {
                if (!this.existsAccountByEmail(email)) return "此账号未注册";
            }
            if (verifyLimit(ip)) {
                Random random = new Random();
                int code = random.nextInt(900000) + 100000;
                Map<String, Object> data = Map.of(
                        "type", type,
                        "email", email,
                        "code", code,
                        "ip", ip
                );


                amqpTemplate.convertAndSend("mail", data);
                stringRedisTemplate.opsForValue()
                        .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
                return null;
            }
            return "请求频繁";
        }
    }
    
    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if (code == null) return "请获取验证码";
        if (!code.equals(vo.getCode())) return "验证码错误";
        if (this.existsAccountByEmail(email)) return "此账号已经注册";
        if (this.existsAccountByUsername(vo.getUsername())) return "此用户名已经注册";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null, vo.getUsername(), password, email, "user", new Date());

        if (this.save(account)) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            return null;
        } else {
            return "注册失败";
        }
    }
    
    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email = vo.getEmail();
        String code = vo.getCode();
        if (code == null) return "请获取验证码";
        if (!this.existsAccountByEmail(email)) return "此账号未注册";
        if (!code.equals(stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email))) return "验证码错误";
        return null;
        
    }
    
    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String verify = this.resetConfirm(new ConfirmResetVO(vo.getEmail(), vo.getCode()));
        if (verify != null) return verify;
        String email = vo.getEmail();
        String password = passwordEncoder.encode(vo.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            return null;
        }
        return "重置密码失败";
    }
    
    private boolean existsAccountByEmail(String email) {
        return baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }
    private boolean existsAccountByUsername(String username) {
        return baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }
    
    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, 60);
    }
    
    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }
}
