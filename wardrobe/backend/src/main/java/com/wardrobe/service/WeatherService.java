package com.wardrobe.service;

import com.wardrobe.dto.WeatherVO;
import com.wardrobe.entity.WeatherPreference;
import com.wardrobe.repository.WeatherPreferenceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

@Service
public class WeatherService {

    @Value("${wardrobe.weather.api-key:}")
    private String apiKey;

    @Autowired
    private WeatherPreferenceRepository prefRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String[] MOCK_CONDITIONS = {"晴", "多云", "阴", "小雨", "雷阵雨"};

    public WeatherVO getWeather(Long userId, String cityParam) {
        String city = cityParam;
        if (city == null || city.trim().isEmpty()) {
            WeatherPreference p = prefRepo.findByUserId(userId).orElse(null);
            if (p != null && p.getCity() != null) {
                city = p.getCity();
            }
        }
        if (city == null || city.trim().isEmpty()) {
            city = "Beijing";
        }
        WeatherVO vo;
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            vo = fetchFromApi(city);
        } else {
            vo = mock(city);
        }
        vo.setSuggestion(suggest(vo.getTemp(), vo.getCondition()));
        return vo;
    }

    public void savePreference(Long userId, String city, String cityCode) {
        if (city == null || city.trim().isEmpty()) {
            throw new com.wardrobe.common.BusinessException("城市不能为空");
        }
        WeatherPreference p = prefRepo.findByUserId(userId).orElse(new WeatherPreference());
        p.setUserId(userId);
        p.setCity(city);
        p.setCityCode(cityCode);
        prefRepo.save(p);
    }

    public WeatherPreference getPreference(Long userId) {
        return prefRepo.findByUserId(userId).orElse(null);
    }

    private WeatherVO fetchFromApi(String city) {
        try {
            String encoded = URLEncoder.encode(city, "UTF-8");
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encoded
                    + "&appid=" + apiKey + "&units=metric&lang=zh_cn";
            String json = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            double temp = root.path("main").path("temp").asDouble();
            double feels = root.path("main").path("feels_like").asDouble();
            JsonNode w = root.path("weather").get(0);
            String condition = w.path("description").asText();
            String icon = w.path("icon").asText();
            WeatherVO vo = new WeatherVO();
            vo.setCity(city);
            vo.setTemp(temp);
            vo.setFeelsLike(feels);
            vo.setCondition(condition);
            vo.setIcon(icon);
            vo.setSource("api");
            return vo;
        } catch (Exception e) {
            WeatherVO m = mock(city);
            m.setSource("mock(fallback)");
            return m;
        }
    }

    private WeatherVO mock(String city) {
        Random r = new Random();
        double temp = 8 + r.nextInt(27); // 8 ~ 34 ℃
        String condition = MOCK_CONDITIONS[r.nextInt(MOCK_CONDITIONS.length)];
        WeatherVO vo = new WeatherVO();
        vo.setCity(city);
        vo.setTemp(temp);
        vo.setFeelsLike(temp - 1);
        vo.setCondition(condition);
        vo.setIcon("01d");
        vo.setSource("mock");
        return vo;
    }

    private String suggest(double temp, String condition) {
        if (condition != null && condition.contains("雨")) {
            return "今日有降雨，建议携带雨具并穿着防水外套";
        }
        if (temp <= 5) {
            return "天气寒冷，建议羽绒服/厚外套，注意保暖";
        }
        if (temp <= 15) {
            return "气温偏低，建议毛衣/卫衣搭配外套";
        }
        if (temp <= 25) {
            return "温度适宜，可穿薄外套或长袖衬衫";
        }
        return "天气炎热，建议短袖等轻薄透气衣物";
    }
}
