package com.example.service;

import com.example.entity.Profile;
import com.example.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private com.example.repository.ProfileImageRepository profileImageRepository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void createOrUpdateProfileAfterLogin_shouldCreateNewProfile_whenUserDoesNotExist() {
        // Arrange
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        profileService.createOrUpdateProfileAfterLogin(1L, "test@example.com", "Test User");

        // Assert
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void createOrUpdateProfileAfterLogin_shouldUpdateExistingProfile() {
        // Arrange
        Profile existingProfile = new Profile(1L, "Old Name");
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(existingProfile));

        // Act
        profileService.createOrUpdateProfileAfterLogin(1L, "test@example.com", "New Name");

        // Assert
        verify(profileRepository).save(existingProfile);
        assert (existingProfile.getFullName().equals("New Name"));
    }

    @Test
    void addEventToHistory_shouldAddEventToInscrits() {
        // Arrange
        Profile profile = new Profile(1L, "User");
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        // Act
        profileService.addEventToHistory(1L, 100L, "inscrit");

        // Assert
        verify(profileRepository).save(profile);
        String history = profile.getEventsHistory();
        assert (history.contains("\"inscrits\":[100]"));
    }

    @Test
    void addEventToHistory_shouldAddEventToCreated() {
        // Arrange
        Profile profile = new Profile(1L, "User");
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        // Act
        profileService.addEventToHistory(1L, 101L, "créé");

        // Assert
        verify(profileRepository).save(profile);
        String history = profile.getEventsHistory();
        assert (history.contains("\"créés\":[101]"));
    }
}
