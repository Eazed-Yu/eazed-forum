package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.eazed.forum.entity.dto.TopicDTO;
import top.eazed.forum.entity.dto.TopicTypeDTO;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.entity.vo.response.TopicDetailVO;
import top.eazed.forum.entity.vo.response.TopicPreviewVO;
import top.eazed.forum.entity.vo.response.TopicTopVO;

import java.util.List;

public interface TopicService extends IService<TopicDTO> {
    List<TopicTypeDTO> listTypes();
    
    String createTopic(int uid, TopicCreateVO vo);
    
    List<TopicPreviewVO> listTopicByPage(int page, int type);
    
    TopicDetailVO getTopic(int tid);
    
    List<TopicTopVO> listTopTopics();
    
}
