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
public class lignePaquet {
    @Id
    @ManyToOne
    @JoinColumn(name = "paquet_id", referencedColumnName = "id")
    private Paquet paquet;

    @Id
    @ManyToOne
    @JoinColumn(name = "produit_id", referencedColumnName = "id")
    private Produits produit;

    private int quantite;
}
