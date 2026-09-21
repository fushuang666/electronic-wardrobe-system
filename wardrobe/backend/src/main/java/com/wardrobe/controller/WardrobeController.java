package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.dto.WardrobeVO;
import com.wardrobe.entity.Wardrobe;
import com.wardrobe.entity.WardrobeMember;
import com.wardrobe.service.WardrobeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wardrobes")
public class WardrobeController {

    @Autowired
    private WardrobeService wardrobeService;

    @PostMapping
    public Result<Wardrobe> create(@RequestAttribute("userId") Long userId,
                                   @RequestParam String name,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false, defaultValue = "PERSONAL") String type) {
        return Result.ok(wardrobeService.create(userId, name, description, type));
    }

    @GetMapping
    public Result<List<WardrobeVO>> list(@RequestAttribute("userId") Long userId) {
        return Result.ok(wardrobeService.listForUser(userId));
    }

    @GetMapping("/{id}")
    public Result<Wardrobe> get(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        wardrobeService.assertRole(id, userId, "OWNER", "ADMIN", "MEMBER");
        return Result.ok(wardrobeService.get(id));
    }

    @PutMapping("/{id}")
    public Result<Wardrobe> update(@RequestAttribute("userId") Long userId, @PathVariable Long id,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String description) {
        return Result.ok(wardrobeService.update(id, userId, name, description));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        wardrobeService.delete(id, userId);
        return Result.ok();
    }

    @PostMapping("/{id}/members")
    public Result<WardrobeMember> addMember(@RequestAttribute("userId") Long userId, @PathVariable Long id,
                                            @RequestParam String username,
                                            @RequestParam(required = false, defaultValue = "MEMBER") String role) {
        return Result.ok(wardrobeService.addMember(id, userId, username, role));
    }

    @DeleteMapping("/{id}/members/{targetUserId}")
    public Result<Void> removeMember(@RequestAttribute("userId") Long userId, @PathVariable Long id,
                                     @PathVariable Long targetUserId) {
        wardrobeService.removeMember(id, userId, targetUserId);
        return Result.ok();
    }

    @GetMapping("/{id}/members")
    public Result<List<WardrobeMember>> members(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        wardrobeService.assertRole(id, userId, "OWNER", "ADMIN", "MEMBER");
        return Result.ok(wardrobeService.listMembers(id));
    }
}
