package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {


}
