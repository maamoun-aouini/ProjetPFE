package com.example.OnlineSellingApplicationBackend.Repositories;

import com.example.OnlineSellingApplicationBackend.entities.Commande;
import com.example.OnlineSellingApplicationBackend.entities.EtatCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c " +
            "JOIN FETCH c.ligneCommandes lc " +
            "JOIN FETCH lc.produit " +
            "WHERE c.client.id = :clientId " +
            "ORDER BY c.dateCommande DESC")
    List<Commande> findCommandesByClientId(@Param("clientId") Long clientId);

    @Query("SELECT c FROM Commande c " +
            "WHERE c.adresseLivraison.id = :adresseId ")
    List<Commande> findCommandesByAdresseId(@Param("adresseId") Long adresseId);

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

    @Query("SELECT c FROM Commande c " +
            "LEFT JOIN FETCH c.adresseLivraison a " +
            "LEFT JOIN FETCH a.ville v " +
            "LEFT JOIN FETCH v.pays " +
            "WHERE c.idCommande = :id")
    Optional<Commande> findByIdWithDetails(@Param("id") Long id);


    @Query(value = "SELECT " +
            "CASE WHEN :groupBy = 'DAY' THEN TO_CHAR(c.date_commande, 'YYYY-MM-DD') " +
            "ELSE TO_CHAR(c.date_commande, 'YYYY-MM') END as period, " +
            "SUM(CASE " +
            "    WHEN c.type = 'PACK' THEN (" +
            "        SELECT COALESCE(SUM(lcp.quantite * p.prix), 0) " +
            "        FROM ligne_command_pack lcp " +
            "        JOIN paquet p ON lcp.paquet_id = p.id " +
            "        WHERE lcp.commande_id = c.id_commande" +
            "    ) " +
            "    ELSE (" +
            "        SELECT COALESCE(SUM(lc.quantite * p.prix), 0) " +
            "        FROM ligne_commande lc " +
            "        JOIN produits p ON lc.produit_id = p.id " +
            "        WHERE lc.commande_id_commande = c.id_commande" +  // This is the correct join column
            "    ) " +
            "END) as total, " +
            "MIN(c.date_commande) as startDate " +
            "FROM commande c " +
            "WHERE c.date_commande BETWEEN :startDate AND :endDate " +
            "GROUP BY period " +
            "ORDER BY period", nativeQuery = true)
    List<Object[]> getSalesData(@Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                @Param("groupBy")  String groupBy);
    @Query(value = """
        SELECT 
            TRIM(TO_CHAR(c.date_commande, 'Day')) AS day, 
            SUM(CASE 
                WHEN c.type = 'PACK' THEN (
                    SELECT COALESCE(SUM(lcp.quantite * p.prix), 0)
                    FROM ligne_command_pack lcp
                    JOIN paquet p ON lcp.paquet_id = p.id
                    WHERE lcp.commande_id = c.id_commande
                )
                ELSE (
                    SELECT COALESCE(SUM(lc.quantite * p.prix), 0)
                    FROM ligne_commande lc
                    JOIN produits p ON lc.produit_id = p.id
                    WHERE lc.commande_id_commande = c.id_commande
                )
            END) AS total
        FROM Commande c 
        WHERE c.date_commande BETWEEN ?1 AND ?2 
        GROUP BY TO_CHAR(c.date_commande, 'Day') 
        ORDER BY ARRAY_POSITION(ARRAY['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'], 
            TRIM(TO_CHAR(c.date_commande, 'Day')))
        """, nativeQuery = true)
    List<Object[]> findDailySalesBetweenDates(LocalDate startDate, LocalDate endDate);


    @Query(value = """
    SELECT 
        category, 
        SUM(total) as total 
    FROM (
        SELECT
            LOWER(TRIM(c.nom)) AS category,  -- Normalize category names
            SUM(lc.quantite * p.prix) AS total
        FROM ligne_commande lc
        JOIN produits p ON lc.produit_id = p.id
        JOIN produit_categorie pc ON p.id = pc.produit_id
        JOIN categories c ON pc.categorie_id = c.id
        GROUP BY LOWER(TRIM(c.nom))

        UNION ALL

        SELECT
            LOWER(TRIM(c.nom)) AS category,  -- Normalize category names
            SUM(lcp.quantite * p.prix) AS total
        FROM ligne_command_pack lcp
        JOIN Paquet pq ON lcp.paquet_id = pq.id
        JOIN ligne_paquet lp ON pq.id = lp.paquet_id
        JOIN produits p ON lp.produit_id = p.id
        JOIN produit_categorie pc ON p.id = pc.produit_id
        JOIN categories c ON pc.categorie_id = c.id
        GROUP BY LOWER(TRIM(c.nom))
    ) combined_data
    GROUP BY category  -- Merge duplicates
""", nativeQuery = true)
    List<Object[]> findSalesByCategory();

    // In CommandeRepository.java
    @Query(value = """
    SELECT SUM(
        CASE WHEN c.type = 'PACK' THEN 
            (SELECT COALESCE(SUM(lcp.quantite * p.prix), 0) 
            FROM ligne_command_pack lcp 
            JOIN paquet p ON lcp.paquet_id = p.id 
            WHERE lcp.commande_id = c.id_commande)
        ELSE 
            (SELECT COALESCE(SUM(lc.quantite * pr.prix), 0) 
            FROM ligne_commande lc 
            JOIN produits pr ON lc.produit_id = pr.id 
            WHERE lc.commande_id_commande = c.id_commande)
        END
    )
    FROM commande c""",
            nativeQuery = true)
    Double getTotalRevenue();

    // In CommandeRepository.java
    @Query(value = """
    SELECT AVG(order_total) 
    FROM (
        SELECT 
            CASE WHEN c.type = 'PACK' THEN 
                (SELECT COALESCE(SUM(lcp.quantite * p.prix), 0) 
                 FROM ligne_command_pack lcp 
                 JOIN paquet p ON lcp.paquet_id = p.id 
                 WHERE lcp.commande_id = c.id_commande)
            ELSE 
                (SELECT COALESCE(SUM(lc.quantite * pr.prix), 0) 
                 FROM ligne_commande lc 
                 JOIN produits pr ON lc.produit_id = pr.id 
                 WHERE lc.commande_id_commande = c.id_commande)
            END AS order_total
        FROM commande c
    ) AS order_totals
    """,
            nativeQuery = true)
    Double getAverageOrderValue();

    // Add these repository methods
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.etat IN :states")
    long countByEtatIn(@Param("states") List<EtatCommande> states);
    @Query(value = """
    SELECT SUM(
        CASE WHEN c.type = 'PACK' THEN 
            (SELECT COALESCE(SUM(lcp.quantite * p.prix), 0) 
             FROM ligne_command_pack lcp 
             JOIN paquet p ON lcp.paquet_id = p.id 
             WHERE lcp.commande_id = c.id_commande)
        ELSE 
            (SELECT COALESCE(SUM(lc.quantite * pr.prix), 0) 
             FROM ligne_commande lc 
             JOIN produits pr ON lc.produit_id = pr.id 
             WHERE lc.commande_id_commande = c.id_commande)
        END
    ) 
    FROM commande c 
    WHERE c.date_commande BETWEEN :start AND :end""",
            nativeQuery = true)
    Double getTotalRevenueBetweenDates(@Param("start") LocalDate start,
                                       @Param("end") LocalDate end);
    @Query(value = """
    SELECT 
        DATE(c.date_commande) as order_date,
        COUNT(c.id_commande) as order_count
    FROM commande c
    WHERE c.date_commande BETWEEN ?1 AND ?2 
    GROUP BY DATE(c.date_commande)
    ORDER BY DATE(c.date_commande)
""", nativeQuery = true)
    List<Object[]> findDailyOrdersBetweenDates(LocalDate startDate, LocalDate endDate);

    // CommandeRepository.java
    @Query("SELECT c.etat, COUNT(c) FROM Commande c GROUP BY c.etat")
    List<Object[]> countOrdersByStatus();
}