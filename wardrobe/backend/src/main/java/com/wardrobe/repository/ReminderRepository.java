package com.wardrobe.repository;

import com.wardrobe.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Page<Reminder> findByWardrobeIdOrderByCreateTimeDesc(Long wardrobeId, Pageable pageable);

    List<Reminder> findByWardrobeIdAndStatus(Long wardrobeId, String status);

    long countByWardrobeIdAndType(Long wardrobeId, String type);

    void deleteByWardrobeId(Long wardrobeId);
}
