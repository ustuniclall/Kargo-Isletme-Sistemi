package com.kocaeli.kargo.service;

import com.kocaeli.kargo.model.Road;
import com.kocaeli.kargo.repository.RoadRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoadDistanceService {

    @Autowired
    private RoadRepository roadRepository; // 📍 Veritabanı bağlantısı

    private final Map<String, Map<String, Double>> adjacencyList = new HashMap<>();

    // 📍 Uygulama her başladığında veritabanındaki yolları belleğe yükler
    @PostConstruct
    public void init() {
        refreshGraph();
    }

    public void refreshGraph() {
        adjacencyList.clear();
        
        // 1. Önce varsayılan (sabit) yolları belleğe ekle (DB boşsa diye)
        // 📍 DİKKAT: İsimleri DB ile aynı (İzmit, Başiskele vb.) yapın
        loadDefaults();

        // 2. Veritabanında kayıtlı olan dinamik yolları belleğe ekle
        List<Road> savedRoads = roadRepository.findAll();
        for (Road r : savedRoads) {
            addDistanceInMemory(r.getSource(), r.getDestination(), r.getDistance());
        }
    }

    private void loadDefaults() {
        addDistanceInMemory("Izmit", "Derince", 12.0);
        addDistanceInMemory("Izmit", "Kartepe", 15.0);
        addDistanceInMemory("Izmit", "Başiskele", 10.0);
        addDistanceInMemory("Izmit", "Umuttepe", 8.0);
        addDistanceInMemory("Izmit", "Kandıra", 45.0);

         // BATI (GEBZE) HATTI
        addDistanceInMemory("Derince", "Körfez", 8.0);
        addDistanceInMemory("Körfez", "Dilovası", 22.0);
        addDistanceInMemory("Dilovası", "Gebze", 12.0);
        addDistanceInMemory("Gebze", "Çayırova", 4.0);
        addDistanceInMemory("Gebze", "Darıca", 5.0);
        
         // GÜNEY (GÖLCÜK) HATTI
        addDistanceInMemory("Başiskele", "Gölcük", 8.0);
        addDistanceInMemory("Gölcük", "Karamürsel", 18.0);
    }

    // 📍 Yeni eklenen durağı bağlamak için kullanılan metot
    public void addDynamicRoad(String source, String target, double distance) {
        // Belleğe ekle
        addDistanceInMemory(source, target, distance);
        // Veritabanına kaydet (Böylece restart sonrası kaybolmaz)
        Road road = new Road();
        road.setSource(source);
        road.setDestination(target);
        road.setDistance(distance);
        roadRepository.save(road);
    }

    private void addDistanceInMemory(String source, String target, double distance) {
        adjacencyList.computeIfAbsent(source, k -> new HashMap<>()).put(target, distance);
        adjacencyList.computeIfAbsent(target, k -> new HashMap<>()).put(source, distance);
    }

    // getFullPath ve calculatePathDistance metotları senin kodundakiyle aynı kalacak...
    public List<String> getFullPath(String start, String end) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        distances.put(start, 0.0);
        pq.add(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current.name)) continue;
            visited.add(current.name);
            if (current.name.equals(end)) break;

            for (Map.Entry<String, Double> neighbor : adjacencyList.getOrDefault(current.name, new HashMap<>()).entrySet()) {
                double newDist = distances.get(current.name) + neighbor.getValue();
                if (newDist < distances.getOrDefault(neighbor.getKey(), Double.MAX_VALUE)) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current.name);
                    pq.add(new Node(neighbor.getKey(), newDist));
                }
            }
        }

        LinkedList<String> path = new LinkedList<>();
        String step = end;
        if (previous.get(step) == null && !step.equals(start)) return Collections.singletonList(start);
        while (step != null) {
            path.addFirst(step);
            step = previous.get(step);
        }
        return path;
    }

    public double calculatePathDistance(List<String> path) {
        if (path == null || path.size() < 2) return 0.0;
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            if (adjacencyList.containsKey(current) && adjacencyList.get(current).containsKey(next)) {
                total += adjacencyList.get(current).get(next);
            }
        }
        return total;
    }

    private static class Node {
        String name;
        double distance;
        Node(String name, double distance) { this.name = name; this.distance = distance; }
    }
    
    /* RoadDistanceService.java içine ekleyin */

// Haversine Formülü ile KM hesaplama
public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // Dünya'nın yarıçapı (km)
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // KM cinsinden mesafe
}
}