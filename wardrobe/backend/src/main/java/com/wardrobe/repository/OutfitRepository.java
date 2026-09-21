package com.wardrobe.repository;

import com.wardrobe.entity.Outfit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {

    Page<Outfit> findByWardrobeIdOrderByCreateTimeDesc(Long wardrobeId, Pageable pageable);

    void deleteByWardrobeId(Long wardrobeId);
}
