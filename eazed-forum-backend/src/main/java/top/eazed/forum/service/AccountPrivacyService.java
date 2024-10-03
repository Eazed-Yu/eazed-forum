package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.eazed.forum.entity.dto.AccountPrivacyDTO;
import top.eazed.forum.entity.vo.request.PrivacySaveVO;


public interface AccountPrivacyService extends IService<AccountPrivacyDTO> {
    
    void savePrivacy(int id, PrivacySaveVO vo);
    
    AccountPrivacyDTO accountPrivacy(int id);
}
