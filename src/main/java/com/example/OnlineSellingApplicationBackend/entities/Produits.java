package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Produits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Column(columnDefinition = "TEXT")
    private String description;
    private double promotionPartenaire;
    private double promotionParticulier;
    private String selection;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Produit_photos", joinColumns = @JoinColumn(name = "produit_id"))
    @Column(name = "photo_path")
    private Set<String> photos = new HashSet<>();
    private int quantite;
    @Column(nullable = false)
    private Double prix;
    private boolean disponibilite;

    @OneToMany(mappedBy = "produit")
    private Set<LigneCommande> ligneCommandes;

    @OneToMany(mappedBy = "produits")
    private Set<Favoris> favoris;

    @OneToMany(mappedBy = "produit")
    private Set<Note> notes;

    @OneToMany(mappedBy = "produit")
    private Set<LignePaquet> lignePaquets;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "produit_categorie", // ✅ Correction ici
            joinColumns = @JoinColumn(name = "produit_id"),
            inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )

    private Set<Categories> categories;
    // Getters and setters

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

    public Set<String> getPhotos() {
        return photos;
    }

    public void setPhoto(Set<String> photos) {
        this.photos = photos;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public Set<LigneCommande> getLigneCommandes() {
        return ligneCommandes;
    }

    public void setLigneCommandes(Set<LigneCommande> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }

    public Set<Favoris> getFavoris() {
        return favoris;
    }

    public void setFavoris(Set<Favoris> favoris) {
        this.favoris = favoris;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public Set<LignePaquet> getLignePaquets() {
        return lignePaquets;
    }

    public void setLignePaquets(Set<LignePaquet> lignePaquets) {
        this.lignePaquets = lignePaquets;
    }
}