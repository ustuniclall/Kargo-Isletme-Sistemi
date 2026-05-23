package com.kocaeli.kargo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "road") // Veritabanındaki tablo adı
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;      // Başlangıç Durağı (Örn: İzmit)
    private String destination; // Varış Durağı (Örn: Derince)
    private Double distance;    // Mesafe (km)

    // 1. Boş Constructor (JPA/Hibernate için Zorunlu)
    public Road() {
    }

    // 2. Parametreli Constructor (Kod içinde yeni yol oluştururken kolaylık sağlar)
    public Road(String source, String destination, Double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    // --- Getter ve Setter Metotları ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}