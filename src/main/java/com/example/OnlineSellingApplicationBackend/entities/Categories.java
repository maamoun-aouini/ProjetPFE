package com.example.OnlineSellingApplicationBackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    @NotBlank(message = "Category name is required")
    private String nom;

    @NotBlank(message = "Category description is required")
    private String description;

    @ElementCollection
    private Set<String> photo = new HashSet<>(); // Initialize to avoid null issues

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference // Avoids recursive serialization
    private Categories parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Handles parent-child relationships during serialization
    private List<Categories> subCategories;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private Set<Produits> produits = new HashSet<>(); // Initialize to avoid null issues

    public Set<String> getPhoto() {
        return photo;
    }

    public void setPhoto(Set<String> photo) {
        this.photo = photo;
    }

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