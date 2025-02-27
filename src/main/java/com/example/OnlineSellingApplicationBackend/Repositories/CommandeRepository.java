package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Commande;
import com.example.OnlineSellingApplicationBackend.entities.EtatCommande;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends CrudRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c " +
            "JOIN FETCH c.ligneCommandes lc " +
            "JOIN FETCH lc.produit " +
            "WHERE c.client.id = :clientId " +
            "ORDER BY c.dateCommande DESC")
    List<Commande> findCommandesByClientId(@Param("clientId") Long clientId);


    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Commande c " +
            "JOIN c.ligneCommandes lc " +
            "WHERE c.client.id = :clientId " +
            "AND lc.produit.id = :productId " +
            "AND c.etat IN :validStates")
    boolean existsValidPurchaseForRating(
            @Param("clientId") Long clientId,
            @Param("productId") Long productId,
            @Param("validStates") List<EtatCommande> validStates
    );
}
