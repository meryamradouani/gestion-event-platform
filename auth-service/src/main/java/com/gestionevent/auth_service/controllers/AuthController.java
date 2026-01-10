package com.gestionevent.auth_service.controllers;

import com.gestionevent.auth_service.entities.User;
import com.gestionevent.auth_service.dto.RegisterRequest;
import com.gestionevent.auth_service.dto.LoginRequest; // 1. Ajout de l'import LoginRequest
import com.gestionevent.auth_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // 2. Ajout de l'import HttpStatus
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

    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_ORGANIZER = "ORGANIZER";

    // --- Inscription Étudiant ---
    @PostMapping("/register/student")
    public ResponseEntity<String> registerStudent(@RequestBody RegisterRequest request) {
        return saveUser(request, ROLE_STUDENT); // On laisse saveUser gérer la réponse
    }

    // --- Inscription Organisateur ---
    @PostMapping("/register/organizer")
    public ResponseEntity<String> registerOrganizer(@RequestBody RegisterRequest request) {
        return saveUser(request, ROLE_ORGANIZER);
    }

    // --- Connexion ---
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    // Vérification du mot de passe haché
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return ResponseEntity.ok(
                                "Connexion réussie ! Bienvenue " + user.getFirstName() + " (" + user.getRole() + ")");
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé"));
    }

    // Méthode utilitaire pour traiter l'inscription
    private ResponseEntity<String> saveUser(RegisterRequest request, String role) {
        // Vérification unique de l'email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Erreur : Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hachage
        user.setRole(role);

        // Attribution des champs spécifiques
        if ("STUDENT".equals(role)) {
            user.setCne(request.getCne());
            user.setFiliere(request.getFiliere());
            user.setNiveau(request.getNiveau());
        } else if ("ORGANIZER".equals(role)) {
            user.setNomEtablissement(request.getNomEtablissement());
            user.setTypeOrganisateur(request.getTypeOrganisateur());
        }

        userRepository.save(user);
        String message = (ROLE_STUDENT.equals(role) ? "Étudiant" : "Organisateur") + " inscrit avec succès !";
        return ResponseEntity.ok(message);
    }
}