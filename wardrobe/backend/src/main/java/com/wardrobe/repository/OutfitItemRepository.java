package com.wardrobe.repository;

import com.wardrobe.entity.OutfitItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutfitItemRepository extends JpaRepository<OutfitItem, Long> {

    List<OutfitItem> findByOutfitId(Long outfitId);

    List<OutfitItem> findByClothingId(Long clothingId);

    void deleteByOutfitId(Long outfitId);

    @Modifying
    @Query("delete from OutfitItem o where o.outfitId = :outfitId")
    void deleteAllByOutfitId(@Param("outfitId") Long outfitId);
}
