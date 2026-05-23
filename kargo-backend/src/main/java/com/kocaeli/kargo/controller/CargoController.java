package com.kocaeli.kargo.controller;

import com.kocaeli.kargo.model.*;
import com.kocaeli.kargo.repository.*;
import com.kocaeli.kargo.service.CargoService;
import org.springframework.format.annotation.DateTimeFormat; // 📍 Tarih formatı için
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // 📍 Tarih kütüphanesi
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cargos")
@CrossOrigin(origins = "*") // 📍 Frontend erişimi için origins eklendi
public class CargoController {

    private final CargoRepository cargoRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final CargoService cargoService;

    public CargoController(CargoRepository cargoRepository,
                           UserRepository userRepository,
                           StationRepository stationRepository,
                           CargoService cargoService) {
        this.cargoRepository = cargoRepository;
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.cargoService = cargoService;
    }

    // =========================
    // KARGO OLUŞTURMA
    // =========================
   @PostMapping("/send")
    public String sendCargo(@RequestBody CargoRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new RuntimeException("User bulunamadı"));

        if (!user.getFirstName().equalsIgnoreCase(req.firstName())
                || !user.getLastName().equalsIgnoreCase(req.lastName())) {
            throw new RuntimeException("Kullanıcı bilgileri uyuşmuyor");
        }

        Station station = stationRepository.findById(req.stationId())
                .orElseThrow(() -> new RuntimeException("Station bulunamadı"));

        Cargo cargo = new Cargo(
                req.quantity(),
                req.totalWeight(),
                user,
                station
        );
        
        // 📍 Kullanıcının seçtiği gönderim tarihini kaydet
        cargo.setShipmentDate(req.shipmentDate() != null ? req.shipmentDate() : LocalDate.now());
        cargo.setAssigned(false);
        
        cargoRepository.save(cargo);
        return "Kargo başarıyla gönderildi";

        // 📍 Tarih bilgisini isteğe göre veya varsayılan (bugün) olarak ata
        /*if (req.shipmentDate() != null) {
            cargo.setShipmentDate(req.shipmentDate());
        } else {
            cargo.setShipmentDate(LocalDate.now());
        }

        cargo.setAssigned(false);
        cargoRepository.save(cargo);
        return "Kargo başarıyla gönderildi";*/
    }

    // =========================
    // KULLANICI KARGOLARI
    // =========================
    @GetMapping("/my")
    public List<Cargo> getMyCargos(
            @RequestParam Long userId,
            @RequestParam String firstName,
            @RequestParam String lastName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User bulunamadı"));

        if (!user.getFirstName().equalsIgnoreCase(firstName)
                || !user.getLastName().equalsIgnoreCase(lastName)) {
            throw new RuntimeException("Kullanıcı bilgileri uyuşmuyor");
        }

        return cargoRepository.findByUser_Id(userId);
    }

    // =========================================
    // ROTA PLANLAMA (GÜN BAZLI)
    // =========================================
    @PostMapping("/assign")
    public ResponseEntity<OptimizationResult> assignCargos(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        // 📍 DÜZELTME: Artık servise URL'den gelen tarih parametresini gönderiyoruz
        OptimizationResult result = cargoService.assignCargos(date);
        
        if (result == null) {
            return ResponseEntity.ok(new OptimizationResult(new ArrayList<>(), new ArrayList<>(), 0.0));
        }
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/plans")
public List<Cargo> getActivePlans(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    // 📍 O tarihte bir araca atanmış tüm kargoları döner
    return cargoRepository.findByShipmentDateAndVehicleIsNotNull(date);
}
}