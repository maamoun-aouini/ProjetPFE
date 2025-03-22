package com.example.OnlineSellingApplicationBackend.DTO;

import java.util.Set;

public class FavoriteProductDTO {
    private Long clientId;
    private Long produitId;
    private String nom;
    private String description;
    private double promotionPartenaire;
    private double promotionParticulier;  //;
    private String selection;
    private Set<String> photo;
    private double prix;
    private boolean disponibilite;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPromotionPartenaire() {
        return promotionPartenaire;
    }

    public void setPromotionPartenaire(double promotionPartenaire) {
        this.promotionPartenaire = promotionPartenaire;
    }

    public double getPromotionParticulier() {
        return promotionParticulier;
    }

    public void setPromotionParticulier(double promotionParticulier) {
        this.promotionParticulier = promotionParticulier;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }


    public Set<String> getPhoto() {
        return photo;
    }

    public void setPhoto(Set<String> photo) {
        this.photo = photo;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public FavoriteProductDTO(Long clientId, Long produitId, String nom, String description, double promotionPartenaire, double promotionParticulier, String selection, Set<String> photo, double prix, boolean disponibilite) {
        this.clientId = clientId;
        this.produitId = produitId;
        this.nom = nom;
        this.description = description;
        this.promotionPartenaire = promotionPartenaire;
        this.promotionParticulier = promotionParticulier;
        this.selection = selection;
        this.photo = photo;
        this.prix = prix;
        this.disponibilite = disponibilite;
    }
}

