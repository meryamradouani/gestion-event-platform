package com.gestionevent.profilservice.repository;

import com.gestionevent.profilservice.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByProfileId(Long profileId);
}
