package com.example.OnlineSellingApplicationBackend.Services;
import com.example.OnlineSellingApplicationBackend.entities.*;
import com.example.OnlineSellingApplicationBackend.Repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.Exeptions.*;
import com.example.OnlineSellingApplicationBackend.Security.SecurityConfig;
import java.util.Date;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * Register a new client account.
     */
    public List<ClientInfoAdmin> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(client -> new ClientInfoAdmin(
                        new ClientInfoResponse(
                                client.getId(),
                                client.getNom(),
                                client.getEmail(),
                                client.getTel(),
                                client.getType() != null ? client.getType().toString() : "N/A", // Handle null type
                                client.getDescription(),
                                Optional.ofNullable(client.getEntreprise())
                                        .map(Entreprise::getNom)
                                        .orElse("N/A"),
                                Optional.ofNullable(client.getEntreprise())
                                        .map(Entreprise::getMatriculeFiscale)
                                        .orElse("N/A")
                        ),
                        new AddressResponse(client.getAdresse())
                ))
                .collect(Collectors.toList());
    }
    public Client registerClient(ClientRegistrationRequest request) {
        // 1. Check email uniqueness
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // 2. Create client
        Client client = new Client();
        // ... set fields (email, password, etc.)

        // 3. Process address (reuse existing if possible)
        if (request.getAddress() != null) {
            Adresse address = processAddress(request.getAddress());
            client.setAdresse(address);
        }
        client.setMotDePasse(passwordEncoder.encode(request.getMotDePasse())); // Now works
        client.setActif(request.isActif());
        client.setNom(request.getNom());
        client.setTel(request.getTel());
        client.setType(request.getType());
        client.setProfil(request.getProfil());
        client.setEmail(request.getEmail());
        return clientRepository.save(client);
    }
    /**
     * Authenticate a client by email and password.
     */
   /* public Optional<Client> authenticateClient(String email, String password) {
        return clientRepository.findByEmailAndMotDePasse(email, password);
    }*/
    /**
     * Update a client profile.
     */
//    public Client updateClientProfile(Long clientId, ClientRegistrationRequest updatedClient) {
//        Optional<Client> clientOptional = clientRepository.findById(clientId);
//        if (clientOptional.isPresent()) {
//            Client client = clientOptional.get();
//            if (updatedClient.getAddress() != null) {
//                if(client.getAdresse() != null){
//                if(canDeleteAddress(client)){
//                    Ville VilleToRemove=client.getAdresse().getVille();
//                    Pays paysToRemove=client.getAdresse().getVille().getPays();
//                    Adresse AdresseToRemove = client.getAdresse();
//                    Adresse newAddress = processAddress(updatedClient.getAddress());
//                    client.setAdresse(newAddress);
//                   // if(adresseRepository.findAdressByVille(VilleToRemove.getId()).isEmpty() && villeRepository.findByPays(paysToRemove).isEmpty()){
//                            adresseRepository.delete(AdresseToRemove);
////                            paysRepository.delete(paysToRemove);
////                            villeRepository.delete(VilleToRemove);
//                    //}
//                }else{ Adresse newAddress = processAddress(updatedClient.getAddress());
//                    client.setAdresse(newAddress);}
//                }else{
//                    Adresse newAddress = processAddress(updatedClient.getAddress());
//                    client.setAdresse(newAddress);
//                }
//            }
//            // Update other fields
//            client.setMotDePasse(passwordEncoder.encode(updatedClient.getMotDePasse()));
//            client.setActif(updatedClient.isActif());
//            client.setNom(updatedClient.getNom());
//            client.setTel(updatedClient.getTel());
//            client.setType(updatedClient.getType());
//            client.setProfil(updatedClient.getProfil());
//            client.setEmail(updatedClient.getEmail());
//            return clientRepository.save(client);
//        }
//        throw new RuntimeException("Client not found");
//    }
    public ClientInfoAdmin updateClientProfile(Long clientId, ClientRegistrationRequest updatedClient) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        if (updatedClient.getAddress() != null) {
            handleAddressUpdate(client, updatedClient.getAddress());
        }
            client.setMotDePasse(passwordEncoder.encode(updatedClient.getMotDePasse()));
            client.setActif(updatedClient.isActif());
            client.setNom(updatedClient.getNom());
            client.setTel(updatedClient.getTel());
            if(updatedClient.getType().equals(TypeClient.Particulier) || updatedClient.getType().equals(TypeClient.Partenaire)){
                client.setType(updatedClient.getType());
            }
            client.setType(updatedClient.getType());  // Use the input value
            client.setProfil(updatedClient.getProfil());
            client.setEmail(updatedClient.getEmail());
            Client clientResult= clientRepository.save(client);
             return new ClientInfoAdmin(
                new ClientInfoResponse(
                        clientResult.getId(),
                        clientResult.getNom(),
                        clientResult.getEmail(),
                        clientResult.getTel(),
                        clientResult.getType() != null ? clientResult.getType().toString() : "N/A", // Handle null type
                        clientResult.getDescription(),
                        Optional.ofNullable(clientResult.getEntreprise())
                                .map(Entreprise::getNom)
                                .orElse("N/A"),
                        Optional.ofNullable(clientResult.getEntreprise())
                                .map(Entreprise::getMatriculeFiscale)
                                .orElse("N/A")
                ),
                new AddressResponse(clientResult.getAdresse()));
    }
    private void handleAddressUpdate(Client client, AddressResponse newAddressData) {
        if (client.getAdresse() != null) {
            Adresse oldAddress = client.getAdresse();
            Ville oldVille = oldAddress.getVille();
            Pays oldPays = oldVille.getPays();

            // Check if address can be deleted
            if (canDeleteAddress(client)) {
                // Clear client's address reference first
                client.setAdresse(null);
                clientRepository.save(client); // Flush changes

                // Now safe to delete
                adresseRepository.delete(oldAddress);

                // Cleanup ville and pays
                cleanUpVilleAndPays(oldVille, oldPays);
            }
        }

        // Process and set new address
        Adresse newAddress = processAddress(newAddressData);
        client.setAdresse(newAddress);
    }

    private void cleanUpVilleAndPays(Ville ville, Pays pays) {
        if (ville != null && adresseRepository.countByVille(ville) == 0) {
            villeRepository.delete(ville);
        }

        if (pays != null && villeRepository.countByPays(pays) == 0) {
            paysRepository.delete(pays);
        }
    }
    private boolean canDeleteAddress(Client client) {
        // Check if other clients or orders use the old address
        List<Client> otherClients = null;
        List<Commande> orders = null ;
        if(client.getAdresse() != null){
         otherClients = clientRepository.findClientsByAdresseId(
                client.getAdresse().getId(),
                client.getId()
        );
         orders = commandeRepository.findCommandesByAdresseId(
                    client.getAdresse().getId()
            );}
        return (otherClients == null || otherClients.isEmpty()) && (orders == null || orders.isEmpty());
    }

    /**
     * Reset the client's password.
     */
    public void resetPassword(Long clientId, String newPassword) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setMotDePasse(passwordEncoder.encode(newPassword)); // Secure password storage
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
                        produit.getPrix(),
                        produit.getCategories()
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
    @Transactional
    public void removeProductFromFavorites(Long clientId, Long productId) {
        if (!favorisRepository.existsByClientIdAndProduitId(clientId, productId)) {
            throw new ResourceNotFoundException("Favorite not found for client " + clientId + " and product " + productId);
        }
        favorisRepository.deleteByClientIdAndProduitId(clientId, productId);
    }
    public Commande createCommand(Long clientId, AddressResponse addressRequest, List<ProductRequest> productData) {
        // Verify client exists
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Process delivery address using shared logic
        Adresse deliveryAddress = processAddress(addressRequest);

        // Create command with proper enum handling
        Commande commande = new Commande();
        commande.setType(TypeCommande.Produit);
        commande.setClient(client);
        commande.setAdresseLivraison(deliveryAddress);
        commande.setDateCommande(new Date());
        commande.setEtat(EtatCommande.EnCoursDeTraitement);

        // Process command lines
        List<LigneCommande> ligneCommands = productData.stream()
                .map(product -> {
                    Produits produit = produitRepository.findById(product.getId_product())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + product.getId_product()));

                    LigneCommande lc = new LigneCommande();
                    lc.setCommande(commande);
                    lc.setProduit(produit);
                    lc.setQuantite(product.getQuantité());
                    return lc;
                })
                .toList();

        // Save command and lines
        commande.setLigneCommandes(ligneCommands);
        return commandeRepository.save(commande);
    }
    private Adresse processAddress(AddressResponse request) {
        // Normalize inputs
        String normalizedPays = request.getPays().trim().toLowerCase() ;
        String normalizedVille = request.getVille().trim().toLowerCase();
        String normalizedRue =   request.getRue().trim().toLowerCase() ;
        String normalizedNumero =  request.getNumero().trim().toLowerCase();
        String normalizedIndication =  request.getIndication().trim().toLowerCase() ;
        // Check existing address using DTO projection
        List<AddressResponse> existingAddresses = adresseRepository.findExistingAddress(
                normalizedRue,
                normalizedNumero,
                normalizedVille,
                normalizedPays,
                normalizedIndication
        );

        if (!existingAddresses.isEmpty()) {
            // Always return the first one consistently (or add logic to pick the most relevant)
            return adresseRepository.findById(existingAddresses.get(0).getId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));
        }



        // Create new address components with proper persistence
        List<Pays> existingPays = paysRepository.findByNomIgnoreCase(normalizedPays);
        Pays pays;
        if (!existingPays.isEmpty()) {
            pays = existingPays.get(0); // Take first existing Pays
        } else {
            pays = new Pays(normalizedPays);
            pays = paysRepository.save(pays);
        }

        // Process Ville
        List<Ville> existingVilles = villeRepository.findByNomIgnoreCaseAndPays(normalizedVille, pays);
        Ville ville;
        if (!existingVilles.isEmpty()) {
            ville = existingVilles.get(0); // Take first existing Ville
        } else {
            ville = new Ville(normalizedVille, pays);
            ville = villeRepository.save(ville);
        }
        Adresse newAdresse = new Adresse();
        newAdresse.setRue(normalizedRue);
        newAdresse.setNumero(normalizedNumero);
        newAdresse.setIndication(normalizedIndication);
        newAdresse.setVille(ville);
        return adresseRepository.save(newAdresse);
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
                        produit.getPrix(),
                        produit.getCategories()
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
    public Note addRating(RatingRequest request) {
        // Validate client and product exist
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        Produits product = produitRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check purchase validity
        if (!hasValidPurchase(client, product)) {
            throw new RatingNotAllowedException(
                    "Client hasn't purchased this product or order isn't in valid state"
            );
        }

        // Create new rating
        Note rating = new Note();
        rating.setClient(client);
        rating.setProduit(product);
        rating.setRating(request.getRating());
        rating.setCommentaire(request.getComment());

        return noteRepository.save(rating);
    }

    private boolean hasValidPurchase(Client client, Produits product) {
        // Check if client has any commandes that:
        // - Are in Livree or EnRetour state
        // - Contain the product in their ligneCommandes
        return commandeRepository.existsValidPurchaseForRating(
                client.getId(),
                product.getId(),
                List.of(EtatCommande.Livree, EtatCommande.EnRetour)
        );
    }
    public Note updateRating(Long clientId, Long productId, RatingUpdateRequest request) {
        // Find existing rating
        Note existingRating = noteRepository.findByClientIdAndProduitId(clientId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        // Re-validate purchase status (optional based on requirements)
        if (!hasValidPurchase(existingRating.getClient(), existingRating.getProduit())) {
            throw new RatingNotAllowedException(
                    "Rating can only be updated for products with valid purchase history"
            );
        }

        // Update fields
        existingRating.setRating(request.getRating());
        existingRating.setCommentaire(request.getComment());

        return noteRepository.save(existingRating);
    }
    public Note getRating(Long clientId, Long productId) {
        return noteRepository.findByClientIdAndProduitId(clientId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
    }
}
