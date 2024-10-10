package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.entity.vo.request.TopicCreateVO;
import top.eazed.forum.entity.vo.response.TopicPreviewVO;
import top.eazed.forum.entity.vo.response.TopicTypeVO;
import top.eazed.forum.entity.vo.response.WeatherVO;
import top.eazed.forum.service.TopicService;
import top.eazed.forum.service.WeatherService;
import top.eazed.forum.utils.Const;
import top.eazed.forum.utils.ControllerUtils;

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
        return RestBean.success(topicService.listTopicByPage(page, type));
        
        
    }
}
