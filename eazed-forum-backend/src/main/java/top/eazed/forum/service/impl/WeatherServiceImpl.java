package top.eazed.forum.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.eazed.forum.entity.vo.response.WeatherVO;
import top.eazed.forum.service.WeatherService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {
    
    @Value("${qweather.key}")
    String key;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    private RestTemplate restTemplate;
    
    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {
        return fetchWeatherFromCache(longitude, latitude);
    }
    
    private WeatherVO fetchWeatherFromCache(double longitude, double latitude) {
        byte[] response = restTemplate.getForObject("https://geoapi.qweather.com/v2/city/lookup?location=" +
                longitude + "," + latitude + "&key=" + key, byte[].class);
        JSONObject data = this.decompress(response);
        if (data == null) {
            return null;
        }
        JSONObject location = data.getJSONArray("location").getJSONObject(0);
        int id = location.getInteger("id");
        String key = "weather:" + id;
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (cache != null) {
            return JSONObject.parseObject(cache).to(WeatherVO.class);
        }
        WeatherVO weather = this.fetchWeatherFromApi(id, location);
        if (weather == null) {
            return null;
        }
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(weather), 1, TimeUnit.HOURS);
        return weather;
    }
    
    private WeatherVO fetchWeatherFromApi(int id, JSONObject location) {
        WeatherVO weather = new WeatherVO();
        weather.setLocation(location);
        byte[] response = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/now?location=" +
                id + "&key=" + key, byte[].class);
        JSONObject data = this.decompress(response);
        if (data == null) {
            return null;
        }
        weather.setNow(data.getJSONObject("now"));
        byte[] hourly = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + key, byte[].class);
        JSONObject hourlyData = this.decompress(hourly);
        if (hourlyData == null) {
            return null;
        }
        weather.setHourly(new JSONArray(hourlyData.getJSONArray("hourly").stream().limit(5).toList()));
        return weather;
    }
    
    private JSONObject decompress(byte[] data) {
        try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(data));
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return JSONObject.parseObject(output.toString());
        } catch (IOException e) {
            log.error("Decompress error", e);
            return null;
        }
    }
}
