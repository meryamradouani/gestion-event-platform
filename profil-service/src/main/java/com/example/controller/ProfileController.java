package com.example.controller;

import com.example.entity.Profile;
import com.example.entity.ProfileImage;
import com.example.service.KafkaTest;
import com.example.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final KafkaTest kafkaTest;

    public ProfileController(ProfileService profileService, KafkaTest kafkaTest) {
        this.profileService = profileService;
        this.kafkaTest = kafkaTest;
    }

    // GET /profiles/{id} -> Utilise l'ID interne de la table profiles
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        System.out.println("🚀 [DEBUG] Received request for Profile ID: " + id);
        Optional<Profile> profile = profileService.getProfileById(id);

        if (profile.isPresent()) {
            return ResponseEntity.ok(profile.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "DEBUG: Profile not found for internal ID: " + id);
            return ResponseEntity.status(404).body(error);
        }
    }

    // PUT /profiles/{id} -> Mise à jour via l'ID interne
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Profile updatedProfile) {
        Profile result = profileService.updateProfile(id, updatedProfile);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cannot update profile. Profile ID not found: " + id);
            return ResponseEntity.status(404).body(error);
        }
    }

    // POST /profiles/{id}/image -> Upload d'image via ID interne
    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            profileService.uploadProfileImage(id, file);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }

    // GET /profiles/image/{profileId} -> Récupération de l'image
    @GetMapping("/image/{profileId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long profileId) {
        Optional<ProfileImage> imageOpt = profileService.getProfileImage(profileId);

        if (imageOpt.isPresent()) {
            ProfileImage image = imageOpt.get();
            String contentType = image.getFileType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "image/png"; // Fallback par défaut
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(image.getImageData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Endpoint de test Kafka
    @PostMapping("/test-kafka")
    public ResponseEntity<String> runKafkaTest() {
        kafkaTest.sendAllTestEvents();
        return ResponseEntity.ok("Test events triggered! Check console logs.");
    }
}
