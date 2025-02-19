package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Produits {
    @Id
    private Long id;
    private String nom;
    private String description;
    private String promotion;
    private String selection;
    private String photo;
    private double prix;
    private boolean disponibilite;

    @OneToOne
    private Categories categories;

    @OneToMany(mappedBy = "produit")
    private Set<LigneCommande> ligneCommandes;

    @OneToMany(mappedBy = "produits")
    private Set<Favoris> favoris;

    @OneToMany(mappedBy = "produit")
    private Set<Note> notes;

    @OneToMany(mappedBy = "produit")
    private Set<lignePaquet> lignePaquets;
}
