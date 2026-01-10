package com.gestionevent.auth_service.dto;

/**
 * Data Transfer Object pour gérer les requêtes d'inscription.
 * Centralise les champs communs et spécifiques aux rôles.
 */
public class RegisterRequest {

    // --- Champs Communs ---
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // --- Champs Spécifiques : Étudiant ---
    private String cne;
    private String filiere;
    private String niveau;

    // --- Champs Spécifiques : Organisateur ---
    private String nomEtablissement;
    private String typeOrganisateur;

    // --- Constructeurs ---
    // Constructeur par défaut requis par Jackson
    public RegisterRequest() {
    }

    // --- Getters et Setters ---

    // Communs
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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