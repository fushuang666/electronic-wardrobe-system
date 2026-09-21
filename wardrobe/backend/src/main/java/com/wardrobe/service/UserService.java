package com.wardrobe.service;

import com.wardrobe.common.BusinessException;
import com.wardrobe.config.JwtUtil;
import com.wardrobe.entity.User;
import com.wardrobe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(String username, String password, String nickname) {
        if (username == null || username.trim().length() < 3) {
            throw new BusinessException("用户名至少 3 个字符");
        }
        if (password == null || password.length() < 6) {
            throw new BusinessException("密码至少 6 个字符");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setNickname(nickname == null || nickname.isEmpty() ? username : nickname);
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (!opt.isPresent()) {
            throw new BusinessException(401, "用户不存在");
        }
        User user = opt.get();
        if (!encoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }
        return jwtUtil.generate(user.getUsername(), user.getId());
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    public User updateProfile(Long id, String nickname, String avatar) {
        User user = getById(id);
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        return userRepository.save(user);
    }
}
