package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Commande {
    @Id
    private Long idCommande;

    public Long getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(Long idCommande) {
        this.idCommande = idCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setLigneCommandes(Set<LigneCommande> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public Client getClient() {
        return client;
    }

    public Set<LigneCommande> getLigneCommandes() {
        return ligneCommandes;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    private Date dateCommande;
    @Enumerated(EnumType.STRING)
    private EtatCommande etat;

    @ManyToOne
    private Client client;  // Change the name to "client" to match "mappedBy"

    @OneToMany(mappedBy = "commande")
    Set<LigneCommande> ligneCommandes;

    @OneToOne(mappedBy = "commande")
    private Paiement paiement;
}
