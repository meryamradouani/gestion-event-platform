package com.example.repository;

import com.example.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByProfileId(Long profileId);
}
