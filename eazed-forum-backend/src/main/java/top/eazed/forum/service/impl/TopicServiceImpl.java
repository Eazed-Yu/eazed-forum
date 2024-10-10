package top.eazed.forum.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.eazed.forum.entity.dto.TopicDTO;
import top.eazed.forum.entity.dto.TopicTypeDTO;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.entity.vo.response.TopicPreviewVO;
import top.eazed.forum.mapper.TopicMapper;
import top.eazed.forum.mapper.TopicTypeMapper;
import top.eazed.forum.service.TopicService;
import top.eazed.forum.utils.CacheUtils;
import top.eazed.forum.utils.Const;
import top.eazed.forum.utils.FlowUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, TopicDTO> implements TopicService {
    
    
    @Resource
    TopicTypeMapper mapper;
    @Resource
    private FlowUtils flowUtils;
    
    @Resource
    private CacheUtils cacheUtils;
    
    
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
            cacheUtils.deleteCache(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
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
    
    @Override
    public List<TopicPreviewVO> listTopicByPage(int page, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + page + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFromCache(key, TopicPreviewVO.class);
        if (list != null) {
            return list;
        }
        List<TopicDTO> topics;
        if (type == 0) {
            topics = baseMapper.topicList(page * 10);
        } else {
            topics = baseMapper.topicListByType(page * 10, type);
        }
        if (topics == null) {
            return null;
        }
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }
    
    private TopicPreviewVO resolveToPreview(TopicDTO topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(topic, vo);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String) {
                previewText.append(insert);
            } else {
                Optional.ofNullable(JSONObject.from(insert).getString("image")).ifPresent(images::add);
            }
        }
        // 截取预览文本
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }
}
