package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
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
}


