package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface AdresseRepository extends JpaRepository<Adresse, Long> {
}
