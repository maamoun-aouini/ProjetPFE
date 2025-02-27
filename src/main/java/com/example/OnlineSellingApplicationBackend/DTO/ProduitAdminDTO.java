package com.example.OnlineSellingApplicationBackend.DTO;

import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor // ✅ Ajoute un constructeur par défaut
public class ProduitAdminDTO {

    private Long id;
    private String nom;
    private String description;
    private String photo;
    private int quantite;
    private double prix;
    private double promotionPartenaire;
    private double promotionParticulier;
    private Set<Categories> categories;
    private Double averageRating;

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public ProduitAdminDTO(Long id, String nom, String description, String photo, int quantite, double prix, double promotionPartenaire, double promotionParticulier, Set<Categories> categories, Double averageRating) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.photo = photo;
        this.quantite = quantite;
        this.prix = prix;
        this.promotionPartenaire = promotionPartenaire;
        this.promotionParticulier = promotionParticulier;
        this.categories = categories;
        this.averageRating = averageRating;
    }

    public Set<Categories> getCategories() {
        return categories;
    }

    public void setCategories(Set<Categories> categories) {
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
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
}
