package com.gestionevent.auth_service.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName; // Remplace firstName et lastName

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password; // Mappe sur la colonne password_hash du SQL

    @Column(nullable = false)
    private String role; // "STUDENT" ou "ORGANIZER"

    // --- Champs spécifiques : Étudiant (selon gestionevent.sql) ---
    @Column(name = "cne")
    private String cne;

    @Column(name = "filiere")
    private String filiere;

    @Column(name = "niveau")
    private String niveau;

    // --- Champs spécifiques : Organisateur (selon gestionevent.sql) ---
    @Column(name = "nom_etablissement")
    private String nomEtablissement;

    @Column(name = "type_organisateur")
    private String typeOrganisateur;

    // --- Constructeurs ---
    public User() {
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Étudiant
    public String getCne() {
        return cne;
    }

    public void setCne(String cne) {
        this.cne = cne;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    // Organisateur
    public String getNomEtablissement() {
        return nomEtablissement;
    }

    public void setNomEtablissement(String nomEtablissement) {
        this.nomEtablissement = nomEtablissement;
    }

    public String getTypeOrganisateur() {
        return typeOrganisateur;
    }

    public void setTypeOrganisateur(String typeOrganisateur) {
        this.typeOrganisateur = typeOrganisateur;
    }
}