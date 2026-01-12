package com.gestionevent.backend.controller;

import com.gestionevent.backend.dto.request.SaveTokenRequest;
import com.gestionevent.backend.dto.response.ApiResponse;
import com.gestionevent.backend.service.TokenManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour gérer les tokens FCM des utilisateurs.
 * Permet de sauvegarder, mettre à jour ou supprimer un token.
 */
@RestController
@RequestMapping("/api/notifications/tokens") // Route principale du contrôleur
public class TokenController {

    @Autowired
    private TokenManagementService tokenService; // Injection du service qui gère les tokens

    /**
     * Endpoint pour enregistrer ou mettre à jour un token FCM.
     * 
     * @param request DTO contenant userId, fcmToken, deviceType et deviceInfo
     * @return ResponseEntity contenant un ApiResponse avec message de succès
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveToken(@Valid @RequestBody SaveTokenRequest request) {
        // Appelle le service pour créer ou mettre à jour le token
        tokenService.saveOrUpdateToken(request);

        // Crée la réponse standard de succès
        ApiResponse<Void> response = ApiResponse.success("Token enregistré avec succès");

        // Retourne un HTTP 200 OK avec la réponse
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour supprimer un token FCM spécifique d'un utilisateur.
     *
     * @param userId   L'ID de l'utilisateur
     * @param fcmToken Le token FCM à supprimer
     * @return ResponseEntity contenant un ApiResponse avec message de succès
     */
    @DeleteMapping("/{userId}/{fcmToken}")
    public ResponseEntity<ApiResponse<Void>> deleteToken(
            @PathVariable Integer userId,
            @PathVariable String fcmToken) {

        // Appelle le service pour supprimer le token
        tokenService.deleteToken(userId, fcmToken);

        // Crée la réponse standard de succès
        ApiResponse<Void> response = ApiResponse.success("Token supprimé avec succès");

        // Retourne un HTTP 200 OK avec la réponse
        return ResponseEntity.ok(response);
    }
}
