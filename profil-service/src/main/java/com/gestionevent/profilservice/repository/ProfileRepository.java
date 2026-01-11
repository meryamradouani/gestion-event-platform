package com.gestionevent.profilservice.repository;

import com.gestionevent.profilservice.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // Trouver par userId (pas par l'id de la table profiles)
    Optional<Profile> findByUserId(Long userId);

    // Vérifier si un profil existe pour un userId
    boolean existsByUserId(Long userId);
}
