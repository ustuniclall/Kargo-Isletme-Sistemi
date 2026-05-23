package com.kocaeli.kargo.config;

import com.kocaeli.kargo.model.Station;
import com.kocaeli.kargo.model.Vehicle;
import com.kocaeli.kargo.repository.StationRepository;
import com.kocaeli.kargo.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    // =========================
    // 📍 Kocaeli Durakları
    // =========================
    @Bean
    CommandLineRunner loadStations(StationRepository stationRepository) {
        return args -> {

            if (stationRepository.count() == 0) {

                stationRepository.save(new Station(null, "Izmit", 40.7669, 29.9169));
                stationRepository.save(new Station(null, "Gebze", 40.8025, 29.4307));
                stationRepository.save(new Station(null, "Darıca", 40.7569, 29.3866));
                stationRepository.save(new Station(null, "Çayırova", 40.8247, 29.3734));
                stationRepository.save(new Station(null, "Dilovası", 40.7796, 29.5440));
                stationRepository.save(new Station(null, "Körfez", 40.7760, 29.7354));
                stationRepository.save(new Station(null, "Derince", 40.7565, 29.8294));
                stationRepository.save(new Station(null, "Başiskele", 40.7177, 29.9375));
                stationRepository.save(new Station(null, "Kartepe", 40.7422, 30.0282));
                stationRepository.save(new Station(null, "Gölcük", 40.7167, 29.8181));
                stationRepository.save(new Station(null, "Karamürsel", 40.6911, 29.6164));
                stationRepository.save(new Station(null, "Kandıra", 41.0706, 30.1526));

                System.out.println("📍 Kocaeli durakları veritabanına eklendi");
            }
        };
    }

    // =========================
    // 🚚 ANA ARAÇLAR
    // =========================
    @Bean
    CommandLineRunner loadVehicles(VehicleRepository vehicleRepository) {
        return args -> {

            if (vehicleRepository.count() == 0) {

                // Tam sayıları (int) ondalıklı sayıya (double/Double) çeviriyoruz
vehicleRepository.save(new Vehicle("Küçük Araç", 500.0));
vehicleRepository.save(new Vehicle("Orta Araç", 750.0));
vehicleRepository.save(new Vehicle("Büyük Araç", 1000.0));

                System.out.println("🚚 Varsayılan ana araçlar veritabanına eklendi");
            }
        };
    }
}
