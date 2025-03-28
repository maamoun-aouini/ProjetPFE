package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    // Fetch all categories along with their subcategories
    @Query("SELECT c FROM Categories c LEFT JOIN FETCH c.subCategories WHERE c.parent IS NULL")
    List<Categories> findAllParentCategories();

    // Fetch a specific category with its subcategories
    @Query("SELECT c FROM Categories c LEFT JOIN FETCH c.subCategories WHERE c.id = :id")
    Categories findCategoryWithSubcategories(@Param("id") Long id);

    public List<Categories> findAllByIdIn(List<Long> ids);
    List<Categories> findByParentIsNull(); // Fetch top-level categories
    // CategoriesRepository.java
    @Query("SELECT DISTINCT c FROM Categories c " +
            "LEFT JOIN FETCH c.photo " +
            "LEFT JOIN FETCH c.subCategories " +
            "WHERE c.parent IS NULL")
    List<Categories> findAllRootCategories();
    @Query("SELECT c FROM Categories c LEFT JOIN FETCH c.subCategories WHERE c.parent IS NULL")
    List<Categories> findAllRootCategoriesWithSubs();

    @Query(
            value = "SELECT c.nom AS category, COUNT(pc.produit_id) AS productCount " +
                    "FROM categories c " +
                    "LEFT JOIN produit_categorie pc ON c.id = pc.categorie_id " +
                    "GROUP BY c.id",
            nativeQuery = true
    )
    List<Object[]> findCategoryProductCounts();

    List<Categories> findByParentId(Long parentId);

}
