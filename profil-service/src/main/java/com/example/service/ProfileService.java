package com.example.service;

import com.example.entity.Profile;
import com.example.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final com.example.repository.ProfileImageRepository profileImageRepository;

    private static final String HISTORY_INSCRITS = "inscrits";
    private static final String HISTORY_CREES = "créés";

    public ProfileService(ProfileRepository profileRepository,
            com.example.repository.ProfileImageRepository profileImageRepository) {
        this.profileRepository = profileRepository;
        this.profileImageRepository = profileImageRepository;
    }

    // Upload profile image
    public void uploadProfileImage(Long userId, org.springframework.web.multipart.MultipartFile file)
            throws java.io.IOException {
        Optional<Profile> profileOpt = profileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            Profile profile = profileOpt.get();
            Optional<com.example.entity.ProfileImage> existingImage = profileImageRepository
                    .findByProfileId(profile.getId());

            com.example.entity.ProfileImage image;
            if (existingImage.isPresent()) {
                image = existingImage.get();
                image.setImageData(file.getBytes());
                image.setContentType(file.getContentType());
            } else {
                image = new com.example.entity.ProfileImage(profile.getId(), file.getBytes(), file.getContentType());
            }
            profileImageRepository.save(image);

            // Met à jour l'URL (optionnel si on veut garder une trace dans text, mais on va
            // le générer dynamiquement plutôt)
            // profile.setProfilePicUrl("http://localhost:8085/profiles/" + profile.getId()
            // + "/image");
            // profileRepository.save(profile);
        }
    }

    public Optional<com.example.entity.ProfileImage> getProfileImage(Long profileId) {
        return profileImageRepository.findByProfileId(profileId);
    }

    // Récupérer un profil par userId
    public Optional<Profile> getProfileByUserId(Long userId) {
        Optional<Profile> profile = profileRepository.findByUserId(userId);
        profile.ifPresent(p -> {
            p.setProfilePicUrl("http://localhost:8085/profiles/" + p.getId() + "/image");
        });
        return profile;
    }

    // Créer un nouveau profil
    public Profile createProfile(Long userId, String fullName, String email) {
        Profile profile = new Profile(userId, fullName);
        profile.setLastLogin(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    // Mettre à jour un profil
    public Profile updateProfile(Long userId, Profile updatedProfile) {
        Optional<Profile> existingProfile = profileRepository.findByUserId(userId);

        if (existingProfile.isPresent()) {
            Profile profile = existingProfile.get();

            // Mettre à jour seulement les champs autorisés
            if (updatedProfile.getFullName() != null) {
                profile.setFullName(updatedProfile.getFullName());
            }
            if (updatedProfile.getBio() != null) {
                profile.setBio(updatedProfile.getBio());
            }
            if (updatedProfile.getProfilePicUrl() != null) {
                profile.setProfilePicUrl(updatedProfile.getProfilePicUrl());
            }

            profile.setLastLogin(LocalDateTime.now());
            return profileRepository.save(profile);
        }

        return null; // Ou lever une exception
    }

    // Créer ou mettre à jour le profil après connexion (Kafka)
    public void createOrUpdateProfileAfterLogin(Long userId, String email, String fullName) {
        Optional<Profile> existingProfile = profileRepository.findByUserId(userId);

        if (existingProfile.isPresent()) {
            // Profil existe : on met à jour la date de connexion
            Profile profile = existingProfile.get();
            profile.setLastLogin(LocalDateTime.now());

            // Si le nom change, on peut le mettre à jour ici (Optionnel, mais logique si on
            // veut que le profil reflète le dernier login)
            if (fullName != null && !fullName.isEmpty()) {
                profile.setFullName(fullName);
            }

            profileRepository.save(profile);
            System.out.println("✅ [Service] Updated last_login for existing user: " + userId);
        } else {
            // Profil n'existe pas : on le crée avec le fullName, ou email, ou User ID par
            // défaut
            String nameToUse = (fullName != null && !fullName.isEmpty()) ? fullName
                    : (email != null ? email : "User " + userId);
            Profile newProfile = new Profile(userId, nameToUse);
            newProfile.setLastLogin(LocalDateTime.now());
            profileRepository.save(newProfile);
            System.out.println("✅ [Service] Created new profile for user: " + userId);
        }
    }

    // Mettre à jour le dernier login - VERSION UNIQUE
    public void updateLastLogin(Long userId) {
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setLastLogin(LocalDateTime.now());
            profileRepository.save(profile);
            System.out.println("✅ Last login updated for user: " + userId);
        });
    }

    // Ajouter un événement à l'historique
    public void addEventToHistory(Long userId, Long eventId, String eventType) {
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.node.ObjectNode root = parseHistory(profile.getEventsHistory(), mapper);

                updateHistoryArray(root, eventType, eventId);

                profile.setEventsHistory(mapper.writeValueAsString(root));
                profileRepository.save(profile);
                System.out.println("✅ Event " + eventId + " added to history for user: " + userId);
            } catch (Exception e) {
                System.err.println("❌ Error updating history: " + e.getMessage());
            }
        });
    }

    private com.fasterxml.jackson.databind.node.ObjectNode parseHistory(String history,
            com.fasterxml.jackson.databind.ObjectMapper mapper) {
        if (history == null || history.trim().isEmpty()) {
            return createEmptyHistory(mapper);
        }
        try {
            com.fasterxml.jackson.databind.node.ObjectNode root = (com.fasterxml.jackson.databind.node.ObjectNode) mapper
                    .readTree(history);
            ensureHistoryArrays(root);
            return root;
        } catch (Exception e) {
            return createEmptyHistory(mapper);
        }
    }

    private com.fasterxml.jackson.databind.node.ObjectNode createEmptyHistory(
            com.fasterxml.jackson.databind.ObjectMapper mapper) {
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
        root.putArray(HISTORY_INSCRITS);
        root.putArray(HISTORY_CREES);
        return root;
    }

    private void ensureHistoryArrays(com.fasterxml.jackson.databind.node.ObjectNode root) {
        if (!root.has(HISTORY_INSCRITS))
            root.putArray(HISTORY_INSCRITS);
        if (!root.has(HISTORY_CREES))
            root.putArray(HISTORY_CREES);
    }

    private void updateHistoryArray(com.fasterxml.jackson.databind.node.ObjectNode root, String eventType,
            Long eventId) {
        com.fasterxml.jackson.databind.node.ArrayNode targetArray = "inscrit".equals(eventType)
                ? (com.fasterxml.jackson.databind.node.ArrayNode) root.get(HISTORY_INSCRITS)
                : (com.fasterxml.jackson.databind.node.ArrayNode) root.get(HISTORY_CREES);

        if (!containsId(targetArray, eventId)) {
            targetArray.add(eventId);
        }
    }

    private boolean containsId(com.fasterxml.jackson.databind.node.ArrayNode array, Long id) {
        for (com.fasterxml.jackson.databind.JsonNode node : array) {
            if (node.asLong() == id) {
                return true;
            }
        }
        return false;
    }
}