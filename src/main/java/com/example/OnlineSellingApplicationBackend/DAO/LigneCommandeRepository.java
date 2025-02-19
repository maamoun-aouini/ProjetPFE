package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
}
