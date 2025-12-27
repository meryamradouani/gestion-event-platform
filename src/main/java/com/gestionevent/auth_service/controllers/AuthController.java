package com.gestionevent.auth_service.controllers;

import com.gestionevent.auth_service.dto.LoginRequest;
import com.gestionevent.auth_service.dto.RegisterRequest;
import com.gestionevent.auth_service.dto.UserAuthenticatedMessage; // Pour Salma
import com.gestionevent.auth_service.dto.UserTokenMessage; // Pour ton autre amie
import com.gestionevent.auth_service.entities.User;
import com.gestionevent.auth_service.repositories.UserRepository;
import com.gestionevent.auth_service.services.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    // --- Inscription Étudiant ---
    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterRequest request) {
        return saveUser(request, "STUDENT");
    }

    // --- Inscription Organisateur ---
    @PostMapping("/register/organizer")
    public ResponseEntity<?> registerOrganizer(@RequestBody RegisterRequest request) {
        return saveUser(request, "ORGANIZER");
    }

    // --- Connexion ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    // Vérification du mot de passe crypté
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                        // 1. --- ENVOI KAFKA POUR AMIE 1 (Notification/Tokens) ---
                        // Topic: user.tokens.updated
                        UserTokenMessage tokenMessage = new UserTokenMessage(
                                user.getId(),
                                request.getFcmToken() != null ? request.getFcmToken() : "token_fcm_test",
                                request.getDeviceType() != null ? request.getDeviceType() : "ANDROID",
                                request.getDeviceInfo() != null ? request.getDeviceInfo() : "Mobile Device"
                        );
                        kafkaProducerService.sendTokenUpdate(tokenMessage);

                        // 2. --- ENVOI KAFKA POUR AMIE 2 / SALMA (Service Profil) ---
                        // Topic: user.authenticated
                        UserAuthenticatedMessage profileMessage = new UserAuthenticatedMessage(
                                user.getId(),
                                user.getEmail(),
                                user.getFullName(),
                                user.getRole()
                        );
                        kafkaProducerService.sendUserAuthenticated(profileMessage);

                        return ResponseEntity.ok("Connexion réussie ! Bienvenue " + user.getFullName());
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé"));
    }

    // Méthode utilitaire pour traiter l'inscription
    private ResponseEntity<?> saveUser(RegisterRequest request, String role) {
        // 1. Vérification email unique selon contrainte SQL
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Erreur : Cet email est déjà utilisé.");
        }

        // 2. Création de l'entité User (mappage full_name et role)
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        // 3. Champs spécifiques (extraits de la logique métier)
        if ("STUDENT".equals(role)) {
            user.setCne(request.getCne());
            user.setFiliere(request.getFiliere());
            user.setNiveau(request.getNiveau());
        } else if ("ORGANIZER".equals(role)) {
            user.setNomEtablissement(request.getNomEtablissement());
            user.setTypeOrganisateur(request.getTypeOrganisateur());
        }

        // 4. Sauvegarde Base de Données
        userRepository.save(user);

        // 5. Notification Kafka simple (Texte) pour ton propre suivi
        String kafkaMsg = "NOUVEL_UTILISATEUR|" + role + "|" + user.getEmail() + "|" + user.getFullName();
        try {
            kafkaProducerService.sendMessage(kafkaMsg);
        } catch (Exception e) {
            System.err.println("Erreur notification Kafka inscription : " + e.getMessage());
        }

        String message = ("STUDENT".equals(role) ? "Étudiant" : "Organisateur") + " inscrit avec succès !";
        return ResponseEntity.ok(message);
    }
}