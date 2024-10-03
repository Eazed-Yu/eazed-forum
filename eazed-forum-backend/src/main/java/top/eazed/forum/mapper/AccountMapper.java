package top.eazed.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.eazed.forum.entity.dto.AccountDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<AccountDTO> {

}
