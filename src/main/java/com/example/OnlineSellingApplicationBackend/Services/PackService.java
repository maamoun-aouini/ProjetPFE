package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.PackUpdateRequest;
import com.example.OnlineSellingApplicationBackend.DTO.ProduitQuantite;
import com.example.OnlineSellingApplicationBackend.Repositories.LignePaquetRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.PaquetRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ProduitsRepository;
import com.example.OnlineSellingApplicationBackend.entities.LignePaquet;
import com.example.OnlineSellingApplicationBackend.entities.Paquet;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class PackService {
    private final PaquetRepository paquetRepository;
    private final ProduitsRepository produitRepository;
    private final LignePaquetRepository lignePaquetRepository;

    public PackService(PaquetRepository paquetRepository, ProduitsRepository produitRepository, LignePaquetRepository lignePaquetRepository) {
        this.paquetRepository = paquetRepository;
        this.produitRepository = produitRepository;
        this.lignePaquetRepository = lignePaquetRepository;
    }

    public Map<String, Object> createPack(String nomPaquet, List<Long> produitIds, List<Integer> quantites, double prixPack) {
        double sommePrixProduits = 0.0;
        List<Map<String, Object>> produitsDetails = new ArrayList<>();
        List<LignePaquet> lignePaquets = new ArrayList<>();

        for (int i = 0; i < produitIds.size(); i++) {
            Long produitId = produitIds.get(i);
            int quantite = quantites.get(i);
            Optional<Produits> produitOptional = produitRepository.findById(produitId);

            if (produitOptional.isPresent()) {
                Produits produit = produitOptional.get();
                double prixTotalProduit = produit.getPrix() * quantite;
                sommePrixProduits += prixTotalProduit;

                //  Create a new LignePaquet for each product in the pack
                LignePaquet lignePaquet = new LignePaquet();
                lignePaquet.setProduit(produit);
                lignePaquet.setQuantite(quantite);
                lignePaquets.add(lignePaquet);

                //  Add product details for response
                Map<String, Object> produitInfo = new HashMap<>();
                produitInfo.put("id_produit", produit.getId());
                produitInfo.put("nom", produit.getNom());
                produitInfo.put("prix_unitaire", produit.getPrix());
                produitInfo.put("quantite", quantite);
                produitInfo.put("prix_total", prixTotalProduit);
                produitsDetails.add(produitInfo);
            }
        }

        //  Ensure the pack price is lower than the sum of product prices
        if (prixPack >= sommePrixProduits) {
            throw new IllegalArgumentException("Le prix du pack doit être inférieur à la somme des prix des produits !");
        }

        //  Save the new pack
        Paquet paquet = new Paquet();
        paquet.setNom(nomPaquet);
        paquet.setPrix(prixPack);
        paquetRepository.save(paquet);

        //  Link LignePaquet to Paquet
        for (LignePaquet lignePaquet : lignePaquets) {
            lignePaquet.setPaquet(paquet);  // Set the newly created pack
            lignePaquetRepository.save(lignePaquet); // Save relation
        }

        //  Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("nom_paquet", nomPaquet);
        response.put("prix_pack", prixPack);
        response.put("somme_prix_produits", sommePrixProduits);
        response.put("produits", produitsDetails);

        return response;
    }
    // PackService.java

    public List<Map<String, Object>> getAllPacks() {
        List<Paquet> paquets = paquetRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Paquet paquet : paquets) {
            response.add(buildPackResponse(paquet));
        }
        return response;
    }

    public Map<String, Object> getPackById(Long packId) {
        Paquet paquet = paquetRepository.findById(packId)
                .orElseThrow(() -> new NoSuchElementException("Pack non trouvé !"));
        return buildPackResponse(paquet);
    }


    public void deletePack(Long packId) {
        Paquet paquet = paquetRepository.findById(packId)
                .orElseThrow(() -> new NoSuchElementException("Pack non trouvé !"));

        // Supprimer d'abord les lignes du paquet
        lignePaquetRepository.deleteAll(paquet.getLignePaquets());

        // Puis supprimer le paquet
        paquetRepository.delete(paquet);
    }

    private Map<String, Object> buildPackResponse(Paquet paquet) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> produitsDetails = new ArrayList<>();
        double sommePrix = 0.0;

        for (LignePaquet ligne : paquet.getLignePaquets()) {
            Produits produit = ligne.getProduit();
            double prixTotal = produit.getPrix() * ligne.getQuantite();
            sommePrix += prixTotal;

            Map<String, Object> produitInfo = new HashMap<>();
            produitInfo.put("id_produit", produit.getId());
            produitInfo.put("nom", produit.getNom());
            produitInfo.put("prix_unitaire", produit.getPrix());
            produitInfo.put("quantite", ligne.getQuantite());
            produitInfo.put("prix_total", prixTotal);
            produitsDetails.add(produitInfo);
        }

        response.put("id_pack", paquet.getId());
        response.put("nom_paquet", paquet.getNom());
        response.put("prix_pack", paquet.getPrix());
        response.put("somme_prix_produits", sommePrix);
        response.put("produits", produitsDetails);

        return response;
    }
    public Map<String, Object> updatePack(Long packId, PackUpdateRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Validate existing pack
            Paquet paquet = paquetRepository.findById(packId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pack not found with id: " + packId));

            // 2. Update pack details
            if (request.getNomPaquet() != null && !request.getNomPaquet().isEmpty()) {
                paquet.setNom(request.getNomPaquet());
            }

            // 3. Validate products list
            if (request.getProduits() == null || request.getProduits().isEmpty()) {
                throw new IllegalArgumentException("Products list cannot be empty");
            }

            // 4. Calculate new total product prices
            double sommePrixProduits = 0;
            Map<Long, Integer> newProductQuantities = new HashMap<>();
            List<Map<String, Object>> produitsDetails = new ArrayList<>();

            for (ProduitQuantite pq : request.getProduits()) {
                Produits produit = produitRepository.findById(pq.getProduitId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + pq.getProduitId()));

                if (pq.getQuantite() == null || pq.getQuantite() < 1) {
                    throw new IllegalArgumentException("Invalid quantity for product ID: " + pq.getProduitId());
                }

                double productTotal = produit.getPrix() * pq.getQuantite();
                sommePrixProduits += productTotal;
                newProductQuantities.put(pq.getProduitId(), pq.getQuantite());

                // Build product details
                Map<String, Object> produitInfo = new HashMap<>();
                produitInfo.put("produitId", produit.getId());
                produitInfo.put("nom", produit.getNom());
                produitInfo.put("prix_unitaire", produit.getPrix());
                produitInfo.put("quantite", pq.getQuantite());
                produitInfo.put("prix_total", productTotal);
                produitsDetails.add(produitInfo);
            }

            // 5. Validate pack price
            if (request.getPrixPack() != null) {
                if (request.getPrixPack() >= sommePrixProduits) {
                    throw new IllegalArgumentException("Pack price must be lower than total product prices (€" + sommePrixProduits + ")");
                }
                paquet.setPrix(request.getPrixPack());
            }

            // 6. Manage existing relationships
            List<LignePaquet> existingRelations = lignePaquetRepository.findByPaquetId(packId);
            Map<Long, LignePaquet> existingProductMap = existingRelations.stream()
                    .collect(Collectors.toMap(
                            lp -> lp.getProduit().getId(),
                            Function.identity()
                    ));

            // 7. Identify changes
            List<LignePaquet> toDelete = new ArrayList<>();
            List<LignePaquet> toUpdate = new ArrayList<>();
            List<LignePaquet> toAdd = new ArrayList<>();

            // Process existing relationships
            for (LignePaquet existing : existingRelations) {
                Long produitId = existing.getProduit().getId();

                if (newProductQuantities.containsKey(produitId)) {
                    // Update quantity if changed
                    int newQuantite = newProductQuantities.get(produitId);
                    if (existing.getQuantite() != newQuantite) {
                        existing.setQuantite(newQuantite);
                        toUpdate.add(existing);
                    }
                    newProductQuantities.remove(produitId);
                } else {
                    // Remove obsolete relationships
                    toDelete.add(existing);
                }
            }

            // Process new products
            newProductQuantities.forEach((produitId, quantite) -> {
                Produits produit = produitRepository.findById(produitId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + produitId));

                LignePaquet newRelation = new LignePaquet();
                newRelation.setPaquet(paquet);
                newRelation.setProduit(produit);
                newRelation.setQuantite(quantite);
                toAdd.add(newRelation);
            });

            // 8. Execute database operations
            if (!toDelete.isEmpty()) {
                lignePaquetRepository.deleteAll(toDelete);
            }
            if (!toUpdate.isEmpty()) {
                lignePaquetRepository.saveAll(toUpdate);
            }
            if (!toAdd.isEmpty()) {
                lignePaquetRepository.saveAll(toAdd);
            }

            // 9. Save pack updates
            paquetRepository.save(paquet);

            // 10. Prepare response
            response.put("id", paquet.getId());
            response.put("nom_paquet", paquet.getNom());
            response.put("prix_pack", paquet.getPrix());
            response.put("somme_prix_produits", sommePrixProduits);
            response.put("produits", produitsDetails);
            response.put("added_products", toAdd.size());
            response.put("updated_products", toUpdate.size());
            response.put("removed_products", toDelete.size());

        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }

        return response;
    }
}
