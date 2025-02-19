package com.example.OnlineSellingApplicationBackend.entities;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Categories {
    @Id
    private Long id;
    private String nom;
    private String description;

    @OneToMany(mappedBy = "categories", cascade = CascadeType.ALL)
    private List<Produits> produits = new ArrayList<>();
}
