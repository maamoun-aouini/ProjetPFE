package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitsRepository extends JpaRepository<Produits, Long> {


}