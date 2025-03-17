package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Client;
import com.example.OnlineSellingApplicationBackend.entities.Commande;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmailAndMotDePasse(String email, String password);
    @Query("SELECT c FROM Client c " +
            "LEFT JOIN FETCH c.favoris f " +
            "LEFT JOIN FETCH c.notes n " +
            "LEFT JOIN FETCH c.commandes cmd " +
            "WHERE c.id = :clientId")
    Optional<Client> findByIdWithRelations(Long clientId);
    List<Client> findByActifTrue(); // Fetch only active clients
    boolean existsByEmail(String email);
    Optional<Client> findByEmail(String email);
    @Query("SELECT c FROM Client c WHERE c.adresse.id = :adresseId AND c.id != :clientId")
    List<Client> findClientsByAdresseId(@Param("adresseId") Long adresseId, @Param("clientId") Long clientId);
    @Query("SELECT c FROM Client c WHERE c.id = :ClientId ")
    List<Client> findClientsById(@Param("ClientId") Long ClientId);
}


