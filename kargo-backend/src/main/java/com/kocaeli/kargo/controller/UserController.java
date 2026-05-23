package com.kocaeli.kargo.controller;

import com.kocaeli.kargo.model.User;
import com.kocaeli.kargo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // 👤 GİRİŞ / ID AL
    // =========================
    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {

        // Ad–soyad zorunlu
        if (request.firstName() == null || request.lastName() == null) {
            throw new RuntimeException("Ad ve soyad zorunludur");
        }

        // Aynı ad–soyad varsa tekrar oluşturma
        return userRepository
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                        request.firstName(),
                        request.lastName()
                )
                .orElseGet(() -> {
                    User user = new User(
                            request.firstName(),
                            request.lastName()
                    );
                    return userRepository.save(user);
                });
    }

    // =========================
    // 🔍 FRONTEND İÇİN DOĞRULAMA
    // =========================
    @GetMapping("/verify")
    public boolean verifyUser(
            @RequestParam Long userId,
            @RequestParam String firstName,
            @RequestParam String lastName
    ) {
        return userRepository.findById(userId)
                .map(u ->
                        u.getFirstName().equalsIgnoreCase(firstName)
                        && u.getLastName().equalsIgnoreCase(lastName)
                )
                .orElse(false);
    }
}
