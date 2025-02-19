package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.LigneCommande;
import com.example.OnlineSellingApplicationBackend.entities.Paquet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaquetRepository extends JpaRepository<Paquet, Long> {
    @Query(value = "SELECT p.id AS paquet_id, pr.id AS produit_id, pr.name AS produit_name " +
            "FROM paquet p " +
            "JOIN ligne_paquet lp ON lp.id_paquet = p.id " +
            "JOIN produit pr ON pr.id = lp.id_produit", nativeQuery = true)
    List<Object[]> findAllPaquetsWithProduits();


}
