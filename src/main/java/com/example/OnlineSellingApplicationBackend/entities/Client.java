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
    private String tel;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Entreprise entreprise;

    @OneToMany(mappedBy = "client")
    private List<Adresse> adresses;

    @OneToMany(mappedBy = "client")

    private Set<Favoris> favoris;

    @OneToMany(mappedBy = "client")
    private Set<Note> notes;

    @OneToMany(mappedBy = "client")
    private Set<Commande> commandes;

    public void setType(TypeClient type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public void setAdresses(List<Adresse> adresses) {
        this.adresses = adresses;
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

    public TypeClient getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTel() {
        return tel;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public List<Adresse> getAdresses() {
        return adresses;
    }

    public Set<Favoris> getFavoris() {
        return favoris;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public Set<Commande> getCommandes() {
        return commandes;
    }
}
