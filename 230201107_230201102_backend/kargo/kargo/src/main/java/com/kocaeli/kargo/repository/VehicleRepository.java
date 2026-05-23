package com.kocaeli.kargo.repository;

import com.kocaeli.kargo.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByAvailableTrue();
}
