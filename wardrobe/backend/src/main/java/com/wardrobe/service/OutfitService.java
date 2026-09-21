package com.wardrobe.service;

import com.wardrobe.common.BusinessException;
import com.wardrobe.dto.OutfitVO;
import com.wardrobe.entity.Clothing;
import com.wardrobe.entity.Outfit;
import com.wardrobe.entity.OutfitItem;
import com.wardrobe.repository.ClothingRepository;
import com.wardrobe.repository.OutfitItemRepository;
import com.wardrobe.repository.OutfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OutfitService {

    @Autowired
    private OutfitRepository outfitRepository;

    @Autowired
    private OutfitItemRepository outfitItemRepository;

    @Autowired
    private ClothingRepository clothingRepository;

    @Autowired
    private WardrobeService wardrobeService;

    @Autowired
    private ClothingService clothingService;

    @Transactional
    public Outfit create(Long userId, Long wardrobeId, Outfit outfit, List<Long> clothingIds) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        outfit.setId(null);
        outfit.setWardrobeId(wardrobeId);
        outfit = outfitRepository.save(outfit);
        saveItems(outfit.getId(), clothingIds);
        return outfit;
    }

    @Transactional
    public Outfit update(Long userId, Long wardrobeId, Long id, Outfit patch, List<Long> clothingIds) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        Outfit existing = getOutfit(wardrobeId, id);
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getCoverImage() != null) existing.setCoverImage(patch.getCoverImage());
        outfitRepository.save(existing);
        outfitItemRepository.deleteAllByOutfitId(id);
        saveItems(id, clothingIds);
        return existing;
    }

    private void saveItems(Long outfitId, List<Long> clothingIds) {
        if (clothingIds == null) {
            return;
        }
        for (Long cid : clothingIds) {
            OutfitItem item = new OutfitItem();
            item.setOutfitId(outfitId);
            item.setClothingId(cid);
            outfitItemRepository.save(item);
        }
    }

    @Transactional
    public void delete(Long userId, Long wardrobeId, Long id) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        getOutfit(wardrobeId, id);
        outfitItemRepository.deleteAllByOutfitId(id);
        outfitRepository.deleteById(id);
    }

    public Page<Outfit> page(Long userId, Long wardrobeId, int page, int size) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        if (size <= 0 || size > 100) {
            size = 20;
        }
        Pageable pageable = PageRequest.of(page < 0 ? 0 : page, size);
        return outfitRepository.findByWardrobeIdOrderByCreateTimeDesc(wardrobeId, pageable);
    }

    public OutfitVO detail(Long userId, Long wardrobeId, Long id) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        Outfit outfit = getOutfit(wardrobeId, id);
        List<OutfitItem> items = outfitItemRepository.findByOutfitId(id);
        List<Clothing> clothes = new ArrayList<>();
        for (OutfitItem item : items) {
            clothingRepository.findById(item.getClothingId()).ifPresent(clothes::add);
        }
        OutfitVO vo = new OutfitVO();
        vo.setOutfit(outfit);
        vo.setItems(clothes);
        return vo;
    }

    /** 应用搭配：记录本次穿着，联动更新所含衣物的穿着次数与最近穿着时间 */
    @Transactional
    public void apply(Long userId, Long wardrobeId, Long id) {
        wardrobeService.assertRole(wardrobeId, userId, "OWNER", "ADMIN", "MEMBER");
        getOutfit(wardrobeId, id);
        List<OutfitItem> items = outfitItemRepository.findByOutfitId(id);
        for (OutfitItem item : items) {
            clothingService.wear(userId, wardrobeId, item.getClothingId());
        }
    }

    private Outfit getOutfit(Long wardrobeId, Long id) {
        Outfit outfit = outfitRepository.findById(id)
                .orElseThrow(() -> new BusinessException("搭配不存在"));
        if (!outfit.getWardrobeId().equals(wardrobeId)) {
            throw new BusinessException(403, "无权访问该搭配");
        }
        return outfit;
    }
}
