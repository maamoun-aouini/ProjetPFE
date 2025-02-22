package com.example.OnlineSellingApplicationBackend.Controller;

import com.example.OnlineSellingApplicationBackend.DTO.ProduitAdminDTO;
import com.example.OnlineSellingApplicationBackend.Service.AdminService;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Associer un produit à une seule catégorie
    @PostMapping("/{productId}/addProdCategory/{categoryId}")
    public ResponseEntity<Boolean> addProductToCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
        boolean result = adminService.addProductToCategory(productId, categoryId);
        return ResponseEntity.ok(result);
    }

    // Remove a category from a product
    @DeleteMapping("/{productId}/removeCategory/{categoryId}")
    public ResponseEntity<Boolean> removeCategoryFromProduct(@PathVariable Long productId, @PathVariable Long categoryId) {
        boolean result = adminService.removeCategoryFromProduct(productId, categoryId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/products")
    public ResponseEntity<List<ProduitAdminDTO>> getAllProducts() {
        List<ProduitAdminDTO> products = adminService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    @PostMapping("/add")
    public Categories addCategory(@RequestBody Categories category, @RequestParam(required = false) Long parentId) {
        return adminService.addCategory(category, parentId);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
    }
    @PutMapping("/modifier/{id}")
    public ResponseEntity<Categories> modifierCategory(@PathVariable Long id, @RequestBody Categories new_category) {
        Categories updatedCategory = adminService.modiferCategory(id, new_category);
        return ResponseEntity.ok(updatedCategory); // 200 OK response with updated category
    }
    @GetMapping("/Category/{id}")
    public ResponseEntity<Categories> getCategory(@PathVariable long id) {
        Categories category = adminService.getCategory(id);
        return ResponseEntity.ok(category);
    }
    @GetMapping("/AllCategories")
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = adminService.getCategories();
        return ResponseEntity.ok(categories);
    }

}
