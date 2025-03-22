package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.updateCategoryParent;
import com.example.OnlineSellingApplicationBackend.Services.CategoriesService;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    /**
     * Add a new category. If parentId is provided, the category will be a subcategory.
     */
    @PostMapping
    public ResponseEntity<Categories> addCategory(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        Categories category = new Categories();
        category.setNom(name);
        category.setDescription(description);
        if (images != null && images.length > 0) {
            Set<String> imagePaths = new HashSet<>();
            for (MultipartFile image : images) {
                String imagePath = saveImage(image); // Save each image and get its path
                imagePaths.add(imagePath);
            }
            category.setPhoto(imagePaths); // Set the paths in the category
        }

        Categories createdCategory = categoriesService.addCategory(category, parentId);
        return ResponseEntity.ok(createdCategory);
    }

    /**
     * Update a category by its ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "images", required = false) MultipartFile image) {
        try {
            // Create a DTO to hold the updated data
            updateCategoryParent updatedCategory = new updateCategoryParent();
            updatedCategory.setName(name);
            updatedCategory.setDescription(description);
            updatedCategory.setParentId(parentId);

            // Handle image uploads
            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image);
                updatedCategory.setPhoto(Set.of(imagePath)); // Set as a collection
            }

            Categories modifiedCategory = categoriesService.modiferCategory(id, updatedCategory);
            return ResponseEntity.ok(modifiedCategory);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category or Parent not found: " + ex.getMessage());
        }
    }
    /**
     * Delete a category by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204 No Content response
    }

    /**
     * Get a single category by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Categories> getCategory(@PathVariable Long id) {
        Categories category = categoriesService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Get all categories.
     */
    @GetMapping
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = categoriesService.getCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Helper method to save an image and return its path.
     */
    private String saveImage(MultipartFile image) {
        try {
            // Define the directory to save images
            String uploadDir = "uploads/categories";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // Create the directory if it doesn't exist
            }

            // Generate a unique filename
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

            // Save the file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath);

            // Return the file path or URL
            return uploadDir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }
}