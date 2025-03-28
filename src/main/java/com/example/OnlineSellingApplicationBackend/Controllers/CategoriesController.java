package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.ProduitCatDTO;
import com.example.OnlineSellingApplicationBackend.DTO.SubCategoryResponse;
import com.example.OnlineSellingApplicationBackend.DTO.CategoryResponse;
import com.example.OnlineSellingApplicationBackend.DTO.updateCategoryParent;
import com.example.OnlineSellingApplicationBackend.Services.CategoriesService;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @PostMapping
    public ResponseEntity<Categories> addCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        Categories category = new Categories();
        category.setNom(name);
        category.setDescription(description);
        if (images != null && images.length > 0) {
            Set<String> imagePaths = new HashSet<>();
            for (MultipartFile image : images) {
                String imagePath = saveImage(image);
                imagePaths.add(imagePath);
            }
            category.setPhoto(imagePaths);
        }

        Categories createdCategory = categoriesService.addCategory(category, parentId);
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        try {
            // Convertir explicitement parentId 0 en null
            Long processedParentId = (parentId != null && parentId == 0L) ? null : parentId;

            updateCategoryParent updatedCategory = new updateCategoryParent();
            updatedCategory.setName(name);
            updatedCategory.setDescription(description);
            updatedCategory.setParentId(processedParentId);

            Set<String> imagePaths = new HashSet<>();
            if (images != null) {
                for (MultipartFile image : images) {
                    String imagePath = saveImage(image);
                    imagePaths.add(imagePath);
                }
                updatedCategory.setPhoto(imagePaths);
            }

            Categories modifiedCategory = categoriesService.modiferCategory(id, updatedCategory);
            return ResponseEntity.ok(modifiedCategory);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Category or Parent not found: " + ex.getMessage());
        }
    }
    // Update the controller to remove deleteProducts parameter
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean deleteSubCategories) {

        try {
            categoriesService.deleteCategory(id, deleteSubCategories);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("Error deleting category: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categories> getCategory(@PathVariable Long id) {
        Categories category = categoriesService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Categories> categories = categoriesService.getCategoriesWithSubCategories();
        List<CategoryResponse> response = categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private SubCategoryResponse convertToSubResponse(Categories category) {
        return new SubCategoryResponse(
                category.getId(),
                category.getNom(),
                category.getDescription(),
                new ArrayList<>(category.getPhoto()),
                category.getSubCategories().stream()
                        .map(this::convertToSubResponse)
                        .collect(Collectors.toList())
        );
    }

    private CategoryResponse convertToResponse(Categories category) {
        return new CategoryResponse(
                category.getId(),
                category.getNom(),
                category.getDescription(),
                new ArrayList<>(category.getPhoto()),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getSubCategories().stream()
                        .map(this::convertToSubResponse)
                        .collect(Collectors.toList())
        );
    }

    private String saveImage(MultipartFile image) {
        try {
            String uploadDir = "uploads/categories";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath);

            return uploadDir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }
    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<String> removeProductFromCategory(
            @PathVariable Long categoryId,
            @PathVariable Long productId
    ) {
        try {
            categoriesService.removeProductFromCategory(categoryId, productId);
            return ResponseEntity.ok("Product successfully removed from category");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove product from category: " + e.getMessage());
        }
    }
    @GetMapping("/products/{categoryId}")
    public ResponseEntity<List<ProduitCatDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProduitCatDTO> products = categoriesService.findProductsByCategoryId(categoryId);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si aucun produit trouvé
        }

        return ResponseEntity.ok(products);
    }

}