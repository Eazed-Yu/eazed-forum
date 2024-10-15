package top.eazed.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import top.eazed.forum.entity.dto.Interact;
import top.eazed.forum.entity.dto.TopicDTO;
import top.eazed.forum.entity.dto.TopicTypeDTO;
import top.eazed.forum.entity.vo.request.AddCommentVO;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.entity.vo.request.TopicUpdateVO;
import top.eazed.forum.entity.vo.response.CommentVO;
import top.eazed.forum.entity.vo.response.TopicDetailVO;
import top.eazed.forum.entity.vo.response.TopicPreviewVO;
import top.eazed.forum.entity.vo.response.TopicTopVO;

import java.util.List;

public interface TopicService extends IService<TopicDTO> {
    List<TopicTypeDTO> listTypes();
    
    String createTopic(int uid, TopicCreateVO vo);
    
    List<TopicPreviewVO> listTopicByPage(int page, int type);
    
    TopicDetailVO getTopic(int tid, int uid);
    
    List<TopicTopVO> listTopTopics();
    
    void interact(Interact interact, boolean state);
    
    List<TopicPreviewVO> listTopicCollects(int id);
    
    String updateTopic(int id, @Valid TopicUpdateVO vo);
    
    List<CommentVO> comments(int tid, int i);
    
    String createComment(int id, @Valid AddCommentVO vo);
    
    void deleteComment(int id, int uid);
}
