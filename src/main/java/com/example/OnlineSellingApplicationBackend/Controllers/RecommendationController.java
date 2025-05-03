package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.ProductRecommendationDTO;
import com.example.OnlineSellingApplicationBackend.Services.AdvancedRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advanced-recommendations")
public class RecommendationController {

    @Autowired
    private AdvancedRecommendationService recommendationService;
    /**
     * Get collaborative filtering recommendations
     */
    @GetMapping("/collaborative/{clientId}")
    public ResponseEntity<List<ProductRecommendationDTO>> getCollaborativeRecommendations(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "10") int limit) {

        List<ProductRecommendationDTO> recommendations =
                recommendationService.getCollaborativeRecommendations(clientId, limit);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get content-based recommendations
     */
    @GetMapping("/content-based/{clientId}")
    public ResponseEntity<List<ProductRecommendationDTO>> getContentBasedRecommendations(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "10") int limit) {

        List<ProductRecommendationDTO> recommendations =
                recommendationService.getContentBasedRecommendations(clientId, limit);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get hybrid recommendations (combines multiple techniques)
     */
    @GetMapping("/hybrid/{clientId}")
    public ResponseEntity<List<ProductRecommendationDTO>> getHybridRecommendations(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "10") int limit) {

        List<ProductRecommendationDTO> recommendations =
                recommendationService.getHybridRecommendations(clientId, limit);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get popular product recommendations
     */
    @GetMapping("/popular")
    public ResponseEntity<List<ProductRecommendationDTO>> getPopularRecommendations(
            @RequestParam(defaultValue = "10") int limit) {

        List<ProductRecommendationDTO> recommendations =
                recommendationService.getPopularRecommendations(limit);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get similar product recommendations
     */
    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<ProductRecommendationDTO>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {

        List<ProductRecommendationDTO> recommendations =
                recommendationService.getSimilarProducts(productId, limit);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Track product view
     */
    @PostMapping("/track/view/{clientId}/{productId}")
    public ResponseEntity<Void> trackProductView(
            @PathVariable Long clientId,
            @PathVariable Long productId) {

        recommendationService.trackProductView(clientId, productId);
        return ResponseEntity.ok().build();
    }

    /**
     * Track add to cart
     */
    @PostMapping("/track/cart/{clientId}/{productId}")
    public ResponseEntity<Void> trackAddToCart(
            @PathVariable Long clientId,
            @PathVariable Long productId) {

        recommendationService.trackAddToCart(clientId, productId);
        return ResponseEntity.ok().build();
    }

    /**
     * Track purchase
     */
    @PostMapping("/track/purchase/{clientId}/{productId}")
    public ResponseEntity<Void> trackPurchase(
            @PathVariable Long clientId,
            @PathVariable Long productId) {

        recommendationService.trackPurchase(clientId, productId);
        return ResponseEntity.ok().build();
    }

    /**
     * Track wishlist addition
     */
    @PostMapping("/track/wishlist/{clientId}/{productId}")
    public ResponseEntity<Void> trackWishlist(
            @PathVariable Long clientId,
            @PathVariable Long productId) {

        recommendationService.trackWishlist(clientId, productId);
        return ResponseEntity.ok().build();
    }
}