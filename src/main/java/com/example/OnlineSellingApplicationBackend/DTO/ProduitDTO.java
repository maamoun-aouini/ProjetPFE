package com.example.OnlineSellingApplicationBackend.DTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class ProduitDTO {
    private Long id;
    private String nom;
    private String description;
    private String photo;
    private int quantite;
    private double prix;

    public ProduitDTO(Long id, String nom, String description, String photo, int quantite, double prix) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.photo = photo;
        this.quantite = quantite;
        this.prix = prix;
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
}
