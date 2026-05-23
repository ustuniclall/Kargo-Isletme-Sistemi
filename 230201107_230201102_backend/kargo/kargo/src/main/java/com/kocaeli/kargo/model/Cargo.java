package com.kocaeli.kargo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cargo")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @Column(name = "weight_per_unit", nullable = false)
    private double weightPerUnit;

    @Column(name = "total_weight", nullable = false)
    private double totalWeight;

    @Column(nullable = false)
    private boolean assigned = false;
    
    @Column(length = 1000) // Rota uzun olabileceği için sınırı artırın
    private String routePath;

    @Column(columnDefinition = "TEXT") // Koordinatlar uzun bir liste olacağı için TEXT kullanın
    private String routeCoordinates;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // 📍 Dökümana göre burası kargonun alındığı İLÇE istasyonudur [cite: 8, 23]
    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Station destination;

    @ManyToOne(optional = true)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    private LocalDate shipmentDate;
    
    @Column(name = "trip_distance")
    private Double tripDistance; // 📍 Sefer mesafesini saklamak için

    public Cargo() {}

    // ✅ Toplam ağırlık odaklı Constructor
    public Cargo(int quantity, double totalWeight, User user, Station destination) {
        this.quantity = quantity;
        this.totalWeight = totalWeight; 
        this.weightPerUnit = (quantity > 0) ? totalWeight / quantity : 0; 
        this.user = user;
        this.destination = destination;
        this.assigned = false;
    }

    // ========= GETTERLAR (Değişmedi) =========
    public Long getId() { return id; }
    public int getQuantity() { return quantity; }
    public double getWeightPerUnit() { return weightPerUnit; }
    public double getTotalWeight() { return totalWeight; }
    public boolean isAssigned() { return assigned; }
    public User getUser() { return user; }
    public Station getDestination() { return destination; }
    public Vehicle getVehicle() { return vehicle; }
    public Double getTripDistance() { return tripDistance; }


    // ========= SETTERLAR (Kritik Düzeltmeler) =========

   public void setQuantity(int quantity) {
    this.quantity = quantity;
    // Birim ağırlık sabit kalmalı, toplam ağırlık adede göre güncellenmeli
    this.totalWeight = this.quantity * this.weightPerUnit; 
}

public void setWeight(double totalWeight) {
    this.totalWeight = totalWeight;
    if (this.quantity > 0) {
        this.weightPerUnit = totalWeight / this.quantity;
    }
}

public String getRoutePath() { return routePath; }
public void setRoutePath(String routePath) { this.routePath = routePath; }

public String getRouteCoordinates() { return routeCoordinates; }
public void setRouteCoordinates(String routeCoordinates) { this.routeCoordinates = routeCoordinates; }
    
    public void setAssigned(boolean assigned) { this.assigned = assigned; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public void setUser(User user) { this.user = user; }
    public void setDestination(Station destination) { this.destination = destination; }
    
    public LocalDate getShipmentDate() { return shipmentDate; }
public void setShipmentDate(LocalDate shipmentDate) { this.shipmentDate = shipmentDate; }
public void setTripDistance(Double tripDistance) { this.tripDistance = tripDistance; }
}