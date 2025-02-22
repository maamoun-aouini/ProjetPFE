package com.example.OnlineSellingApplicationBackend.Controller;

import com.example.OnlineSellingApplicationBackend.Service.ClientService;
import com.example.OnlineSellingApplicationBackend.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.OnlineSellingApplicationBackend.DTO.*;
import java.util.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * Register a new client account.
     */
    @PostMapping("/register")
    public ResponseEntity<Client> registerClient(@RequestBody Client client) {
        Client registeredClient = clientService.registerClient(client);
        return ResponseEntity.ok(registeredClient);
    }

    /**
     * Authenticate a client by email and password.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<Optional<Client>> authenticateClient(@RequestParam String email, @RequestParam String password) {
        Optional<Client> authenticatedClient = clientService.authenticateClient(email, password);
        return authenticatedClient.isPresent() ? ResponseEntity.ok(authenticatedClient) : ResponseEntity.status(401).build();
    }

    /**
     * Update a client profile.
     */
    @PutMapping("/{clientId}/update")
    public ResponseEntity<Client> updateClientProfile(@PathVariable Long clientId, @RequestBody Client updatedClient) {
        Client client = clientService.updateClientProfile(clientId, updatedClient);
        return ResponseEntity.ok(client);
    }

    /**
     * Reset the client's password.
     */
    @PutMapping("/{clientId}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long clientId, @RequestParam String newPassword) {
        clientService.resetPassword(clientId, newPassword);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all products.
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProduitDTO>> getAllProducts() {
        List<ProduitDTO> products = clientService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Add a product to the client's favorites.
     */
    @PostMapping("/{clientId}/favorites/{productId}")
    public ResponseEntity<Favoris> addProductToFavorites(@PathVariable Long clientId, @PathVariable Long productId) {
        Favoris favoris = clientService.addProductToFavorites(clientId, productId);
        return ResponseEntity.ok(favoris);
    }
    /**
     * Get all favorite products of a client.
     */
    @GetMapping("/{clientId}/favorites")
    public ResponseEntity<List<FavoriteProductDTO>> getClientFavorites(@PathVariable Long clientId) {
        List<FavoriteProductDTO> favoriteProducts = clientService.getClientFavorites(clientId);
        return ResponseEntity.ok(favoriteProducts);
    }




    /**
     * Create a command for a client.
     */
    @PostMapping("/{clientId}/commands")
    public ResponseEntity<Commande> createCommand(
            @PathVariable Long clientId,
            @RequestBody CommandRequest commandRequest) {
        Commande commande = clientService.createCommand(clientId, commandRequest.getAddress(), commandRequest.getProducts());
        return ResponseEntity.ok(commande);
    }
    /**
     * Get the order history of a client.
     */
    @GetMapping("/{clientId}/orders")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@PathVariable Long clientId) {
        List<Commande> orderHistory = clientService.getOrderHistory(clientId);
        List<OrderHistoryDTO> orderHistoryDTO = clientService.mapCommandeToDTO(orderHistory);
        return ResponseEntity.ok(orderHistoryDTO);
    }
    @GetMapping("/{id}/info")
    public ResponseEntity<Map<String, Object>> getClientInfo(@PathVariable Long id) {
        try {
            Map<String, Object> clientInfo = clientService.getClientInfo(id);
            return ResponseEntity.ok(clientInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/category/{categoryId}")
    public List<ProduitDTO> getProductsByCategoryAndSubcategories(@PathVariable Long categoryId) {
        return clientService.getProductsByCategoryWithSubcategories(categoryId);
    }
}
