package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.eazed.forum.entity.dto.AccountDetailsDTO;
import top.eazed.forum.entity.vo.request.AccountDetailsSaveVO;


public interface AccountDetailsService extends IService<AccountDetailsDTO> {
    AccountDetailsDTO findAccountDetailsById(int id);
    
    boolean saveAccountDetails(int id, AccountDetailsSaveVO vo);
}
