package com.example.OnlineSellingApplicationBackend.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Adresse {
    @Id
    private Long id;

    private String rue;
    private String numero;
    private String indication;

    @ManyToOne
    private Ville ville;    

    @ManyToOne
    private Client client;
}
