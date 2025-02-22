package com.example.OnlineSellingApplicationBackend.Service;
import com.example.OnlineSellingApplicationBackend.DAO.CategoriesRepository;
import com.example.OnlineSellingApplicationBackend.DAO.ProduitsRepository;
import com.example.OnlineSellingApplicationBackend.DTO.ProduitAdminDTO;
import com.example.OnlineSellingApplicationBackend.DTO.ProduitDTO;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private ProduitsRepository produitRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProduitsRepository produitsRepository;

    // Add categories to a product
    public boolean addProductToCategory(Long productId, Long categoryId) {
        Optional<Produits> produitOpt = produitsRepository.findById(productId);
        Optional<Categories> categoryOpt = categoriesRepository.findById(categoryId);

        if (produitOpt.isEmpty() || categoryOpt.isEmpty()) {
            throw new RuntimeException("Produit ou catégorie non trouvé");
        }

        Produits produit = produitOpt.get();
        Categories category = categoryOpt.get();

        produit.getCategories().add(category);
        produitsRepository.save(produit);
        return true;
    }

    // Remove category from a product
    public boolean removeCategoryFromProduct(Long productId, Long categoryId) {
        Optional<Produits> produitOpt = produitsRepository.findById(productId);
        if (produitOpt.isPresent()) {
            Produits produit = produitOpt.get();
            Set<Categories> categories = produit.getCategories();
            categories.removeIf(category -> category.getId().equals(categoryId)); // Remove the category
            produit.setCategories(categories); // Update the product categories
            produitsRepository.save(produit); // Save the updated product
            return true;
        }
        return false;
    }

    public List<ProduitAdminDTO> getAllProducts() {
        return produitRepository.findAll()
                .stream()
                .map(produit -> new ProduitAdminDTO(
                        produit.getId(),
                        produit.getNom(),
                        produit.getDescription(),
                        produit.getPhoto(),
                        produit.getQuantite(),
                        produit.getPrix(),
                        produit.getPromotionPartenaire(),
                        produit.getPromotionParticulier(),
                        produit.getCategories()
                ))
                .collect(Collectors.toList());
    }
    public Categories addCategory(Categories category, Long parentId) {
        if (parentId != null) {
            Categories parent = categoriesRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParent(parent);
            parent.getSubCategories().add(category); // Bidirectional sync
        }
        return categoriesRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Prevent deletion if subcategories exist (adjust based on requirements)
        if (!category.getSubCategories().isEmpty()) {
            throw new RuntimeException("Delete subcategories first");
        }
        categoriesRepository.delete(category);
    }


    public Categories modiferCategory(Long id , Categories new_category) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setDescription(new_category.getDescription());
        category.setNom(new_category.getNom());
        return categoriesRepository.save(category);
    }
    public Categories getCategory(Long id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return category;
    }
    public List<Categories> getCategories() {
        List<Categories> categories = categoriesRepository.findAll();
        if(categories.isEmpty()){
            new RuntimeException("Categories is empty");
        }
        return categories;
    }



}
