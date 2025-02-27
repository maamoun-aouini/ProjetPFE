package com.example.OnlineSellingApplicationBackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nom;
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference // This avoids recursive serialization
    private Categories parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // This handles parent-child relationships during serialization
    private List<Categories> subCategories;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private Set<Produits> produits;

    public Set<Produits> getProduits() {
        return produits;
    }

    public void setProduits(Set<Produits> produits) {
        this.produits = produits;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Categories getParent() {
        return parent;
    }

    public void setParent(Categories parent) {
        this.parent = parent;
    }

    public List<Categories> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Categories> subCategories) {
        this.subCategories = subCategories;
    }
}