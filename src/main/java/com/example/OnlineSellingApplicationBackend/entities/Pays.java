package com.example.OnlineSellingApplicationBackend.entities;
import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ou une autre stratégie selon votre besoin
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "pays", cascade = CascadeType.ALL)
    private List<Ville> villes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Ville> getVilles() {
        return villes;
    }

    public void setVilles(List<Ville> villes) {
        this.villes = villes;
    }
}
