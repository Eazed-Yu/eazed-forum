package top.eazed.forum.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.eazed.forum.entity.dto.*;
import top.eazed.forum.entity.vo.request.AddCommentVO;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.entity.vo.request.TopicUpdateVO;
import top.eazed.forum.entity.vo.response.CommentVO;
import top.eazed.forum.entity.vo.response.TopicDetailVO;
import top.eazed.forum.entity.vo.response.TopicPreviewVO;
import top.eazed.forum.entity.vo.response.TopicTopVO;
import top.eazed.forum.mapper.*;
import top.eazed.forum.service.NotificationService;
import top.eazed.forum.service.TopicService;
import top.eazed.forum.utils.CacheUtils;
import top.eazed.forum.utils.Const;
import top.eazed.forum.utils.FlowUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
    
    private final Map<String, Boolean> state = new HashMap<>();
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
    
    
    private Set<Integer> types;
    @Resource
    private TopicCommentMapper commentMapper;
    @Resource
    private NotificationService notificationService;
    @Resource
    private AccountDetailsMapper accountDetailsMapper;
    
    
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
        if (!flowUtils.limitPeriodCounterCheck(key, 10000, 3600)) {
            return "操作过于频繁";
        }
        TopicDTO topic = new TopicDTO();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        StringBuffer stringBuffer = new StringBuffer();
        JSONArray ops = vo.getContent().getJSONArray("ops");
        for (Object op : ops) {
            JSONObject object = JSONObject.from(op);
            if (object.containsKey("insert")) {
                stringBuffer.append(object.getString("insert"));
            }
        }
        topic.setText(stringBuffer.toString());
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
    @Resource
    private AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    public TopicDetailVO getTopic(int tid, int uid) {
        TopicDetailVO vo = new TopicDetailVO();
        TopicDTO topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVO.Interact interact = new TopicDetailVO.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        TopicDetailVO.User user = new TopicDetailVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, topic.getUid()));
        vo.setComments(commentMapper.selectCount(Wrappers.<TopicComment>query().eq("tid", tid)));
        return vo;
    }
    private <T> T fillUserDetailsByPrivacy(T target, int uid) {
        AccountDetailsDTO details = accountDetailsMapper.selectById(uid);
        AccountDTO account = accountMapper.selectById(uid);
        AccountPrivacyDTO accountPrivacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(details, target, ignores);
        return target;
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
    
    private boolean hasInteract(int tid, int uid, String type) {
        String key = tid + ":" + uid;
        if (stringRedisTemplate.opsForHash().hasKey(type, key))
            return Boolean.parseBoolean(stringRedisTemplate.opsForHash().entries(type).get(key).toString());
        return baseMapper.userInteractCount(tid, uid, type) > 0;
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
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));
        vo.setLike(baseMapper.interactCount(topic.getId(), "collect"));
        return vo;
    }
    
    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()) {
            stringRedisTemplate.opsForHash().put(type, interact.toKey(), Boolean.toString(state));
            this.saveInteractSchedule(type);
        }
    }
    
    @Override
    public List<TopicPreviewVO> listTopicCollects(int uid) {
        return baseMapper.collectTopics(uid)
                .stream()
                .map(topic -> {
                    TopicPreviewVO vo = new TopicPreviewVO();
                    BeanUtils.copyProperties(topic, vo);
                    return vo;
                })
                .toList();
    }
    
    @Override
    public String updateTopic(int uid, TopicUpdateVO vo) {
        if (!textLimitCheck(vo.getContent(), 20000))
            return "文章内容太多，发文失败！";
        if (!types.contains(vo.getType()))
            return "文章类型非法！";
        baseMapper.update(null, Wrappers.<TopicDTO>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toString())
                .set("type", vo.getType())
        );
        return null;
    }
    
    @Override
    public List<CommentVO> comments(int tid, int pageNumber) {
        Page<TopicComment> page = Page.of(pageNumber, 10);
        commentMapper.selectPage(page, Wrappers.<TopicComment>query().eq("tid", tid));
        return page.getRecords().stream().map(dto -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(dto, vo);
            if (dto.getQuote() > 0) {
                TopicComment comment = commentMapper.selectOne(Wrappers.<TopicComment>query()
                        .eq("id", dto.getQuote()).orderByAsc("time"));
                if (comment != null) {
                    JSONObject object = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"), builder, ignore -> {
                    });
                    vo.setQuote(builder.toString());
                } else {
                    vo.setQuote("此评论已被删除");
                }
            }
            CommentVO.User user = new CommentVO.User();
            this.fillUserDetailsByPrivacy(user, dto.getUid());
            vo.setUser(user);
            return vo;
        }).toList();
    }
    
    @Override
    public String createComment(int uid, AddCommentVO vo) {
        if (!textLimitCheck(JSONObject.parseObject(vo.getContent()), 2000))
            return "评论内容太多，发表失败！";
        String key = Const.FORUM_TOPIC_COMMENT_COUNTER + uid;
        if (!flowUtils.limitPeriodCounterCheck(key, 20000, 60))
            return "发表评论频繁，请稍后再试！";
        TopicComment comment = new TopicComment();
        comment.setUid(uid);
        BeanUtils.copyProperties(vo, comment);
        comment.setTime(new Date());
        commentMapper.insert(comment);
        TopicDTO topic = baseMapper.selectById(vo.getTid());
        AccountDTO account = accountMapper.selectById(uid);
        if (vo.getQuote() > 0) {
            TopicComment com = commentMapper.selectById(vo.getQuote());
            if (!Objects.equals(account.getId(), com.getUid())) {
                notificationService.addNotification(
                        com.getUid(),
                        "您有新的帖子评论回复",
                        account.getUsername() + " 回复了你发表的评论，快去看看吧！",
                        "success", "/index/topic-detail/" + com.getTid()
                );
            }
        } else if (!Objects.equals(account.getId(), topic.getUid())) {
            notificationService.addNotification(
                    topic.getUid(),
                    "您有新的帖子回复",
                    account.getUsername() + " 回复了你发表主题: " + topic.getTitle() + "，快去看看吧！",
                    "success", "/index/topic-detail/" + topic.getId()
            );
        }
        return null;
    }
    
    @Override
    public void deleteComment(int id, int uid) {
        commentMapper.delete(Wrappers.<TopicComment>query().eq("id", id).eq("uid", uid));
    }

    private void saveInteractSchedule(String type) {
        if (!state.getOrDefault(type, false)) {
            state.put(type, true);
            service.schedule(() -> {
                this.saveInteract(type);
                state.put(type, false);
            }, 3, TimeUnit.SECONDS);
        }
    }
    
    private void saveInteract(String type) {
        synchronized (type.intern()) {
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            stringRedisTemplate.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString()))
                    check.add(Interact.parseInteract(k.toString(), type));
                else
                    uncheck.add(Interact.parseInteract(k.toString(), type));
            });
            if (!check.isEmpty())
                baseMapper.addInteract(check, type);
            if (!uncheck.isEmpty())
                baseMapper.deleteInteract(uncheck, type);
            stringRedisTemplate.delete(type);
        }
    }
    
    private boolean textLimitCheck(JSONObject object, int max) {
        if (object == null) return false;
        long length = 0;
        for (Object op : object.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > max) return false;
        }
        return true;
    }
    
    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler) {
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text) {
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image")).ifPresent(imageHandler);
            }
        }
    }
}
