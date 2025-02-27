package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Produits;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitsRepository extends JpaRepository<Produits, Long> {

    @Query("SELECT p FROM Produits p WHERE p.id = :id")
    Optional<Produits> findById(@Param("id") Long id);

    // Corrected UPDATE query that sets the relationship (e.g., set the category to null)
    @Modifying
    @Transactional
    @Query("UPDATE Produits p SET p.categories = null WHERE p.id = :id")
    int deleteCategoryFromProduct(@Param("id") Long id);  // Returns number of rows affected

    @Query("SELECT p FROM Produits p WHERE p.disponibilite = true")
    List<Produits> findAvailableProducts();

    @Query("SELECT p FROM Produits p LEFT JOIN FETCH p.notes")
    List<Produits> findAllWithRatings();
}