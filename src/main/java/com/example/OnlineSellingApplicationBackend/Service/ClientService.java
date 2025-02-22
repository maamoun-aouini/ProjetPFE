package com.example.OnlineSellingApplicationBackend.Service;
import com.example.OnlineSellingApplicationBackend.entities.*;
import com.example.OnlineSellingApplicationBackend.DAO.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.OnlineSellingApplicationBackend.DTO.*;
import java.util.Date;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private CategoriesRepository categoriesRepository;

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


    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private AdresseRepository adresseRepository;
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
    public List<ProduitDTO> getAllProducts() {
        return produitRepository.findAvailableProducts()
                .stream()
                .map(produit -> new ProduitDTO(
                        produit.getId(),
                        produit.getNom(),
                        produit.getDescription(),
                        produit.getPhoto(),
                        produit.getQuantite(),
                        produit.getPrix()
                ))
                .collect(Collectors.toList());
    }    /**
     * Add a product to the client's favorites.
     */
    public Favoris addProductToFavorites(Long clientId, Long productId) {
        // Retrieve client and product by their IDs
        Optional<Client> client = clientRepository.findById(clientId);
        Optional<Produits> produit = produitRepository.findById(productId);
        // Check if both exist
        if (client.isPresent() && produit.isPresent()) {
            Favoris favoris = new Favoris();

            // Set composite key fields
            favoris.setClientId(clientId);
            favoris.setProduitId(productId);

            // Set relationships
            favoris.setClient(client.get());
            favoris.setProduits(produit.get());

            // Save and return the new Favoris entity
            return favorisRepository.save(favoris);
        }

        // If client or product doesn't exist, throw an exception
        throw new RuntimeException("Client or Product not found");
    }

    /**
     * Get all favorite products of a client.
     */
    public List<FavoriteProductDTO> getClientFavorites(Long clientId) {
        // Step 1: Fetch the product IDs from the 'favoris' table for the given client ID
        List<Long> produitIds = favorisRepository.findProduitIdsByClientId(clientId);

        // Step 2: Fetch the actual products from the 'produits' table using those IDs
        List<FavoriteProductDTO> favoriteProducts = new ArrayList<>();
        for (Long id : produitIds) {
            Optional<Produits> produit = produitRepository.findById(id);
            produit.ifPresent(p -> favoriteProducts.add(
                    new FavoriteProductDTO(
                            clientId,
                            p.getId(),
                            p.getNom(),
                            p.getDescription(),
                            p.getPromotionPartenaire(),
                            p.getPromotionParticulier(),  // Assuming this exists in the 'produits' entity
                            p.getSelection(),   // Assuming this exists in the 'produits' entity
                            p.getPhoto(),       // Assuming this exists in the 'produits' entity
                            p.getPrix(),
                            p.isDisponibilite() // Assuming this exists in the 'produits' entity
                    )
            ));
        }

        return favoriteProducts;
    }

    public Commande createCommand(Long clientId, AddressRequest addressRequest, List<ProductRequest> productData) {
        // Retrieve the client
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isEmpty()) {
            throw new RuntimeException("Client not found");
        }
        Client client = clientOptional.get();

        // Create or find the Ville and Pays
        Pays pays =     paysRepository.findByNom(addressRequest.getNomPays())
                .orElseGet(() -> {
                    Pays newPays = new Pays();
                    newPays.setNom(addressRequest.getNomPays());
                    return paysRepository.save(newPays);
                });

        Ville ville = villeRepository.findByNomAndPays(addressRequest.getNomVille(), pays)
                .orElseGet(() -> {
                    Ville newVille = new Ville();
                    newVille.setNom(addressRequest.getNomVille());
                    newVille.setPays(pays);
                    return villeRepository.save(newVille);
                });

        // Create the Adresse
        Adresse adresse = new Adresse();
        adresse.setRue(addressRequest.getRue());
        adresse.setNumero(addressRequest.getNumero());
        adresse.setIndication(addressRequest.getIndication());
        adresse.setVille(ville);
        adresse.setClient(client);
        Adresse savedAdresse = adresseRepository.save(adresse);

        // Create the Commande
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setAdresseLivraison(savedAdresse); // Link the delivery address
        commande.setDateCommande(new Date());
        commande.setEtat(EtatCommande.EnCoursDeTraitement);

        // Process product data and create LigneCommande entries
        List<LigneCommande> ligneCommands = new ArrayList<>();
        for (ProductRequest product : productData) {
            Long productId = product.getId_product();
            int quantity = product.getQuantité();

            Optional<Produits> produitOptional = produitRepository.findById(productId);
            if (produitOptional.isEmpty()) {
                throw new RuntimeException("Product with ID " + productId + " not found");
            }
            Produits produit = produitOptional.get();

            LigneCommande ligneCommand = new LigneCommande();
            ligneCommand.setCommande(commande);
            ligneCommand.setProduit(produit);
            ligneCommand.setQuantite(quantity);
            ligneCommands.add(ligneCommand);
        }

        // Save the Commande and its LigneCommande entries
        Commande savedCommande = commandeRepository.save(commande);
        for (LigneCommande ligneCommand : ligneCommands) {
            ligneCommand.setCommande(savedCommande);
        }
        ligneCommandeRepository.saveAll(ligneCommands);

        return savedCommande;
    }

    public List<Commande> getOrderHistory(Long clientId) {
        // Fetch and return all commandes for the client
        return commandeRepository.findCommandesByClientId(clientId);
    }
    public List<OrderHistoryDTO> mapCommandeToDTO(List<Commande> commandes) {
        List<OrderHistoryDTO> dtoList = new ArrayList<>();

        for (Commande commande : commandes) {
            OrderHistoryDTO dto = new OrderHistoryDTO();
            dto.setOrderId(commande.getIdCommande());
            dto.setOrderDate(commande.getDateCommande());
            dto.setOrderState(commande.getEtat().toString());

            List<OrderHistoryDTO.LigneCommandeDTO> ligneCommandes = new ArrayList<>();
            for (LigneCommande ligneCommande : commande.getLigneCommandes()) {
                OrderHistoryDTO.LigneCommandeDTO ligneDTO = new OrderHistoryDTO.LigneCommandeDTO();
                ligneDTO.setProductId(ligneCommande.getProduit().getId());
                ligneDTO.setProductName(ligneCommande.getProduit().getNom());
                ligneDTO.setQuantity(ligneCommande.getQuantite());
                ligneCommandes.add(ligneDTO);
            }
            dto.setLigneCommandes(ligneCommandes);
            dtoList.add(dto);
        }
        return dtoList;
    }
    public Map<String, Object> getClientInfo(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));

        // Prepare the response map
        Map<String, Object> response = new HashMap<>();
        response.put("id", client.getId());
        response.put("nom", client.getNom());
        response.put("email", client.getEmail());
        response.put("profil", client.getProfil());
        response.put("description", client.getDescription());
        response.put("tel", client.getTel());
        response.put("type", client.getType() != null ? client.getType().toString() : null);

        // Check if the client has an entreprise
        Entreprise entreprise = client.getEntreprise();
        if (entreprise != null) {
            response.put("matriculeFiscale", entreprise.getMatriculeFiscale());
            response.put("nomEntreprise", entreprise.getNom());
        }

        return response;
    }

    // Method to get products for a category and all its subcategories (including deeper levels)
    public List<ProduitDTO> getProductsByCategoryWithSubcategories(Long categoryId) {
        // Fetch the category by ID
        Categories category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        // Create a set to store products from the category and all subcategories
        Set<Produits> products = new HashSet<>();

        // Add products from the category and its subcategories (recursively)
        collectProducts(category, products);
        return products.stream()
                .map(produit -> new ProduitDTO(
                        produit.getId(),
                        produit.getNom(),
                        produit.getDescription(),
                        produit.getPhoto(),
                        produit.getQuantite(),
                        produit.getPrix()
                ))
                .collect(Collectors.toList());
    }
    // Recursive method to collect products from the category and all its subcategories
    private void collectProducts(Categories category, Set<Produits> products) {
        // Add the products from the current category
        products.addAll(category.getProduits());

        // Recursively add products from the subcategories
        if (category.getSubCategories() != null) {
            for (Categories subCategory : category.getSubCategories()) {
                collectProducts(subCategory, products);  // Recursive call to add products from subcategories
            }
        }
    }
}
