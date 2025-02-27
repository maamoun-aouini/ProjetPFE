package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(ClePaquet.class)
public class LignePaquet {
    @Id
    @ManyToOne
    @JoinColumn(name = "paquet_id", referencedColumnName = "id")
    private Paquet paquet;

    @Id
    @ManyToOne
    @JoinColumn(name = "produit_id", referencedColumnName = "id")
    private Produits produit;


    private int quantite;

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Produits getProduit() {
        return produit;
    }

    public void setProduit(Produits produit) {
        this.produit = produit;
    }

    public Paquet getPaquet() {
        return paquet;
    }

    public void setPaquet(Paquet paquet) {
        this.paquet = paquet;
    }
}