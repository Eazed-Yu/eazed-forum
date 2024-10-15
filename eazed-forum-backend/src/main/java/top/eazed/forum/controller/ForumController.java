package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.entity.dto.Interact;
import top.eazed.forum.entity.vo.request.AddCommentVO;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.entity.vo.request.TopicUpdateVO;
import top.eazed.forum.entity.vo.response.*;
import top.eazed.forum.service.TopicService;
import top.eazed.forum.service.WeatherService;
import top.eazed.forum.utils.Const;
import top.eazed.forum.utils.ControllerUtils;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    
    @Resource
    WeatherService weatherService;
    
    @Resource
    TopicService topicService;
    
    @Resource
    ControllerUtils utils;
    
    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO weather = weatherService.fetchWeather(longitude, latitude);
        if (weather == null) {
            return RestBean.failure(500, "获取天气信息失败");
        }
        return RestBean.success(weather);
    }
    
    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listTopic() {
        return RestBean.success(topicService.listTypes().stream().map(type -> type.asViewObject(TopicTypeVO.class)).toList());
    }
    
    
    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return utils.messageHandle(() -> topicService.createTopic(uid, vo));
    }
    
    
    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int page,
                                                    @RequestParam @Min(0) int type) {
        return RestBean.success(topicService.listTopicByPage(page + 1, type));
    }
    
    @GetMapping("/topic")
    public RestBean<TopicDetailVO> topic(@RequestParam @Min(0) int tid,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return RestBean.success(topicService.getTopic(tid, id));
    }
    
    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVO>> topTopic() {
        return RestBean.success(topicService.listTopTopics());
    }
    
    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid,
                                   @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute(Const.ATTR_USER_ID) int id) {
        topicService.interact(new Interact(tid, id, new Date(), type), state);
        return RestBean.success();
    }
    
    @GetMapping("/collects")
    public RestBean<List<TopicPreviewVO>> collects(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        return RestBean.success(topicService.listTopicCollects(id));
    }
    
    @PostMapping("/update-topic")
    public RestBean<Void> updateTopic(@Valid @RequestBody TopicUpdateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return utils.messageHandle(() -> topicService.updateTopic(id, vo));
    }
    
    @PostMapping("/add-comment")
    public RestBean<Void> addComment(@Valid @RequestBody AddCommentVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return utils.messageHandle(() -> topicService.createComment(id, vo));
    }
    
    @GetMapping("/comments")
    public RestBean<List<CommentVO>> comments(@RequestParam @Min(0) int tid,
                                              @RequestParam @Min(0) int page) {
        return RestBean.success(topicService.comments(tid, page + 1));
    }
    
    @GetMapping("/delete-comment")
    public RestBean<Void> deleteComment(@RequestParam @Min(0) int id,
                                        @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        topicService.deleteComment(id, uid);
        return RestBean.success();
    }
}
