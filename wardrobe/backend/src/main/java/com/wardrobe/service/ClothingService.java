package com.wardrobe.service;

import com.wardrobe.common.BusinessException;
import com.wardrobe.entity.Clothing;
import com.wardrobe.repository.ClothingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class ClothingService {

    @Autowired
    private ClothingRepository clothingRepository;

    @Autowired
    private WardrobeService wardrobeService;

    @Transactional
    public Clothing create(Long userId, Long wardrobeId, Clothing clothing) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        clothing.setId(null);
        clothing.setWardrobeId(wardrobeId);
        clothing.setWearCount(0);
        return clothingRepository.save(clothing);
    }

    @Transactional
    public Clothing update(Long userId, Long wardrobeId, Long id, Clothing patch) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        Clothing existing = get(userId, wardrobeId, id);
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getCategory() != null) existing.setCategory(patch.getCategory());
        if (patch.getSeason() != null) existing.setSeason(patch.getSeason());
        if (patch.getColor() != null) existing.setColor(patch.getColor());
        if (patch.getStyle() != null) existing.setStyle(patch.getStyle());
        if (patch.getOccasion() != null) existing.setOccasion(patch.getOccasion());
        if (patch.getImageUrl() != null) existing.setImageUrl(patch.getImageUrl());
        if (patch.getBrand() != null) existing.setBrand(patch.getBrand());
        if (patch.getPrice() != null) existing.setPrice(patch.getPrice());
        if (patch.getPurchaseDate() != null) existing.setPurchaseDate(patch.getPurchaseDate());
        if (patch.getNote() != null) existing.setNote(patch.getNote());
        return clothingRepository.save(existing);
    }

    @Transactional
    public void delete(Long userId, Long wardrobeId, Long id) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        get(userId, wardrobeId, id);
        clothingRepository.deleteById(id);
    }

    public Clothing get(Long userId, Long wardrobeId, Long id) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        Clothing clothing = clothingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("衣物不存在"));
        if (!clothing.getWardrobeId().equals(wardrobeId)) {
            throw new BusinessException(403, "无权访问该衣物");
        }
        return clothing;
    }

    public Page<Clothing> page(Long userId, Long wardrobeId, String category, String season,
                               String color, String style, String occasion, String keyword,
                               int page, int size) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        if (size <= 0 || size > 100) {
            size = 20;
        }
        Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size);
        return clothingRepository.search(wardrobeId, category, season, color, style, occasion, keyword, pageable);
    }

    @Transactional
    public Clothing wear(Long userId, Long wardrobeId, Long id) {
        Clothing clothing = get(userId, wardrobeId, id);
        clothing.setWearCount((clothing.getWearCount() == null ? 0 : clothing.getWearCount()) + 1);
        clothing.setLastWearDate(LocalDateTime.now());
        return clothingRepository.save(clothing);
    }
}
