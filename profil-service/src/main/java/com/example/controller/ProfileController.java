package com.example.controller;

import com.example.entity.Profile;
import com.example.service.KafkaTest;
import com.example.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // GET /profiles/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        Optional<Profile> profile = profileService.getProfileByUserId(userId);

        if (profile.isPresent()) {
            return ResponseEntity.ok(profile.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Profile not found for user ID: " + userId);
            return ResponseEntity.status(404).body(error);
        }
    }

    // PUT /profiles/{userId}
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody Profile updatedProfile) {
        Profile result = profileService.updateProfile(userId, updatedProfile);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cannot update profile. User ID not found: " + userId);
            return ResponseEntity.status(404).body(error);
        }
    }

    // Endpoint de test
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Profile-Service is running!");
    }

    // Ajoute cette méthode
    @Autowired
    private KafkaTest kafkaTest;

    @PostMapping("/test-kafka")
    public String testKafka() {
        kafkaTest.sendAllTestEvents();
        return "Test Kafka events sent! Check console logs.";
    }

    // --- Gestion des Images (BLOB) ---

    // Upload Image: POST /profiles/{userId}/image
    @PostMapping("/{userId}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long userId, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            profileService.uploadProfileImage(userId, file);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (java.io.IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }

    // Get Image: GET /profiles/{userId}/image
    @GetMapping("/{userId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long userId) {
        // Attention: ici on suppose que userId = profileId pour simplifier,
        // sinon il faut récupérer le profil d'abord.
        // Dans getProfileByUserId, on a supposé que l'ID du profil était accessible.
        // On va d'abord retrouver le profil ID via le UserID.

        java.util.Optional<com.example.entity.Profile> profile = profileService.getProfileByUserId(userId);
        if (profile.isPresent()) {
            java.util.Optional<com.example.entity.ProfileImage> imageOpt = profileService.getProfileImage(profile.get().getId());
            if (imageOpt.isPresent()) {
                com.example.entity.ProfileImage image = imageOpt.get();
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(image.getContentType()))
                        .body(image.getImageData());
            }
        }
        return ResponseEntity.notFound().build();
    }
}
