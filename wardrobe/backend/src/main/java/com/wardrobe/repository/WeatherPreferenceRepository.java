package com.wardrobe.repository;

import com.wardrobe.entity.WeatherPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherPreferenceRepository extends JpaRepository<WeatherPreference, Long> {

    Optional<WeatherPreference> findByUserId(Long userId);
}
