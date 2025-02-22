package com.example.OnlineSellingApplicationBackend.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Paiement {
    @Id
    private Long idPaiement;

    private double montant;
    private Date datePaiement;
    private String modePaiement;
    private boolean statut;

    @OneToOne
    private Commande commande;
}