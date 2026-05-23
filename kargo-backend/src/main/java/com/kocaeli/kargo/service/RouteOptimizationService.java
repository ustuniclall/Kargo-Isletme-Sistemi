package com.kocaeli.kargo.service;

import com.kocaeli.kargo.model.*;
import com.kocaeli.kargo.util.DistanceUtil;
import com.kocaeli.kargo.repository.StationRepository;
import com.kocaeli.kargo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteOptimizationService {
    private static final double UMUTTEPE_LAT = 40.8231;
    private static final double UMUTTEPE_LON = 29.9210;
    //private static final double FUEL_COST_PER_KM = 1.0; 
    //private static final double RENT_COST = 200.0;     

    @Autowired
    private RoadDistanceService roadDistanceService;

    @Autowired
    private StationRepository stationRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private SettingsService settingsService;

    public static class AssignmentPlan {
        public Cargo originalCargo;
        public Vehicle vehicle;
        public int quantity;
        public double totalWeight;
        public List<String> routePath; 
        public List<double[]> routeCoordinates = new ArrayList<>();
        public double vehicleTotalKm;

        public AssignmentPlan(Cargo c, Vehicle v, int q, double w) { 
            this.originalCargo = c; 
            this.vehicle = v; 
            this.quantity = q; 
            this.totalWeight = w; 
            this.routePath = new ArrayList<>();
        }
    }

    public OptimizationResult optimize(List<Cargo> cargos, List<Vehicle> mainVehicles) {
        // 📍 3. ADIM: Metodun en başında güncel değerleri alın
Double fuelCostPerKm = settingsService.getParam("fuel_cost", 1.0);
Double rentalFee = settingsService.getParam("rental_fee", 200.0);

        // 1. Kargoları ilçelerine göre grupla
        Map<Station, List<Cargo>> districtGroups = cargos.stream()
                .collect(Collectors.groupingBy(Cargo::getDestination));

        List<Station> sortedDistricts = new ArrayList<>(districtGroups.keySet());
        // Merkeze en uzak ilçeden başlamak toplama verimliliği için hala en mantıklısıdır
        sortedDistricts.sort((s1, s2) -> Double.compare(distanceToUmuttepe(s2), distanceToUmuttepe(s1)));

        List<AssignmentPlan> plans = new ArrayList<>();
        List<Vehicle> activeVehicles = new ArrayList<>();
        List<Vehicle> availableMainVehicles = new ArrayList<>(mainVehicles);
        availableMainVehicles.sort(Comparator.comparingDouble(Vehicle::getCapacityKg).reversed());
        List<Vehicle> rentedVehicles = new ArrayList<>();

        // 2. Kargoları Araçlara Dağıt
        for (Station district : sortedDistricts) {
            List<Cargo> districtCargos = districtGroups.get(district);

            for (Cargo cargo : districtCargos) {
                int remainingQty = cargo.getQuantity();
                double weightPerUnit = cargo.getWeightPerUnit();

                while (remainingQty > 0) {
                    Vehicle bestVehicle = null;
                    double bestMarginalCost = Double.MAX_VALUE;

                    for (Vehicle v : activeVehicles) {
                        if (v.getRemainingCapacity() >= weightPerUnit) {
                            double cost = calculateMarginalCost(v, plans, district);
                            if (cost < bestMarginalCost) {
                                bestMarginalCost = cost;
                                bestVehicle = v;
                            }
                        }
                    }

                    // Yeni araç çıkarma maliyeti (Sadece ilçeden Umuttepe'ye gidiş hesaplanır)
                    double newVehicleStartCost = distanceToUmuttepe(district) * fuelCostPerKm;

                   if (!availableMainVehicles.isEmpty() && newVehicleStartCost < bestMarginalCost) {
    bestVehicle = availableMainVehicles.remove(0);
    activeVehicles.add(bestVehicle);
    bestMarginalCost = newVehicleStartCost;
}

// 📍 Kiralama kararı (Dinamik kira ücreti ile)
if (bestVehicle == null || bestMarginalCost > rentalFee) {
    if (bestVehicle == null || bestVehicle.getRemainingCapacity() < weightPerUnit) {
        bestVehicle = Vehicle.rentedVehicle("Kiralık-" + (rentedVehicles.size() + 1));
        rentedVehicles.add(bestVehicle);
        activeVehicles.add(bestVehicle);
    }
                    }

                    double currentRemaining = bestVehicle.getRemainingCapacity();
                    int canFitQty = (int) Math.floor(currentRemaining / weightPerUnit);
                    int toLoad = Math.min(remainingQty, canFitQty);

                    if (toLoad > 0) {
                        double loadWeight = toLoad * weightPerUnit;
                        bestVehicle.load(loadWeight);
                        plans.add(new AssignmentPlan(cargo, bestVehicle, toLoad, loadWeight));
                        remainingQty -= toLoad;
                    } else {
                        bestMarginalCost = Double.MAX_VALUE; 
                    }
                }
            }
        }

        return finalizeOptimization(plans, mainVehicles, rentedVehicles, activeVehicles);
    }

    private OptimizationResult finalizeOptimization(List<AssignmentPlan> plans, List<Vehicle> main, List<Vehicle> rented, List<Vehicle> active) {
        double grandTotalKm = 0;
        Map<String, List<String>> vehiclePaths = new HashMap<>();
        Map<String, Double> vehicleDistances = new HashMap<>();

        for (Vehicle v : active) {
            List<Station> stops = getVehicleStops(v, plans);
            if (stops.isEmpty()) continue;

            List<String> detailedPath = new ArrayList<>();
            double vehicleKm = 0;

            // 📍 BAŞLANGIÇ NOKTASI DÜZELTMESİ: Rota Umuttepe'den değil, ilk duraktan başlar
            String current = stops.get(0).getName();
            detailedPath.add(current);

            for (int i = 1; i < stops.size(); i++) {
                Station nextStation = stops.get(i);
                List<String> segment = roadDistanceService.getFullPath(current, nextStation.getName());
                vehicleKm += roadDistanceService.calculatePathDistance(segment);
                for(String city : segment) {
                    if (detailedPath.isEmpty() || !detailedPath.get(detailedPath.size()-1).equals(city)) detailedPath.add(city);
                }
                current = nextStation.getName();
            }
            
            // Son duraktan Umuttepe merkezine gidiş
            List<String> toUmut = roadDistanceService.getFullPath(current, "Umuttepe");
            vehicleKm += roadDistanceService.calculatePathDistance(toUmut);
            for(String city : toUmut) {
                if (!detailedPath.get(detailedPath.size()-1).equals(city)) detailedPath.add(city);
            }

            vehiclePaths.put(v.getName(), detailedPath);
            vehicleDistances.put(v.getName(), vehicleKm);
            grandTotalKm += vehicleKm;
        }
        
         
        for (Vehicle rv : rented) {
        if (rv.getId() == null) {
            vehicleRepository.save(rv); // 📍 vehicleRepository'yi buraya enjekte etmelisin
        }
    }


        for (AssignmentPlan p : plans) {
            p.routePath = vehiclePaths.get(p.vehicle.getName());
            double km = vehicleDistances.getOrDefault(p.vehicle.getName(), 0.0);
    p.vehicleTotalKm = km;
          
            // 📍 ÖNEMLİ: Veritabanına kayıt edilecek kargo nesnesini güncelle
    Cargo dbCargo = p.originalCargo;
    dbCargo.setTripDistance(km); // 📍 0'dan farklı olduğundan emin oluyoruz
    dbCargo.setRoutePath(String.join(" → ", p.routePath));
    dbCargo.setVehicle(p.vehicle);
    dbCargo.setAssigned(true);
    
   
            if (p.routePath != null) {
                for (String cityName : p.routePath) {
                    if (cityName.equals("Umuttepe")) p.routeCoordinates.add(new double[]{UMUTTEPE_LAT, UMUTTEPE_LON});
                    else {
                        Station s = stationRepository.findByName(cityName);
                        if (s != null) p.routeCoordinates.add(new double[]{s.getLatitude(), s.getLongitude()});
                    }
                }
            }
        }

        // 📍 2. ADIM: Dinamik parametreleri çekin
    Double fuelCostPerKm = settingsService.getParam("fuel_cost", 1.0);
    Double rentalFee = settingsService.getParam("rental_fee", 200.0);

    // 📍 Toplam maliyet hesabı: (Toplam KM * Yakıt) + (Kiralık Araç Sayısı * Kira Ücreti)
    double totalCost = (grandTotalKm * fuelCostPerKm) + (rented.size() * rentalFee);
    
    OptimizationResult res = new OptimizationResult(main, rented, totalCost);
    res.setAssignmentPlans(plans); 
    return res;
    }

    private double calculateMarginalCost(Vehicle v, List<AssignmentPlan> plans, Station newDistrict) {
    List<Station> currentStops = getVehicleStops(v, plans);
    double d1 = calculateStopsDistance(currentStops);
    List<Station> newStops = new ArrayList<>(currentStops);
    
    if (!newStops.contains(newDistrict)) {
        newStops.add(newDistrict);
    }
    
    double d2 = calculateStopsDistance(newStops);

    // 📍 Hata veren satırı bu şekilde güncelleyin:
    Double fuelCostRate = settingsService.getParam("fuel_cost", 1.0); 
    return (d2 - d1) * fuelCostRate;
}

    private double calculateStopsDistance(List<Station> stops) {
        if (stops.isEmpty()) return 0;
        double dist = 0;
        // 📍 DÜZELTME: Mesafe hesabı da ilk duraktan başlar
        String current = stops.get(0).getName();
        for (int i = 1; i < stops.size(); i++) {
            dist += roadDistanceService.calculatePathDistance(roadDistanceService.getFullPath(current, stops.get(i).getName()));
            current = stops.get(i).getName();
        }
        dist += roadDistanceService.calculatePathDistance(roadDistanceService.getFullPath(current, "Umuttepe"));
        return dist;
    }

    private List<Station> getVehicleStops(Vehicle v, List<AssignmentPlan> plans) {
        return plans.stream()
            .filter(p -> p.vehicle.getName().equals(v.getName()))
            .map(p -> p.originalCargo.getDestination())
            .distinct()
            .collect(Collectors.toList());
    }

    private double distanceToUmuttepe(Station s) {
        return DistanceUtil.distanceKm(s.getLatitude(), s.getLongitude(), UMUTTEPE_LAT, UMUTTEPE_LON);
    }
}