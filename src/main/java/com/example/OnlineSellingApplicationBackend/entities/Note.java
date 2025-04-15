package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@IdClass(CleNote.class)
public class Note {

    @Id
    @ManyToOne
    @JoinColumn(name = "client_id") // Foreign key column in the Note table
    private Client client; // Match CleNote field name

    @Id
    @ManyToOne
    @JoinColumn(name = "produit_id") // Foreign key column in the Note table
    private Produits produit; // Match CleNote field name

    private int rating; // Example: Rating field
    private String commentaire; // Example: Comment field
    private LocalDateTime date;

    public void setRating(int rating) {
        this.rating = rating;
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Produits getProduit() {
        return produit;
    }

    public void setProduit(Produits produit) {
        this.produit = produit;
    }

    public int getRating() {
        return rating;
    }


    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}