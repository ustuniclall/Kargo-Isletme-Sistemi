package com.kocaeli.kargo.repository;

import com.kocaeli.kargo.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    // 📍 Hata Çözümü: Bu metodu eklediğinden emin ol
    Station findByName(String name);
}