package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true) // Ensures correct inheritance handling

public class Client extends Utilisateur {
    @Enumerated(EnumType.STRING)
    private TypeClient type;

    private String description;
    private boolean actif = true;
    private String tel;

    @OneToOne(mappedBy = "client")
    private Entreprise entreprise;

    @OneToOne
    @JoinColumn(name = "adresse_id", unique = true, nullable = false)
    private Adresse adresse;


    @OneToMany(mappedBy = "client")
    private Set<Favoris> favoris;

    @OneToMany(mappedBy = "client")
    private Set<Note> notes;

    @OneToMany(mappedBy = "client")
    private Set<Commande> commandes;



    public Set<Favoris> getFavoris() {
        return favoris;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public Set<Commande> getCommandes() {
        return commandes;
    }

    public TypeClient getType() {
        return type;
    }

    public void setType(TypeClient type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public void setFavoris(Set<Favoris> favoris) {
        this.favoris = favoris;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public void setCommandes(Set<Commande> commandes) {
        this.commandes = commandes;
    }
}
