package com.gestionevent.backend.service;

import com.gestionevent.backend.dto.request.SaveTokenRequest;
import com.gestionevent.backend.model.NotificationToken;
import com.gestionevent.backend.repository.NotificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TokenManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(TokenManagementService.class);
    
    @Autowired
    private NotificationTokenRepository tokenRepository;
    
    @Transactional
    public void saveOrUpdateToken(SaveTokenRequest request) {
        Optional<NotificationToken> existingToken = tokenRepository
                .findByUserIdAndFcmToken(request.getUserId(), request.getFcmToken());
        
        if (existingToken.isPresent()) {
            NotificationToken token = existingToken.get();
            token.setDeviceType(request.getDeviceType());
            token.setDeviceInfo(request.getDeviceInfo());
            tokenRepository.save(token);
            log.info("Token mis à jour pour user {}", request.getUserId());
        } else {
            NotificationToken newToken = new NotificationToken(
                    request.getUserId(),
                    request.getFcmToken(),
                    request.getDeviceType()
            );
            newToken.setDeviceInfo(request.getDeviceInfo());
            tokenRepository.save(newToken);
            log.info("Nouveau token enregistré pour user {}", request.getUserId());
        }
    }
    
    @Transactional
    public void deleteToken(Integer userId, String fcmToken) {
        tokenRepository.deleteByUserIdAndFcmToken(userId, fcmToken);
        log.info("Token supprimé pour user {}", userId);
    }
    
    public List<String> getTokensForUser(Integer userId) {
        List<NotificationToken> tokens = tokenRepository.findByUserId(userId);
        return tokens.stream()
                .map(NotificationToken::getFcmToken)
                .toList();
    }
    
    public List<String> getTokensForUsers(List<Integer> userIds) {
        List<NotificationToken> tokens = tokenRepository.findByUserIdIn(userIds);
        return tokens.stream()
                .map(NotificationToken::getFcmToken)
                .toList();
    }
    
    /**
     * Récupère tous les userIds qui ont au moins un token FCM enregistré
     */
    public List<Integer> getAllUserIdsWithTokens() {
        List<NotificationToken> allTokens = tokenRepository.findAll();
        return allTokens.stream()
                .map(NotificationToken::getUserId)
                .distinct()
                .toList();
    }
    
    /**
     * Sauvegarde ou met à jour un token depuis un événement Kafka
     */
    @Transactional
    public void saveOrUpdateTokenFromEvent(Integer userId, String fcmToken, 
                                          String deviceType, String deviceInfo) {
        Optional<NotificationToken> existingToken = tokenRepository
                .findByUserIdAndFcmToken(userId, fcmToken);
        
        if (existingToken.isPresent()) {
            NotificationToken token = existingToken.get();
            token.setDeviceType(deviceType);
            token.setDeviceInfo(deviceInfo);
            tokenRepository.save(token);
            log.info("Token mis à jour depuis Kafka pour user {}", userId);
        } else {
            NotificationToken newToken = new NotificationToken(
                    userId,
                    fcmToken,
                    deviceType
            );
            newToken.setDeviceInfo(deviceInfo);
            tokenRepository.save(newToken);
            log.info("Nouveau token enregistré depuis Kafka pour user {}", userId);
        }
    }
}