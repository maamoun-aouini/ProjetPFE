package com.example.OnlineSellingApplicationBackend.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Ville {
    @Id
    private Long id;

    private String nom;

    @ManyToOne
    private Pays pays;

    @OneToMany(mappedBy = "ville")
    private List<Adresse> adresses;

}
