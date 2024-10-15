package top.eazed.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.eazed.forum.entity.dto.TopicComment;

@Mapper
public interface TopicCommentMapper extends BaseMapper<TopicComment> {

}