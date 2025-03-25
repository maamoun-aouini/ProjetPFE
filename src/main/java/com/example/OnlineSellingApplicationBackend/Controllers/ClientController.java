package com.example.OnlineSellingApplicationBackend.Controllers;
import org.springframework.security.core.GrantedAuthority;

import com.example.OnlineSellingApplicationBackend.Exeptions.RatingNotAllowedException;
import com.example.OnlineSellingApplicationBackend.Security.CustomUserDetails;
import com.example.OnlineSellingApplicationBackend.Security.JwtUtils;
import com.example.OnlineSellingApplicationBackend.Security.UserDetailsServiceImpl;
import com.example.OnlineSellingApplicationBackend.Services.ClientService;
import com.example.OnlineSellingApplicationBackend.entities.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.OnlineSellingApplicationBackend.DTO.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private  UserDetailsServiceImpl userDetailsService;
    /**
     * Register a new client account.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ClientInfoAdmin>> getAllClients(){
        return ResponseEntity.ok(clientService.getAllClients());
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerClient(@RequestBody ClientRegistrationRequest request) {
        try {
            Client client = clientService.registerClient(request);
            return ResponseEntity.ok(client);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", Instant.now()
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "Duplicate address components detected",
                    "resolution", "Automatic deduplication in progress",
                    "timestamp", Instant.now()
            ));
        }
    }
    /**
     * Authenticate a client (Moved to /api/auth).
     */

    /**
     * Get client information.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER' , 'SUPERADMIN' , 'ADMIN')")
    @GetMapping("/{clientId}")
    public ResponseEntity<Map<String, Object>> getClientInfo(@PathVariable Long clientId) {
        try {
            Map<String, Object> clientInfo = clientService.getClientInfo(clientId);
            return ResponseEntity.ok(clientInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update client profile.
     */
    @PutMapping("/{clientId}")
    public ResponseEntity<?> updateClientProfile(
            @PathVariable Long clientId,
            @RequestBody ClientRegistrationRequest updatedClient,
            HttpServletRequest request
    ) {
        // Validate the request body
        if (updatedClient == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body cannot be null"));
        }
        // Validate the client type
        if (updatedClient.getType() == null ||
                (!updatedClient.getType().equals(TypeClient.Particulier) &&
                        !updatedClient.getType().equals(TypeClient.Partenaire))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid client type"));
        }

        // Get the old email for token generation
        String oldEmail = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        try {
            // Update the client profile
            ClientInfoAdmin client = clientService.updateClientProfile(clientId, updatedClient);

            // Generate a new token with updated details
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(client.getClientInfoResponse().getEmail());
            String newToken = jwtUtils.generateToken(
                    client.getClientInfoResponse().getEmail(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()),
                    userDetails.getUserId()
            );

            // Return the updated client and new token
            return ResponseEntity.ok(Map.of(
                    "client", client,
                    "token", newToken
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    /**
     * Reset password.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER' , 'SUPERADMIN' , 'ADMIN')")
    @PutMapping("/{clientId}/password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long clientId, @RequestParam String newPassword) {
        clientService.resetPassword(clientId, newPassword);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all products (Moved to /api/products).
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @GetMapping("/products")
    public ResponseEntity<List<ProduitDTO>> getAllProducts() {
        List<ProduitDTO> products = clientService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    /**
     * Get products by category.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER' , 'SUPERADMIN' , 'ADMIN')")
    @GetMapping("/products/categories/{categoryId}")
    public List<ProduitDTO> getProductsByCategoryAndSubcategories(@PathVariable Long categoryId) {
        return clientService.getProductsByCategoryWithSubcategories(categoryId);
    }
    /**
     * Add a product to favorites.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @PostMapping("/{clientId}/favorites/{productId}")
    public ResponseEntity<Favoris> addProductToFavorites(@PathVariable Long clientId, @PathVariable Long productId) {
        Favoris favoris = clientService.addProductToFavorites(clientId, productId);
        return ResponseEntity.ok(favoris);
    }

    /**
     * Get favorite products of a client.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @GetMapping("/{clientId}/favorites")
    public ResponseEntity<List<FavoriteProductDTO>> getClientFavorites(@PathVariable Long clientId) {
        List<FavoriteProductDTO> favoriteProducts = clientService.getClientFavorites(clientId);
        return ResponseEntity.ok(favoriteProducts);
    }
    /**
     * Remove a product from favorites.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @DeleteMapping("/{clientId}/favorites/{productId}")
    public ResponseEntity<?> removeProductFromFavorites(@PathVariable Long clientId, @PathVariable Long productId) {
        try {
            clientService.removeProductFromFavorites(clientId, productId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



    /**
     * Add a product rating.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @PostMapping("/{clientId}/ratings")
    public ResponseEntity<?> addRating(@PathVariable Long clientId, @RequestBody RatingRequest request) {
        try {
            Note createdRating = clientService.addRating(request);
            return ResponseEntity.ok(createdRating);
        } catch (RatingNotAllowedException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update a product rating.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @PutMapping("/{clientId}/ratings/{productId}")
    public ResponseEntity<?> updateRating(
            @PathVariable Long clientId,
            @PathVariable Long productId,
            @RequestBody RatingUpdateRequest request) {
        try {
            Note updatedRating = clientService.updateRating(clientId, productId, request);
            return ResponseEntity.ok(updatedRating);
        } catch (RatingNotAllowedException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/rating/{clientId}/{productId}")
    public ResponseEntity<?> getRating(@PathVariable Long clientId, @PathVariable Long productId) {
        Note note = clientService.getRating(clientId, productId);
        return ResponseEntity.ok(note);
    }
    // UserController.java
    @GetMapping("/stats/growth")
    public ResponseEntity<List<UserGrowthDTO>> getUserGrowth() {
        return ResponseEntity.ok(clientService.getUserGrowthData());
    }
    // UserController.java
    @GetMapping("stats/client-types")
    public ResponseEntity<List<ClientTypeDTO>> getClientTypeDistribution() {
        return ResponseEntity.ok(clientService.getClientTypeDistribution());
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUsersStats() {
        return ResponseEntity.ok(clientService.getUsersStats());
    }
}
