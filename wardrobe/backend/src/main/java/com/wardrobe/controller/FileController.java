package com.wardrobe.controller;

import com.wardrobe.common.BusinessException;
import com.wardrobe.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final List<String> ALLOWED = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${wardrobe.upload.path}")
    private String uploadPath;

    @Value("${wardrobe.upload.url-prefix}")
    private String urlPrefix;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        String original = file.getOriginalFilename();
        String suffix = "";
        if (original != null && original.contains(".")) {
            suffix = original.substring(original.lastIndexOf(".")).toLowerCase();
        }
        if (!ALLOWED.contains(suffix)) {
            throw new BusinessException("仅支持图片文件（jpg/jpeg/png/gif/webp）");
        }
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            Path target = Paths.get(dir.getAbsolutePath(), fileName);
            file.transferTo(target.toFile());

            Map<String, String> data = new HashMap<>();
            data.put("url", urlPrefix + "/" + fileName);
            data.put("fileName", fileName);
            return Result.ok(data);
        } catch (Exception e) {
            throw new BusinessException("上传失败：" + e.getMessage());
        }
    }
}
