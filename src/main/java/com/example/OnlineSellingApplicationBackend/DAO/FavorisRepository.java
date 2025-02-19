package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.Favoris;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavorisRepository extends JpaRepository<Favoris, Long> {
    List<Favoris> findByClientId(Long clientId);
}
