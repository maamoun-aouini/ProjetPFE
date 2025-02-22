package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    // Fetch all categories along with their subcategories
    @Query("SELECT c FROM Categories c LEFT JOIN FETCH c.subCategories WHERE c.parent IS NULL")
    List<Categories> findAllParentCategories();

    // Fetch a specific category with its subcategories
    @Query("SELECT c FROM Categories c LEFT JOIN FETCH c.subCategories WHERE c.id = :id")
    Categories findCategoryWithSubcategories(@Param("id") Long id);

    public List<Categories> findAllByIdIn(List<Long> ids);

}