package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Pays;
import com.example.OnlineSellingApplicationBackend.entities.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface VilleRepository extends JpaRepository<Ville, Long> {
    Optional<Ville> findByNomAndPays(String nomVille, Pays pays);
}
