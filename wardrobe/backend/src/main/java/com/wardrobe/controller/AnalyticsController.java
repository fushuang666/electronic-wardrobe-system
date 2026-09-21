package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wardrobes")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/{wid}/analytics")
    public Result<Map<String, Object>> stats(@RequestAttribute("userId") Long userId,
                                             @PathVariable("wid") Long wid) {
        return Result.ok(analyticsService.stats(userId, wid));
    }
}
