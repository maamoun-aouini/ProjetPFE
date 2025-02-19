package com.example.OnlineSellingApplicationBackend.Service;
import com.example.OnlineSellingApplicationBackend.entities.*;
import com.example.OnlineSellingApplicationBackend.DAO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private ProduitsRepository produitRepository;

    @Autowired
    private PaquetRepository paquetRepository;

    @Autowired
    private FavorisRepository favorisRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    /**
     * Register a new client account.
     */
    public Client registerClient(Client client) {
        return clientRepository.save(client);
    }

    /**
     * Authenticate a client by email and password.
     */
    public Optional<Client> authenticateClient(String email, String password) {
        return clientRepository.findByEmailAndMotDePasse(email, password);
    }

    /**
     * Update a client profile.
     */
    public Client updateClientProfile(Long clientId, Client updatedClient) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setNom(updatedClient.getNom());
            client.setEmail(updatedClient.getEmail());
            client.setProfil(updatedClient.getProfil());
            client.setDescription(updatedClient.getDescription());
            client.setTel(updatedClient.getTel());
            return clientRepository.save(client);
        }
        throw new RuntimeException("Client not found");
    }

    /**
     * Reset the client's password.
     */
    public void resetPassword(Long clientId, String newPassword) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setMotDePasse(newPassword);
            clientRepository.save(client);
        } else {
            throw new RuntimeException("Client not found");
        }
    }

    /**
     * Get all products.
     */
    public List<Produits> getAllProducts() {
        return produitRepository.findAll();
    }
    /**
     * Add a product to the client's favorites.
     */
    public Favoris addProductToFavorites(Long clientId, Long productId) {
        Optional<Client> client = clientRepository.findById(clientId);
        Optional<Produits> produit = produitRepository.findById(productId);

        if (client.isPresent() && produit.isPresent()) {
            Favoris favoris = new Favoris();
            favoris.setClient(client.get());  // Utiliser setClient() après modification
            favoris.setProduits(produit.get());
            return favorisRepository.save(favoris);
        }
        throw new RuntimeException("Client or Product not found");
    }


    /**
     * Get all favorite products of a client.
     */
    public List<Favoris> getClientFavorites(Long clientId) {
        return favorisRepository.findByClientId(clientId);
    }

    /**
     * Create a command for a client.
     */
    public Commande createCommand(Long clientId, List<Map<String, Object>> productData) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            Commande commande = new Commande();
            commande.setClient(client);
            // Create a list to store the LigneCommande objects
            List<LigneCommande> ligneCommands = new ArrayList<>();
            // For each product data (productId and quantity), create a LigneCommande entry
            for (Map<String, Object> product : productData) {
                Long productId = (Long) product.get("id_product");
                int quantity = (Integer) product.get("quantité");  // Retrieve quantity
                Optional<Produits> produitOptional = produitRepository.findById(productId);
                if (produitOptional.isPresent()) {
                    Produits produit = produitOptional.get();
                    LigneCommande ligneCommand = new LigneCommande();
                    ligneCommand.setCommande(commande);  // Set the Commande
                    ligneCommand.setProduit(produit);   // Set the Produit
                    ligneCommand.setQuantite(quantity); // Set the quantity
                    ligneCommands.add(ligneCommand);
                } else {
                    throw new RuntimeException("Product with ID " + productId + " not found");
                }
            }
            // Save the LigneCommande entries
            ligneCommandeRepository.saveAll(ligneCommands);
            // After saving the LigneCommand entries, save the Commande
            return commandeRepository.save(commande);
        }
        throw new RuntimeException("Client not found");
    }
    public List<Commande> getOrderHistory(Long clientId) {
        // Retrieve the client by ID
        Optional<Client> clientOptional = clientRepository.findById(clientId);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            // Fetch and return all commandes for the client
            return commandeRepository.findCommandesByClient(client);
        } else {
            throw new RuntimeException("Client not found");
        }
    }

}
