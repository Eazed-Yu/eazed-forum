package top.eazed.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.eazed.forum.entity.dto.AccountPrivacyDTO;
import top.eazed.forum.entity.vo.request.PrivacySaveVO;
import top.eazed.forum.mapper.AccountPrivacyMapper;
import top.eazed.forum.service.AccountPrivacyService;

import java.util.Optional;

@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacyDTO> implements AccountPrivacyService {
    @Override
    public void savePrivacy(int id, PrivacySaveVO vo) {
        AccountPrivacyDTO account = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacyDTO(id));
        boolean status = vo.isStatus();
        switch (vo.getType()) {
            case "phone" -> account.setPhone(status);
            case "email" -> account.setEmail(status);
            case "gender" -> account.setGender(status);
            case "wechat" -> account.setWechat(status);
            case "qq" -> account.setQq(status);
        }
        this.saveOrUpdate(account);
    }
    
    @Override
    public AccountPrivacyDTO accountPrivacy(int id) {
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacyDTO(id));
    }
    
}
