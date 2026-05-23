package com.kocaeli.kargo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "capacity_kg")
    private Double capacityKg;

    @Column(name = "used_capacity_kg")
    private Double usedCapacityKg = 0.0;

    private boolean available = true;

    private boolean rented = false;

    @Column(name = "daily_cost")
    private Double dailyCost = 0.0;

    // --- CONSTRUCTORS ---
    public Vehicle() {}

    public Vehicle(String name, Double capacityKg) {
        this.name = name;
        this.capacityKg = capacityKg;
        this.usedCapacityKg = 0.0;
        this.available = true;
    }

    // 📍 RouteOptimizationService'in kiralık araç oluştururken kullandığı yardımcı metot
    public static Vehicle rentedVehicle(String name) {
        Vehicle v = new Vehicle();
        v.setName(name);
        v.setCapacityKg(500.0); // Varsayılan kiralık kapasite
        v.setRented(true);
        v.setAvailable(true);
        v.setUsedCapacityKg(0.0);
        return v;
    }

    // --- ÖZEL MANTIK METOTLARI (RouteOptimizationService için gerekli) ---

    // Kalan kapasiteyi hesaplar
    public double getRemainingCapacity() {
        return (capacityKg != null ? capacityKg : 0.0) - (usedCapacityKg != null ? usedCapacityKg : 0.0);
    }

    // Araca kargo yükler
    public void load(double weight) {
        if (this.usedCapacityKg == null) this.usedCapacityKg = 0.0;
        this.usedCapacityKg += weight;
    }

    // --- GETTER VE SETTERLAR (Controller ve Servisler için) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getCapacityKg() { return capacityKg; }
    public void setCapacityKg(Double capacityKg) { this.capacityKg = capacityKg; }

    public Double getUsedCapacityKg() { return usedCapacityKg; }
    public void setUsedCapacityKg(Double usedCapacityKg) { this.usedCapacityKg = usedCapacityKg; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isRented() { return rented; }
    public void setRented(boolean rented) { this.rented = rented; }

    public Double getDailyCost() { return dailyCost; }
    public void setDailyCost(Double dailyCost) { this.dailyCost = dailyCost; }
}