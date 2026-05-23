package com.kocaeli.kargo.controller;

import com.kocaeli.kargo.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/settings")
@CrossOrigin(origins = "*") // Frontend'den erişim için
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    // 📍 Mevcut tüm ayarları getirir
   /* SettingsController.java - getAllSettings metodunu güncelleyin */
@GetMapping
public Map<String, Double> getAllSettings() {
    Map<String, Double> settings = new HashMap<>();
    settings.put("fuel_cost", settingsService.getParam("fuel_cost", 1.0));
    settings.put("rental_fee", settingsService.getParam("rental_fee", 200.0));
    settings.put("free_vehicle_count", settingsService.getParam("free_vehicle_count", 3.0));
    return settings;
}

    // 📍 Belirli bir ayarı günceller (Örn: /settings/update?key=fuel_cost&value=1.5)
    @PostMapping("/update")
    public String updateSetting(@RequestParam String key, @RequestParam Double value) {
        try {
            settingsService.updateParam(key, value);
            return key + " başarıyla " + value + " olarak güncellendi.";
        } catch (Exception e) {
            return "Hata oluştu: " + e.getMessage();
        }
    }
}