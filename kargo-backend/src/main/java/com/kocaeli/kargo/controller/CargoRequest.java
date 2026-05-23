package com.kocaeli.kargo.controller;

import java.time.LocalDate;

public record CargoRequest(
    Long userId,
    String firstName,
    String lastName,
    Long stationId,
    int quantity,
    double totalWeight,
    LocalDate shipmentDate
) {}