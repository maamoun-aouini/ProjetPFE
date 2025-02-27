package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Paquet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Correct placement
    private Long id;

    private String nom;
    private double prix;

    @OneToMany(mappedBy = "paquet")
    private Set<LignePaquet> lignePaquets;

    @OneToMany(mappedBy = "paquet")
    private Set<LigneCommandPack> ligneCommandePack;

    public Set<LigneCommandPack> getLigneCommandePack() {
        return ligneCommandePack;
    }

    public void setLigneCommandePack(Set<LigneCommandPack> ligneCommandePack) {
        this.ligneCommandePack = ligneCommandePack;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Set<LignePaquet> getLignePaquets() {
        return lignePaquets;
    }

    public void setLignePaquets(Set<LignePaquet> lignePaquets) {
        this.lignePaquets = lignePaquets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}