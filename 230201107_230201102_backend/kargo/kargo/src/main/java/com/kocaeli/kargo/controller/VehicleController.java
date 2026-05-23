package com.kocaeli.kargo.controller;

import com.kocaeli.kargo.model.Vehicle;
import com.kocaeli.kargo.repository.VehicleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    
    /* VehicleController.java içine ekleyin */

@PostMapping("/update-vehicle")
public String updateVehicle(@RequestParam Long id, 
                             @RequestParam String name, 
                             @RequestParam Double capacity) {
    // 📍 1. findById bir 'Optional' döner, bu yüzden .map() ve .orElse() kullanılmalıdır
    return vehicleRepository.findById(id).map(vehicle -> {
        // 📍 2. Setter isimlerinin Entity (Model) sınıfınızla birebir aynı olması gerekir
        vehicle.setName(name);
        
        // Eğer hata alıyorsanız burayı 'setCapacity' veya 'setCapacityKg' olarak deneyin
        // RouteOptimizationService'e göre doğrusu: setCapacityKg
        vehicle.setCapacityKg(capacity); 
        
        vehicleRepository.save(vehicle); // 📍 3. Değişikliği DB'ye kaydet
        
        return "Araç (ID: " + id + ") başarıyla güncellendi."; // map içindeki return
    }).orElse("Hata: " + id + " ID'li araç bulunamadı!"); // Eğer araç yoksa dönecek mesaj
}

    // Tüm araçlar
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // Sadece müsait araçlar
    @GetMapping("/available")
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByAvailableTrue();
    }
}
