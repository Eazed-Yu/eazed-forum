package top.eazed.forum.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.eazed.forum.entity.RestBean;
import top.eazed.forum.entity.vo.response.WeatherVO;
import top.eazed.forum.service.WeatherService;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    
    @Resource
    WeatherService weatherService;
    
    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO weather = weatherService.fetchWeather(longitude, latitude);
        if (weather == null) {
            return RestBean.failure(500, "获取天气信息失败");
        }
        return RestBean.success(weather);
        
    }
}
