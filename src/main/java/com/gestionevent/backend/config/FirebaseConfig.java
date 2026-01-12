package com.gestionevent.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    @PostConstruct
    public void initialize() {
        try {
            // Chemin vers le fichier de configuration
            InputStream serviceAccount = getClass()
                .getClassLoader()
                .getResourceAsStream("ecole-events-notifications-firebase-adminsdk-fbsvc-ae04441b78.json");
            
            if (serviceAccount == null) {
                throw new RuntimeException("Fichier .json non trouvé dans resources");
            }
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialisé avec succès");
            } else {
                System.out.println("⚠️ Firebase déjà initialisé");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur d'initialisation Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}