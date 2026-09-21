package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.entity.Reminder;
import com.wardrobe.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wardrobes")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @GetMapping("/{wid}/reminders")
    public Result<Page<Reminder>> list(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return Result.ok(reminderService.list(userId, wid, page, size));
    }

    @PostMapping("/{wid}/reminders/generate")
    public Result<?> generate(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid) {
        return Result.ok(reminderService.generate(userId, wid));
    }

    @PutMapping("/{wid}/reminders/{id}/read")
    public Result<Void> read(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                             @PathVariable Long id) {
        reminderService.markRead(userId, wid, id);
        return Result.ok();
    }
}
