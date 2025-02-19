package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.Commande;
import com.example.OnlineSellingApplicationBackend.entities.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandeRepository extends CrudRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c " +
            "JOIN FETCH c.ligneCommandes lc " +
            "JOIN FETCH lc.produit " +
            "WHERE c.client = :client " +
            "ORDER BY c.dateCommande DESC")
    List<Commande> findCommandesByClient(Client client);
}
