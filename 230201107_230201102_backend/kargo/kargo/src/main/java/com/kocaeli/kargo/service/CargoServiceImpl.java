package com.kocaeli.kargo.service;

import com.kocaeli.kargo.model.*;
import com.kocaeli.kargo.repository.CargoRepository;
import com.kocaeli.kargo.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CargoServiceImpl implements CargoService {

    private final CargoRepository cargoRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteOptimizationService routeOptimizationService;

    public CargoServiceImpl(CargoRepository cargoRepository,
                            VehicleRepository vehicleRepository,
                            RouteOptimizationService routeOptimizationService) {
        this.cargoRepository = cargoRepository;
        this.vehicleRepository = vehicleRepository;
        this.routeOptimizationService = routeOptimizationService;
    }

@Override
@Transactional
public OptimizationResult assignCargos(LocalDate date) {
    // 📍 Sadece seçilen tarihteki kargoları getir
    List<Cargo> pending = cargoRepository.findByShipmentDateAndVehicleIsNull(date);
    if (pending.isEmpty()) return null;

    List<Vehicle> main = vehicleRepository.findByAvailableTrue().stream().filter(v -> !v.isRented()).toList();
// 0 yerine 0.0 yazarak Double tipine uyumlu hale getiriyoruz
for (Vehicle v : main) v.setUsedCapacityKg(0.0);

    OptimizationResult result = routeOptimizationService.optimize(pending, new ArrayList<>(main));

    vehicleRepository.saveAll(result.getRentedVehicles());
    cargoRepository.deleteAll(pending);

    Map<String, Cargo> combinedParts = new HashMap<>();
   // CargoServiceImpl.java içindeki döngüyü şu şekilde güncelleyin:
/* CargoServiceImpl.java içindeki döngünün düzeltilmiş hali */
for (RouteOptimizationService.AssignmentPlan plan : result.getAssignmentPlans()) {
    String key = plan.originalCargo.getId() + "-" + plan.vehicle.getName();
    
    if (combinedParts.containsKey(key)) {
        Cargo existing = combinedParts.get(key);
        existing.setQuantity(existing.getQuantity() + plan.quantity);
        existing.setWeight(existing.getTotalWeight() + plan.totalWeight);
    } else {
        Cargo part = new Cargo();
        part.setUser(plan.originalCargo.getUser());
        part.setDestination(plan.originalCargo.getDestination());
        part.setQuantity(plan.quantity);
        part.setWeight(plan.totalWeight);
        part.setVehicle(plan.vehicle);
        part.setAssigned(true);
        part.setShipmentDate(date); 
        
        // 📍 YAKIT SORUNU ÇÖZÜMÜ: Mesafeyi plana göre set et
        part.setTripDistance(plan.vehicleTotalKm); // Bu satır eksikti

        if (plan.routePath != null && !plan.routePath.isEmpty()) {
            part.setRoutePath(String.join(" → ", plan.routePath));
        }

        if (plan.routeCoordinates != null && !plan.routeCoordinates.isEmpty()) {
            String coordsStr = plan.routeCoordinates.stream()
                .map(c -> c[0] + "," + c[1])
                .collect(java.util.stream.Collectors.joining("|"));
            part.setRouteCoordinates(coordsStr);
        }

        combinedParts.put(key, part);
    }
}
    cargoRepository.saveAll(combinedParts.values());
    vehicleRepository.saveAll(main);
    return result;
}
    /**
     * Optimizasyon sonuçlarını (Araç dolulukları ve Kargo atamaları) DB'ye yansıtır.
     */
    private void saveResults(OptimizationResult result) {
        // Ana araçları güncelle
        vehicleRepository.saveAll(result.getMainVehicles());

        // Eğer yeni kiralık araçlar oluştuysa onları kaydet
        if (!result.getRentedVehicles().isEmpty()) {
            vehicleRepository.saveAll(result.getRentedVehicles());
        }

        // Ana araçlara atanan kargoları güncelle
        for (Vehicle v : result.getMainVehicles()) {
            updateCargoAssignments(v);
        }

        // Kiralık araçlara atanan kargoları güncelle
        for (Vehicle v : result.getRentedVehicles()) {
            updateCargoAssignments(v);
        }

        System.out.println("✅ Atama işlemi tamamlandı. Toplam Maliyet: " + result.getTotalCost());
    }

    private void updateCargoAssignments(Vehicle vehicle) {
        // RouteOptimizationService içinde kargo nesnesine araç set edildiği için
        // sadece atanmış (assigned) bayrağını işaretleyip kaydediyoruz.
        List<Cargo> allCargos = cargoRepository.findAll();
        for (Cargo c : allCargos) {
            if (c.getVehicle() != null && c.getVehicle().getName().equals(vehicle.getName())) {
                c.setAssigned(true);
                cargoRepository.save(c);
            }
        }
    }
}