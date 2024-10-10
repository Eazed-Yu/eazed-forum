package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.eazed.forum.entity.dto.TopicDTO;
import top.eazed.forum.entity.dto.TopicTypeDTO;
import top.eazed.forum.entity.vo.request.TopicCreateVO;

import java.util.List;

public interface TopicService extends IService<TopicDTO> {
    List<TopicTypeDTO> listTypes();
    
    String createTopic(int uid, TopicCreateVO vo);
}
