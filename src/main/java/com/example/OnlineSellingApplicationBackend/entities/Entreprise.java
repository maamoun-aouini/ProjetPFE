package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Entreprise {

    @Id
    private String matriculeFiscale;
    private String nom;
    @OneToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
