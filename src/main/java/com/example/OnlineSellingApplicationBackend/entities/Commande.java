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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idCommande;

    private Date dateCommande;

    @Enumerated(EnumType.STRING)
    private EtatCommande etat;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "adresse_livraison_id")
    private Adresse adresseLivraison;

    @OneToMany(mappedBy = "commande" , cascade = CascadeType.ALL)
    private List<LigneCommande> ligneCommandes;

    @OneToOne(mappedBy = "commande")
    private Paiement paiement;

    @Enumerated(EnumType.STRING)
    private TypeCommande type;

    @OneToMany(mappedBy = "commande")
    private List<LigneCommandPack> ligneCommandePack ;

    public List<LigneCommandPack> getLigneCommandePack() {
        return ligneCommandePack;
    }
    public void setLigneCommandePack(List<LigneCommandPack> ligneCommandePack) {
        this.ligneCommandePack = ligneCommandePack;
    }

    public TypeCommande getType() {
        return type;
    }

    public void setType(TypeCommande type) {
        this.type = type;
    }

    public Long getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(Long idCommande) {
        this.idCommande = idCommande;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Adresse getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(Adresse adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public List<LigneCommande> getLigneCommandes() {
        return ligneCommandes;
    }

    public void setLigneCommandes(List<LigneCommande> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }
}