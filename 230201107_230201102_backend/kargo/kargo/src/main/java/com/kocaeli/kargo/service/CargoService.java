package com.kocaeli.kargo.service;
import com.kocaeli.kargo.model.OptimizationResult;
import java.time.LocalDate; // 📍 Doğru import

public interface CargoService {
    OptimizationResult assignCargos(LocalDate date); // 📍 Parametre eklendi
}