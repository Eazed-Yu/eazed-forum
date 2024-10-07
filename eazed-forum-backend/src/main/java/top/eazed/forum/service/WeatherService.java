package top.eazed.forum.service;

import top.eazed.forum.entity.vo.response.WeatherVO;

public interface WeatherService {
    WeatherVO fetchWeather(double longitude, double latitude);
}
