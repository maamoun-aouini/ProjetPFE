package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
