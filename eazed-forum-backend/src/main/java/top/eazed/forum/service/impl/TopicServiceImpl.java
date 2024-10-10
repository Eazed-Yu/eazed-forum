package top.eazed.forum.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.eazed.forum.entity.dto.TopicDTO;
import top.eazed.forum.entity.dto.TopicTypeDTO;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.mapper.TopicMapper;
import top.eazed.forum.mapper.TopicTypeMapper;
import top.eazed.forum.service.TopicService;
import top.eazed.forum.utils.Const;
import top.eazed.forum.utils.FlowUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, TopicDTO> implements TopicService {
    
    
    @Resource
    TopicTypeMapper mapper;
    @Resource
    private FlowUtils flowUtils;
    private Set<Integer> types;
    
    @PostConstruct
    public void init() {
        types = this.listTypes().stream().map(TopicTypeDTO::getId).collect(Collectors.toSet());
    }
    
    @Override
    public List<TopicTypeDTO> listTypes() {
        return mapper.selectList(null);
    }
    
    @Override
    public String createTopic(int uid, TopicCreateVO vo) {
        if (!textLimit(vo.getContent())) {
            return "内容长度超过限制";
        }
        if (!types.contains(vo.getType())) {
            return "类型不存在";
        }
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if (!flowUtils.limitPeriodCounterCheck(key, 10, 3600)) {
            return "操作过于频繁";
        }
        TopicDTO topic = new TopicDTO();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if (this.save(topic)) {
            return null;
        } else {
            return "内部错误，请联系管理员";
        }
    }
    
    private boolean textLimit(JSONObject object) {
        if (object == null) {
            return false;
        }
        long length = 0;
        for (Object op : object.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > 20000) {
                return false;
            }
        }
        return true;
    }
}
