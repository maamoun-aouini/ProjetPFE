package com.example.OnlineSellingApplicationBackend.Controllers;
import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.Services.ProductService;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/Products")
public class ProductController {
    @Autowired
    private ProductService productService;
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProduitAdminDTO>> getAllProducts() {
        List<ProduitAdminDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductAndItsCategories(
            @PathVariable Long id,
            @RequestBody ProductCreateUpdateRequest request
    ){
        try{
            ProductUpdateCategoriesResponse response = productService.updateProductAndItsCategories(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "Category you want to add as parent is not found" + ex.getMessage());
        }
    }
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateUpdateRequest request) {
        try {
            Produits createdProduct = productService.createProductWithCategories(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + ex.getMessage());
        }
    }
    @GetMapping("/distribution")
    public ResponseEntity<List<CategoryDistributionDTO>> getCategoryDistribution() {
        List<CategoryDistributionDTO> distribution = productService.getCategoryDistribution();
        return ResponseEntity.ok(distribution);
    }
    @GetMapping("/stats")
    public ResponseEntity<ProductStatsDTO> getProductStats() {
        ProductStatsDTO stats = productService.getProductStats();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/sales")
    public ResponseEntity<List<SalesDataDTO>> getSalesData(
            @RequestParam String range,
            @RequestParam(required = false) String timezone) {
        return ResponseEntity.ok(productService.getSalesData(range));
    }
}

