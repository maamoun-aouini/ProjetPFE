package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)  // Ensures correct inheritance handling
public class Client extends Utilisateur {

    @Enumerated(EnumType.STRING)
    private TypeClient type;

    private String description;
    private String tel;


    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Entreprise entreprise;

    @OneToMany(mappedBy = "client")
    private List<Adresse> adresses;

    @OneToMany(mappedBy = "client")  // Remplace "clients" par "client"
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

    public TypeClient getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTel() {
        return tel;
    }

}
