package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.entity.User;
import com.wardrobe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestParam String username,
                                                @RequestParam String password,
                                                @RequestParam(required = false) String nickname) {
        User user = userService.register(username, password, nickname);
        String token = userService.login(username, password);
        return Result.ok(build(user, token));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String username,
                                             @RequestParam String password) {
        String token = userService.login(username, password);
        User user = userService.getByUsername(username);
        return Result.ok(build(user, token));
    }

    @GetMapping("/info")
    public Result<User> info(@RequestAttribute("userId") Long userId) {
        return Result.ok(userService.getById(userId));
    }

    @PutMapping("/profile")
    public Result<User> profile(@RequestAttribute("userId") Long userId,
                                @RequestParam(required = false) String nickname,
                                @RequestParam(required = false) String avatar) {
        return Result.ok(userService.updateProfile(userId, nickname, avatar));
    }

    private Map<String, Object> build(User user, String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        return map;
    }
}
