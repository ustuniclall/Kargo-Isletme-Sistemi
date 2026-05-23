package com.kocaeli.kargo.model;

import com.kocaeli.kargo.service.RouteOptimizationService.AssignmentPlan;
import java.util.ArrayList;
import java.util.List;

/**
 * Rota optimizasyonu sonucunda oluşan tüm verileri (araçlar, maliyet ve plan) 
 * bir arada tutan model sınıfıdır.
 */
public class OptimizationResult {

    private List<Vehicle> mainVehicles;
    private List<Vehicle> rentedVehicles;
    private double totalCost;
    
    // 📍 Yeni eklenen alan: Kargo-Araç eşleşme detaylarını tutar.
    // Bu liste, parçalanmış kargoların dağılımını raporlamak için kullanılır.
    private List<AssignmentPlan> assignmentPlans = new ArrayList<>();

    public OptimizationResult() {}

    public OptimizationResult(List<Vehicle> mainVehicles, List<Vehicle> rentedVehicles, double totalCost) {
        this.mainVehicles = mainVehicles;
        this.rentedVehicles = rentedVehicles;
        this.totalCost = totalCost;
    }

    // ========= GETTER & SETTER METOTLARI =========

    public List<Vehicle> getMainVehicles() {
        return mainVehicles;
    }

    public void setMainVehicles(List<Vehicle> mainVehicles) {
        this.mainVehicles = mainVehicles;
    }

    public List<Vehicle> getRentedVehicles() {
        return rentedVehicles;
    }

    public void setRentedVehicles(List<Vehicle> rentedVehicles) {
        this.rentedVehicles = rentedVehicles;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    // 📍 Yeni eklenen Getter & Setter
    public List<AssignmentPlan> getAssignmentPlans() {
        return assignmentPlans;
    }

    public void setAssignmentPlans(List<AssignmentPlan> assignmentPlans) {
        this.assignmentPlans = assignmentPlans;
    }
}