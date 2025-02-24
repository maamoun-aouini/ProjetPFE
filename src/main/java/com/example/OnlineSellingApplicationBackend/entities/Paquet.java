package com.example.OnlineSellingApplicationBackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Paquet {
    @Id
    private Long id;
    @OneToMany(mappedBy = "paquet")
    private Set<lignePaquet> lignePaquets;
}
