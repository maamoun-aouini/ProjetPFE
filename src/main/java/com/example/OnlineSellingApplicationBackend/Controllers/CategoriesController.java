package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.updateCategoryParent;
import com.example.OnlineSellingApplicationBackend.Services.CategoriesService;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    /**
     * Add a new category. If parentId is provided, the category will be a subcategory.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @PostMapping
    public ResponseEntity<Categories> addCategory(
            @RequestBody Categories category,
            @RequestParam(required = false) Long parentId) {
        Categories createdCategory = categoriesService.addCategory(category, parentId);
        return ResponseEntity.ok(createdCategory);
    }

    /**
     * Delete a category by its ID.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204 No Content response
    }

    /**
     * Update a category by its ID.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody updateCategoryParent updatedCategory) {
        try {
            Categories modifiedCategory = categoriesService.modiferCategory(id, updatedCategory);
            return ResponseEntity.ok(modifiedCategory);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "Category or Parent not found" + ex.getMessage());
        }
    }

    /**
     * Get a single category by its ID.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Categories> getCategory(@PathVariable Long id) {
        Categories category = categoriesService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Get all categories.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = categoriesService.getCategories();
        return ResponseEntity.ok(categories);
    }
}
