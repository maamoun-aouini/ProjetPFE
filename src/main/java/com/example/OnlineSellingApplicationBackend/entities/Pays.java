package com.example.OnlineSellingApplicationBackend.entities;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Pays {

    @Id
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "pays", cascade = CascadeType.ALL)
    private List<Ville> villes;
}
