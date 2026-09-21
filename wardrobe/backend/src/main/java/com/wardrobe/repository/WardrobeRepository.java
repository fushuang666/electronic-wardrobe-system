package com.wardrobe.repository;

import com.wardrobe.entity.Wardrobe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardrobeRepository extends JpaRepository<Wardrobe, Long> {

    List<Wardrobe> findByOwnerId(Long ownerId);

    List<Wardrobe> findByType(String type);
}
