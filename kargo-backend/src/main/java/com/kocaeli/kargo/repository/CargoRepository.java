package com.kocaeli.kargo.repository;

import com.kocaeli.kargo.model.Cargo;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    // Aracı olmayan kargolar (senin mevcut kodun)
    List<Cargo> findByVehicleIsNull();

    // 🔐 Sadece kullanıcıya ait kargolar
    List<Cargo> findByUser_Id(Long userId);
    
    List<Cargo> findByShipmentDateAndVehicleIsNull(LocalDate date);
    
    // 📍 YENİ: Zaten planlanmış (atanmış) kargoları görmek için
    List<Cargo> findByShipmentDateAndVehicleIsNotNull(LocalDate date);
}
