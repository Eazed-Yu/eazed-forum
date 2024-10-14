package top.eazed.forum.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.eazed.forum.entity.dto.*;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.entity.vo.response.TopicDetailVO;
import top.eazed.forum.entity.vo.response.TopicPreviewVO;
import top.eazed.forum.entity.vo.response.TopicTopVO;
import top.eazed.forum.mapper.*;
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
    
    @Resource
    private AccountMapper accountMapper;
    
    @Resource
    private AccountDetailsMapper detailsMapper;
    
    @Resource
    private AccountPrivacyMapper privacyMapper;
    
    
    private Set<Integer> types;
    @Autowired
    private AccountDetailsMapper accountDetailsMapper;
    @Autowired
    private AccountPrivacyMapper accountPrivacyMapper;
    
    
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
            cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
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
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNumber + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFromCache(key, TopicPreviewVO.class);
        if (list != null) {
            return list;
        }
        Page<TopicDTO> page = Page.of(pageNumber, 10);
        if (type == 0) {
            baseMapper.selectPage(page, Wrappers.<TopicDTO>query().orderByDesc("time"));
        } else {
            baseMapper.selectPage(page, Wrappers.<TopicDTO>query().eq("type", type).orderByDesc("time"));
        }
        List<TopicDTO> topics = page.getRecords();
        if (topics == null) {
            return null;
        }
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }
    
    
    @Override
    public TopicDetailVO getTopic(int tid) {
        TopicDetailVO vo = new TopicDetailVO();
        TopicDTO topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVO.User user = new TopicDetailVO.User();
        vo.setUser(this.fillUerDetailsByPrivacy(user, topic.getUid()));
        return vo;
    }
    
    
    /**
     * 根据隐私设置填充目标对象的用户详细信息。
     *
     * @param <T>    目标对象的类型。
     * @param target 要填充用户详细信息的目标对象。
     * @param uid    要获取详细信息的用户ID。
     * @return 填充了用户详细信息的目标对象。
     */
    private <T> T fillUerDetailsByPrivacy(T target, int uid) {
        // 根据用户ID获取账户详细信息
        AccountDetailsDTO accountDetailsDTO = accountDetailsMapper.selectById(uid);
        // 根据用户ID获取账户信息
        AccountDTO accountDTO = accountMapper.selectById(uid);
        // 根据用户ID获取账户隐私设置
        AccountPrivacyDTO accountPrivacyDTO = accountPrivacyMapper.selectById(uid);
        // 获取根据隐私设置要忽略的字段
        String[] ignores = accountPrivacyDTO.hiddenFields();
        // 将accountDTO的属性复制到target，忽略指定的字段
        BeanUtils.copyProperties(accountDTO, target, ignores);
        // 将accountDetailsDTO的属性复制到target，忽略指定的字段
        BeanUtils.copyProperties(accountDetailsDTO, target, ignores);
        // 返回填充了用户详细信息的目标对象
        return target;
    }
    
    private TopicPreviewVO resolveToPreview(TopicDTO topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()), vo);
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
    
    
    @Override
    public List<TopicTopVO> listTopTopics() {
        List<TopicDTO> topics = baseMapper.selectList(Wrappers.<TopicDTO>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic -> {
            TopicTopVO vo = new TopicTopVO();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }
}
