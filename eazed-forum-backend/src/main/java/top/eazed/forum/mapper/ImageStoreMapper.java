package top.eazed.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.eazed.forum.entity.dto.StoreImageDTO;

@Mapper
public interface ImageStoreMapper extends BaseMapper<StoreImageDTO> {
}
