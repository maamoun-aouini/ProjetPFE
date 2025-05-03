package com.example.OnlineSellingApplicationBackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Category name is required")
    private String nom;

    @NotBlank(message = "Category description is required")
    @Column(columnDefinition = "TEXT")
    private String description;
    // Change the ElementCollection to load eagerly
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "category_photos", joinColumns = @JoinColumn(name = "category_id"))
    @Column(name = "photo_path")
    private Set<String> photo = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference // Avoids recursive serialization
    private Categories parent;

    @OneToMany(mappedBy = "parent",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false,
            fetch = FetchType.LAZY)
    @JsonManagedReference // Handles parent-child relationships during serialization
    private List<Categories> subCategories = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Produits> produits = new ArrayList<>();

    // Méthodes utilitaires pour gérer les relations bidirectionnelles
    public void addSubCategory(Categories subCategory) {
        subCategories.add(subCategory);
        subCategory.setParent(this);
    }

    public void removeSubCategory(Categories subCategory) {
        subCategories.remove(subCategory);
        subCategory.setParent(null);
    } // Initialize to avoid null issues

    public List<Produits> getProduits() {
        return produits;
    }

    public void setProduits(List<Produits> produits) {
        this.produits = produits;
    }

    public Set<String> getPhoto() {
        return photo;
    }

    public void setPhoto(Set<String> photo) {
        this.photo = photo;
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