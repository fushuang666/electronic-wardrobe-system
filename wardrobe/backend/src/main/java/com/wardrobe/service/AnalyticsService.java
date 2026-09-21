package com.wardrobe.service;

import com.wardrobe.common.BusinessException;
import com.wardrobe.entity.Clothing;
import com.wardrobe.repository.ClothingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private ClothingRepository clothingRepository;

    @Autowired
    private WardrobeService wardrobeService;

    /** 闲置判定阈值（天） */
    private static final int IDLE_DAYS = 90;

    public Map<String, Object> stats(Long userId, Long wardrobeId) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        List<Clothing> list = clothingRepository.findAllByWardrobeId(wardrobeId);

        Map<String, Long> byCategory = countBy(list, Clothing::getCategory);
        Map<String, Long> bySeason = countBy(list, Clothing::getSeason);
        Map<String, Long> byColor = countBy(list, Clothing::getColor);

        List<Map<String, Object>> topWorn = list.stream()
                .filter(c -> c.getWearCount() != null && c.getWearCount() > 0)
                .sorted((a, b) -> b.getWearCount().compareTo(a.getWearCount()))
                .limit(5)
                .map(c -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", c.getId());
                    m.put("name", c.getName());
                    m.put("wearCount", c.getWearCount());
                    return m;
                })
                .collect(Collectors.toList());

        long idle = list.stream().filter(this::isIdle).count();
        double idleRate = list.isEmpty() ? 0 : (double) idle / list.size() * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("total", list.size());
        result.put("byCategory", byCategory);
        result.put("bySeason", bySeason);
        result.put("byColor", byColor);
        result.put("topWorn", topWorn);
        result.put("idleCount", idle);
        result.put("idleRate", Math.round(idleRate * 10) / 10.0);
        return result;
    }

    private Map<String, Long> countBy(List<Clothing> list, java.util.function.Function<Clothing, String> fn) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Clothing c : list) {
            String key = fn.apply(c);
            if (key == null || key.isEmpty()) {
                key = "未分类";
            }
            map.put(key, map.getOrDefault(key, 0L) + 1);
        }
        return map;
    }

    private boolean isIdle(Clothing c) {
        if (c.getLastWearDate() == null) {
            // 购买超过阈值且从未穿着
            if (c.getPurchaseDate() != null
                    && ChronoUnit.DAYS.between(c.getPurchaseDate(), LocalDateTime.now()) > IDLE_DAYS) {
                return true;
            }
            return c.getWearCount() == null || c.getWearCount() == 0;
        }
        return ChronoUnit.DAYS.between(c.getLastWearDate(), LocalDateTime.now()) > IDLE_DAYS;
    }
}
