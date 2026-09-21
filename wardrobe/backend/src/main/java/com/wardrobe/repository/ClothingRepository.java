package com.wardrobe.repository;

import com.wardrobe.entity.Clothing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClothingRepository extends JpaRepository<Clothing, Long> {

    Page<Clothing> findByWardrobeIdOrderByCreateTimeDesc(Long wardrobeId, Pageable pageable);

    List<Clothing> findAllByWardrobeId(Long wardrobeId);

    void deleteByWardrobeId(Long wardrobeId);

    @Query("select c from Clothing c where c.wardrobeId = :wid " +
            "and (:category is null or c.category = :category) " +
            "and (:season is null or c.season = :season) " +
            "and (:color is null or c.color = :color) " +
            "and (:style is null or c.style = :style) " +
            "and (:occasion is null or c.occasion = :occasion) " +
            "and (:keyword is null or c.name like %:keyword% " +
            "     or c.note like %:keyword% or c.brand like %:keyword%) " +
            "order by c.createTime desc")
    Page<Clothing> search(@Param("wid") Long wid,
                         @Param("category") String category,
                         @Param("season") String season,
                         @Param("color") String color,
                         @Param("style") String style,
                         @Param("occasion") String occasion,
                         @Param("keyword") String keyword,
                         Pageable pageable);
}
