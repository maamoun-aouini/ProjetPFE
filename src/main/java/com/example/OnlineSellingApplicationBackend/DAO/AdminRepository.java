package com.example.OnlineSellingApplicationBackend.DAO;

import com.example.OnlineSellingApplicationBackend.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
