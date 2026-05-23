package com.kocaeli.kargo.controller;

import com.kocaeli.kargo.model.Road;
import com.kocaeli.kargo.model.Station;
import com.kocaeli.kargo.repository.StationRepository;
import com.kocaeli.kargo.service.RoadDistanceService;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stations")
@CrossOrigin(origins = "*")
public class StationController {

    private final StationRepository stationRepository;
    private final RoadDistanceService roadDistanceService;

    public StationController(StationRepository stationRepository, RoadDistanceService roadDistanceService) {
        this.stationRepository = stationRepository;
        this.roadDistanceService = roadDistanceService;
    }

    // Yeni istasyon ekle (Admin)
    /* StationController.java - addStation metodunu güncelleyin */

/* StationController.java - addStation metodunu güncelleyin */
@PostMapping
public Station addStation(@RequestBody Station newStation) {
    Station savedStation = stationRepository.save(newStation);
    List<Station> allStations = stationRepository.findAll();
    
    // 1. Tüm durakları mesafeleriyle birlikte bir listeye alalım
    List<Map.Entry<Station, Double>> nearbyStations = new ArrayList<>();

    for (Station s : allStations) {
        if (!s.getId().equals(savedStation.getId())) {
            double dist = roadDistanceService.calculateDistance(
                savedStation.getLatitude(), savedStation.getLongitude(),
                s.getLatitude(), s.getLongitude()
            );
            nearbyStations.add(new AbstractMap.SimpleEntry<>(s, dist));
        }
    }

    // 2. Mesafeye göre küçükten büyüğe sırala (En yakın en başa gelir)
    nearbyStations.sort(Comparator.comparingDouble(Map.Entry::getValue));

    // 3. Sadece en yakın 2 durağa bağlan (Böylece atlama yapmaz)
    int count = 0;
    for (Map.Entry<Station, Double> entry : nearbyStations) {
        if (count >= 2) break; // En yakın 2 bağlantı yeterli
        
        roadDistanceService.addDynamicRoad(
            savedStation.getName(), 
            entry.getKey().getName(), 
            entry.getValue()
        );
        count++;
        System.out.println("🔗 Mantıklı Bağlantı: " + savedStation.getName() + " -> " + entry.getKey().getName());
    }

    return savedStation;
}

// Yardımcı metot: Hiç yakın durak yoksa en yakındakini bulur
private void connectToNearestOnly(Station savedStation, List<Station> allStations) {
    Station nearest = null;
    double min = Double.MAX_VALUE;
    for (Station s : allStations) {
        if (!s.getId().equals(savedStation.getId())) {
            double d = roadDistanceService.calculateDistance(savedStation.getLatitude(), savedStation.getLongitude(), s.getLatitude(), s.getLongitude());
            if (d < min) { min = d; nearest = s; }
        }
    }
    if (nearest != null) {
        roadDistanceService.addDynamicRoad(savedStation.getName(), nearest.getName(), min);
    }
}
    // Tüm istasyonları listele
    @GetMapping
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
    @PostMapping("/roads")
    public String addRoad(@RequestParam String from, @RequestParam String to, @RequestParam double dist) {
        try {
            // RoadDistanceService içindeki yeni metodu çağırıyoruz
            roadDistanceService.addDynamicRoad(from, to, dist); 
            return from + " ve " + to + " arasında " + dist + " km mesafe başarıyla tanımlandı.";
        } catch (Exception e) {
            return "Hata oluştu: " + e.getMessage();
        }
    }
}
