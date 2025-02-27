package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.Repositories.CategoriesRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ProduitsRepository;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Note;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class ProductService {
    @Autowired
    private ProduitsRepository produitRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProduitsRepository produitsRepository;

    // Add categories to a product
    public List<ProduitAdminDTO> getAllProducts() {
        return produitRepository.findAllWithRatings() // Custom repository method
                .stream()
                .map(produit -> {
                    Double avgRating = calculateAverageRating(produit.getNotes());
                    return new ProduitAdminDTO(
                            produit.getId(),
                            produit.getNom(),
                            produit.getDescription(),
                            produit.getPhoto(),
                            produit.getQuantite(),
                            produit.getPrix(),
                            produit.getPromotionPartenaire(),
                            produit.getPromotionParticulier(),
                            produit.getCategories(),
                            avgRating
                    );
                })
                .collect(Collectors.toList());
    }

    private Double calculateAverageRating(Set<Note> notes) {
        if (notes == null || notes.isEmpty()) {
            return null;
        }
        return notes.stream()
                .mapToInt(Note::getRating)
                .average()
                .orElse(0.0);
    }

    public ProductUpdateResponse updateProductCategories(Long productId, ProductUpdateRequest request) {
        ProductUpdateResponse response = new ProductUpdateResponse();
        response.setProductId(productId);

        try {
            // Validate input
            Set<Long> incomingIds = Optional.ofNullable(request.getCategoryIds())
                    .orElseGet(HashSet::new);

            if (incomingIds.isEmpty()) {
                response.setError("No categories provided for update");
                return response;
            }

            Produits product = produitRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Batch load categories
            List<Categories> categories = categoriesRepository.findAllById(incomingIds);
            if (categories.size() != incomingIds.size()) {
                Set<Long> foundIds = categories.stream()
                        .map(Categories::getId)
                        .collect(Collectors.toSet());
                incomingIds.removeAll(foundIds);
                throw new ResourceNotFoundException("Missing categories: " + incomingIds);
            }

            // Build ancestor map for each category
            Map<Long, Set<Long>> categoryAncestors = new HashMap<>();
            for (Categories category : categories) {
                Set<Long> ancestors = new HashSet<>();
                Categories current = category;
                while (current != null) {
                    ancestors.add(current.getId());
                    current = current.getParent();
                }
                categoryAncestors.put(category.getId(), ancestors);
            }

            // Determine categories to exclude (those that are ancestors of others in the list)
            Set<Long> excludedCategoryIds = new HashSet<>();
            for (Long categoryId : incomingIds) {
                for (Long otherCategoryId : incomingIds) {
                    if (categoryId.equals(otherCategoryId)) continue;
                    Set<Long> otherAncestors = categoryAncestors.get(otherCategoryId);
                    if (otherAncestors.contains(categoryId)) {
                        excludedCategoryIds.add(categoryId);
                        break;
                    }
                }
            }

            // Final category IDs are those not excluded
            Set<Long> finalCategoryIds = incomingIds.stream()
                    .filter(id -> !excludedCategoryIds.contains(id))
                    .collect(Collectors.toSet());

            // Retrieve the final categories (already loaded)
            Set<Categories> finalCategories = categories.stream()
                    .filter(c -> finalCategoryIds.contains(c.getId()))
                    .collect(Collectors.toSet());

            // Update product associations
            product.setCategories(finalCategories);
            produitRepository.save(product);

            // Populate response
            for (Categories category : finalCategories) {
                response.getAddedCategories().add(
                        new CategoryMessage(category.getId(), category.getNom())
                );
            }

            // Populate skipped categories
            for (Long excludedId : excludedCategoryIds) {
                Categories excludedCategory = categories.stream()
                        .filter(c -> c.getId().equals(excludedId))
                        .findFirst()
                        .orElse(null);
                if (excludedCategory != null) {
                    response.getSkippedCategories().add(
                            new CategoryMessage(
                                    excludedCategory.getId(),
                                    excludedCategory.getNom(),
                                    "Category skipped because it is an ancestor of another category in the request"
                            )
                    );
                }
            }

        } catch (ResourceNotFoundException e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    public Produits createProductWithCategories(ProductCreateRequest request) {
        // Validate input categories
        Set<Long> incomingCategoryIds = Optional.ofNullable(request.getCategoryIds())
                .orElse(Collections.emptySet());

        // Create and populate product
        Produits product = new Produits();
        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPromotionPartenaire(request.getPromotionPartenaire());
        product.setPromotionParticulier(request.getPromotionParticulier());
        product.setSelection(request.getSelection());
        product.setPhoto(request.getPhoto());
        product.setQuantite(request.getQuantite());
        product.setPrix(request.getPrix());
        product.setDisponibilite(request.isDisponibilite());

        // Process categories if provided
        if (!incomingCategoryIds.isEmpty()) {
            List<Categories> categories = categoriesRepository.findAllById(incomingCategoryIds);

            // Validate all categories exist
            if (categories.size() != incomingCategoryIds.size()) {
                Set<Long> foundIds = categories.stream()
                        .map(Categories::getId)
                        .collect(Collectors.toSet());
                Set<Long> missingIds = incomingCategoryIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .collect(Collectors.toSet());
                throw new ResourceNotFoundException("Categories not found: " + missingIds);
            }

            // Build ancestor map for each category
            Map<Long, Set<Long>> categoryAncestors = new HashMap<>();
            for (Categories category : categories) {
                Set<Long> ancestors = new HashSet<>();
                Categories current = category;
                while (current != null) {
                    ancestors.add(current.getId());
                    current = current.getParent();
                }
                categoryAncestors.put(category.getId(), ancestors);
            }

            // Determine categories to exclude (ancestors of others in the list)
            Set<Long> excludedCategoryIds = new HashSet<>();
            for (Long categoryId : incomingCategoryIds) {
                for (Long otherCategoryId : incomingCategoryIds) {
                    if (categoryId.equals(otherCategoryId)) continue;
                    Set<Long> otherAncestors = categoryAncestors.get(otherCategoryId);
                    if (otherAncestors.contains(categoryId)) {
                        excludedCategoryIds.add(categoryId);
                        break;
                    }
                }
            }

            // Final category IDs
            Set<Long> finalCategoryIds = incomingCategoryIds.stream()
                    .filter(id -> !excludedCategoryIds.contains(id))
                    .collect(Collectors.toSet());

            Set<Categories> finalCategories = categories.stream()
                    .filter(c -> finalCategoryIds.contains(c.getId()))
                    .collect(Collectors.toSet());

            product.setCategories(finalCategories);
        }

        // Save and return
        return produitRepository.save(product);
    }

}
