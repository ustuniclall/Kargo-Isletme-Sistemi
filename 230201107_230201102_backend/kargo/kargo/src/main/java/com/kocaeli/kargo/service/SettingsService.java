package com.kocaeli.kargo.service;

import com.kocaeli.kargo.model.SystemSetting;
import com.kocaeli.kargo.repository.SystemSettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @Autowired
    private SystemSettingRepository repository;

    // 📍 Uygulama ilk başladığında varsayılan değerleri DB'ye yazar
    @PostConstruct
    public void initDefaults() {
        if (repository.count() == 0) {
            updateParam("fuel_cost", 1.0);   // KM başına 1 birim
            updateParam("rental_fee", 200.0); // 500kg için kiralama ücreti
            updateParam("free_vehicle_count", 3.0); // Kaç araç ücretsiz olacak?
            System.out.println("✅ Genişletilmiş sistem ayarları yüklendi.");
        }
    }

    // Ayarı getirir, yoksa varsayılan döner
    public Double getParam(String key, Double defaultValue) {
        return repository.findById(key)
                .map(SystemSetting::getParamValue)
                .orElse(defaultValue);
    }

    // Ayarı günceller veya yeni ekler
    public void updateParam(String key, Double value) {
        SystemSetting setting = new SystemSetting(key, value);
        repository.save(setting);
    }
}