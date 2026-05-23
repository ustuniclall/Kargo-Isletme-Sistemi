package com.kocaeli.kargo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_settings")
public class SystemSetting {

    @Id
    private String paramKey;   // Ayarın adı (Örn: fuel_cost, rental_fee)
    private Double paramValue; // Ayarın değeri (Örn: 1.0, 200.0)

    // Boş Constructor (JPA için zorunlu)
    public SystemSetting() {
    }

    // Parametreli Constructor (Kolaylık için)
    public SystemSetting(String paramKey, Double paramValue) {
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    // --- GETTER VE SETTERLAR ---

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public Double getParamValue() {
        return paramValue;
    }

    public void setParamValue(Double paramValue) {
        this.paramValue = paramValue;
    }
}