package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long>{
}
