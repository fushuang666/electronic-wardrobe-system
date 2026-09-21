package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.dto.WeatherVO;
import com.wardrobe.entity.WeatherPreference;
import com.wardrobe.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public Result<WeatherVO> get(@RequestAttribute("userId") Long userId,
                                 @RequestParam(required = false) String city) {
        return Result.ok(weatherService.getWeather(userId, city));
    }

    @GetMapping("/preference")
    public Result<WeatherPreference> preference(@RequestAttribute("userId") Long userId) {
        return Result.ok(weatherService.getPreference(userId));
    }

    @PostMapping("/preference")
    public Result<Void> savePreference(@RequestAttribute("userId") Long userId,
                                      @RequestParam String city,
                                      @RequestParam(required = false) String cityCode) {
        weatherService.savePreference(userId, city, cityCode);
        return Result.ok();
    }
}
