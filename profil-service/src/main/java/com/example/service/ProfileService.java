package com.example.service;

import com.example.entity.Profile;
import com.example.entity.ProfileImage;
import com.example.repository.ProfileImageRepository;
import com.example.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileImageRepository profileImageRepository;

    public ProfileService(ProfileRepository profileRepository, ProfileImageRepository profileImageRepository) {
        this.profileRepository = profileRepository;
        this.profileImageRepository = profileImageRepository;
    }

    // Upload profile image
    public void uploadProfileImage(Long profileId, MultipartFile file) throws IOException {
        Optional<Profile> profileOpt = profileRepository.findById(profileId);
        if (profileOpt.isPresent()) {
            Profile profile = profileOpt.get();

            Optional<ProfileImage> existingImage = profileImageRepository.findByProfileId(profileId);
            ProfileImage profileImage = existingImage.orElse(new ProfileImage());

            profileImage.setProfileId(profileId);
            profileImage.setImageData(file.getBytes());
            profileImage.setFileName(file.getOriginalFilename());
            profileImage.setFileType(file.getContentType());

            profileImageRepository.save(profileImage);
        }
    }

    // Récupérer les données de l'image
    public Optional<ProfileImage> getProfileImage(Long profileId) {
        return profileImageRepository.findByProfileId(profileId);
    }

    // Récupérer un profil par son ID interne (profileId)
    public Optional<Profile> getProfileById(Long profileId) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        profile.ifPresent(p -> {
            p.setProfilePicUrl("http://localhost:8085/profiles/image/" + p.getId());
        });
        return profile;
    }

    // Récupérer un profil par userId (Création automatique si absent - Très Robuste)
    public Profile getOrCreateProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(this::enrichProfile)
                .orElseGet(() -> {
                    System.out.println("✨ [Service] Creating placeholder profile for User: " + userId);
                    Profile newProfile = new Profile(userId, "New User");
                    newProfile.setUserType("STUDENT"); // Valeur par défaut
                    return profileRepository.save(newProfile);
                });
    }

    private Profile enrichProfile(Profile p) {
        p.setProfilePicUrl("http://localhost:8085/profiles/image/" + p.getId());
        return p;
    }

    // Modifier un profil par son ID interne
    public Profile updateProfile(Long profileId, Profile updatedProfile) {
        Optional<Profile> existingProfile = profileRepository.findById(profileId);

        if (existingProfile.isPresent()) {
            Profile profile = existingProfile.get();
            updateProfileFields(profile, updatedProfile);
            return profileRepository.save(profile);
        }
        return null;
    }

    // Modifier ou Créer un profil par son ID global utilisateur (DYNAMIQUE & ROBUSTE)
    public Profile updateProfileByUserId(Long userId, Profile updatedProfile) {
        Optional<Profile> existingProfile = profileRepository.findByUserId(userId);
        Profile profile;
        
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
        } else {
            profile = new Profile();
            profile.setUserId(userId);
            // On peut mettre des valeurs par défaut ou laisser vide
        }
        
        updateProfileFields(profile, updatedProfile);
        return profileRepository.save(profile);
    }

    private void updateProfileFields(Profile profile, Profile updatedProfile) {
        if (updatedProfile.getFullName() != null) profile.setFullName(updatedProfile.getFullName());
        if (updatedProfile.getBio() != null) profile.setBio(updatedProfile.getBio());
        if (updatedProfile.getInstitution() != null) profile.setInstitution(updatedProfile.getInstitution());
        if (updatedProfile.getMajor() != null) profile.setMajor(updatedProfile.getMajor());
        if (updatedProfile.getOrganizationName() != null) profile.setOrganizationName(updatedProfile.getOrganizationName());
        if (updatedProfile.getOrganizationType() != null) profile.setOrganizationType(updatedProfile.getOrganizationType());
        if (updatedProfile.getUserType() != null) profile.setUserType(updatedProfile.getUserType());
    }

    // Créer ou mettre à jour le profil après connexion (Kafka)
    public void createOrUpdateProfileAfterLogin(Long userId, String email, String fullName, String role,
                                               String institution, String major, String organizationName, String organizationType) {
        Optional<Profile> existingProfile = profileRepository.findByUserId(userId);

        if (existingProfile.isPresent()) {
            Profile profile = existingProfile.get();
            profile.setLastLogin(LocalDateTime.now());
            
            if (role != null) {
                profile.setUserType(role.toUpperCase());
            }

            if (fullName != null && !fullName.isEmpty()) {
                profile.setFullName(fullName);
            }

            if (institution != null) profile.setInstitution(institution);
            if (major != null) profile.setMajor(major);
            if (organizationName != null) profile.setOrganizationName(organizationName);
            if (organizationType != null) profile.setOrganizationType(organizationType);
            
            profileRepository.save(profile);
            System.out.println("✅ [Service] Updated profile (login) for user: " + userId);
        } else {
            String nameToUse = (fullName != null && !fullName.isEmpty()) ? fullName : (email != null ? email : "User " + userId);
            Profile newProfile = new Profile(userId, nameToUse);
            newProfile.setLastLogin(LocalDateTime.now());
            if (role != null) {
                newProfile.setUserType(role.toUpperCase());
            }

            newProfile.setInstitution(institution);
            newProfile.setMajor(major);
            newProfile.setOrganizationName(organizationName);
            newProfile.setOrganizationType(organizationType);

            profileRepository.save(newProfile);
            System.out.println("✅ [Service] Created new profile for user: " + userId + " as " + role);
        }
    }

    // Ajouter un événement à l'historique
    @Transactional
    public void addEventToHistory(Long userId, Long eventId, String type) {
        Optional<Profile> profileOpt = profileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            Profile profile = profileOpt.get();
            String history = profile.getEventsHistory();
            
            // Mise à jour simple (à améliorer avec une vraie gestion JSON)
            String newEntry = "{\"eventId\":" + eventId + ",\"type\":\"" + type + "\",\"date\":\"" + LocalDateTime.now() + "\"}";
            if (history == null || history.isEmpty() || history.equals("{}")) {
                profile.setEventsHistory("[" + newEntry + "]");
            } else {
                profile.setEventsHistory(history.replace("]", "," + newEntry + "]"));
            }
            profileRepository.save(profile);
        }
    }
}