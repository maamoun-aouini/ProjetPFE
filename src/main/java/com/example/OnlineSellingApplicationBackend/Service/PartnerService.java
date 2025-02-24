package com.example.OnlineSellingApplicationBackend.Service;

import com.example.OnlineSellingApplicationBackend.DAO.PaquetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class PartnerService {
    @Autowired
    private PaquetRepository paquetRepository;

    public List<Map<String, Object>> getPacksAndOffers() {
        // Fetch the raw result set of Paquet and Produit pairs
        List<Object[]> results = paquetRepository.findAllPaquetsWithProduits();

        // Create a map to group Paquet by id_paquet
        Map<Long, Map<String, Object>> paquetMap = new HashMap<>();

        // Iterate over the results to populate the Paquet entities and group their products
        for (Object[] result : results) {
            Long paquetId = (Long) result[0];  // Paquet ID
            Long produitId = (Long) result[1]; // Produit ID
            String produitName = (String) result[2]; // Produit Name

            // If the Paquet is not already in the map, create a new entry for it
            if (!paquetMap.containsKey(paquetId)) {
                Map<String, Object> paquetData = new HashMap<>();
                paquetData.put("id_paquet", paquetId);
                paquetData.put("produits", new ArrayList<Map<String, Object>>());
                paquetMap.put(paquetId, paquetData);
            }

            // Add the product to the Paquet's list of products
            Map<String, Object> paquetData = paquetMap.get(paquetId);
            List<Map<String, Object>> produits = (List<Map<String, Object>>) paquetData.get("produits");

            // Create a map for the product
            Map<String, Object> produitData = new HashMap<>();
            produitData.put("id_product", produitId);
            produitData.put("name", produitName);

            produits.add(produitData);
        }

        // Return the result as a list of maps, each containing paquet data with products
        return new ArrayList<>(paquetMap.values());
    }

}
