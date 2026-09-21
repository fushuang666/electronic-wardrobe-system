package com.wardrobe.service;

import com.wardrobe.common.BusinessException;
import com.wardrobe.dto.WeatherVO;
import com.wardrobe.entity.Reminder;
import com.wardrobe.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private WardrobeService wardrobeService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private WeatherService weatherService;

    public Page<Reminder> list(Long userId, Long wardrobeId, int page, int size) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        if (size <= 0 || size > 100) {
            size = 20;
        }
        Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size);
        return reminderRepository.findByWardrobeIdOrderByCreateTimeDesc(wardrobeId, pageable);
    }

    @Transactional
    public void markRead(Long userId, Long wardrobeId, Long id) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        Reminder r = reminderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("提醒不存在"));
        if (!r.getWardrobeId().equals(wardrobeId)) {
            throw new BusinessException(403, "无权操作");
        }
        r.setStatus("READ");
        reminderRepository.save(r);
    }

    /** 根据当前衣柜数据生成场景提醒（先清除未读旧提醒再重新生成） */
    @Transactional
    public List<Reminder> generate(Long userId, Long wardrobeId) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        reminderRepository.deleteAll(reminderRepository.findByWardrobeIdAndStatus(wardrobeId, "UNREAD"));
        List<Reminder> created = new ArrayList<>();

        Map<String, Object> stats = analyticsService.stats(userId, wardrobeId);

        // 1. 闲置提醒
        long idle = ((Number) stats.get("idleCount")).longValue();
        if (idle > 0) {
            created.add(make(wardrobeId, "IDLE", "闲置衣物提醒",
                    "当前有 " + idle + " 件衣物已超过 90 天未穿着，建议清理或重新搭配穿着。"));
        }

        // 2. 不均衡提醒
        @SuppressWarnings("unchecked")
        Map<String, Long> byCategory = (Map<String, Long>) stats.get("byCategory");
        if (byCategory != null && byCategory.size() >= 2) {
            Map.Entry<String, Long> max = null;
            Map.Entry<String, Long> min = null;
            for (Map.Entry<String, Long> e : byCategory.entrySet()) {
                if (max == null || e.getValue() > max.getValue()) max = e;
                if (min == null || e.getValue() < min.getValue()) min = e;
            }
            if (max != null && min != null && min.getValue() > 0
                    && max.getValue() >= min.getValue() * 3) {
                created.add(make(wardrobeId, "IMBALANCE", "衣橱不均衡提醒",
                        "「" + max.getKey() + "」类衣物（" + max.getValue() + " 件）明显偏多，而「"
                                + min.getKey() + "」类仅 " + min.getValue() + " 件，建议均衡购置。"));
            }
        }

        // 3. 天气提醒
        WeatherVO weather = weatherService.getWeather(userId, null);
        if (weather != null) {
            boolean cold = weather.getTemp() != null && weather.getTemp() <= 5;
            boolean rain = weather.getCondition() != null && weather.getCondition().contains("雨");
            if (cold || rain) {
                created.add(make(wardrobeId, "WEATHER", "天气穿衣提醒",
                        weather.getCity() + " 当前" + weather.getCondition()
                                + "，气温 " + Math.round(weather.getTemp()) + "℃，"
                                + weather.getSuggestion()));
            }
        }

        // 4. 搭配提醒
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topWorn = (List<Map<String, Object>>) stats.get("topWorn");
        if (topWorn != null && !topWorn.isEmpty()) {
            Map<String, Object> top = topWorn.get(0);
            created.add(make(wardrobeId, "OUTFIT", "可复用搭配提醒",
                    "你最常穿的「" + top.get("name") + "」已穿着 " + top.get("wearCount") + " 次，今天可继续复用。"));
        }

        if (!created.isEmpty()) {
            reminderRepository.saveAll(created);
        }
        return created;
    }

    private Reminder make(Long wardrobeId, String type, String title, String content) {
        Reminder r = new Reminder();
        r.setWardrobeId(wardrobeId);
        r.setType(type);
        r.setTitle(title);
        r.setContent(content);
        r.setStatus("UNREAD");
        return r;
    }
}
