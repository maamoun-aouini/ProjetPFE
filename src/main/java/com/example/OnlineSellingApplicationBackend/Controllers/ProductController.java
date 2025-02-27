package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.ProductCreateRequest;
import com.example.OnlineSellingApplicationBackend.DTO.ProductUpdateRequest;
import com.example.OnlineSellingApplicationBackend.DTO.ProductUpdateResponse;
import com.example.OnlineSellingApplicationBackend.DTO.ProduitAdminDTO;
import com.example.OnlineSellingApplicationBackend.Services.ProductService;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/Products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping()
    public ResponseEntity<List<ProduitAdminDTO>> getAllProducts() {
        List<ProduitAdminDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductUpdateResponse> updateProductCategories(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest request
    ) {
        ProductUpdateResponse response = productService.updateProductCategories(id, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest request) {
        try {
            Produits createdProduct = productService.createProductWithCategories(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + ex.getMessage());
        }
    }
}
