package com.wardrobe.repository;

import com.wardrobe.entity.WardrobeMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WardrobeMemberRepository extends JpaRepository<WardrobeMember, Long> {

    List<WardrobeMember> findByWardrobeId(Long wardrobeId);

    List<WardrobeMember> findByUserId(Long userId);

    Optional<WardrobeMember> findByWardrobeIdAndUserId(Long wardrobeId, Long userId);
}
