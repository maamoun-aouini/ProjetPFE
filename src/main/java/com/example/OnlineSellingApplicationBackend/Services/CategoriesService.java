package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.CategoryResponse;
import com.example.OnlineSellingApplicationBackend.DTO.updateCategoryParent;
import com.example.OnlineSellingApplicationBackend.Repositories.CategoriesRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ProduitsRepository;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProduitsRepository produitsRepository;

    public Categories addCategory(Categories category, Long parentId) {
        if (parentId != null) {
            Categories parent = categoriesRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParent(parent);
            parent.getSubCategories().add(category);
        }
        return categoriesRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id, boolean deleteProducts, boolean removeFromProducts) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // First, remove all references from produit_categorie table
        Set<Produits> products = category.getProduits();

        // Remove category from all products (this will handle the produit_categorie entries)
        for (Produits product : products) {
            product.getCategories().remove(category);
            produitsRepository.save(product);
        }

        // Then handle the products based on flags
        if (!products.isEmpty()) {
            if (deleteProducts) {
                // Delete the products
                produitsRepository.deleteAll(products);
            } else if (!removeFromProducts) {
                // If neither flag is true and there are products, throw exception
                throw new RuntimeException("Category has products. Please specify action (deleteProducts or removeFromProducts)");
            }
            // If removeFromProducts is true, we've already removed the category from products
        }

        // Handle subcategories
        if (!category.getSubCategories().isEmpty()) {
            if (category.getParent() != null) {
                // Move subcategories to parent category
                for (Categories subCategory : category.getSubCategories()) {
                    subCategory.setParent(category.getParent());
                    categoriesRepository.save(subCategory);
                }
            } else {
                throw new RuntimeException("Cannot delete category with subcategories");
            }
        }

        // Finally delete the category
        categoriesRepository.delete(category);
    }
    public Categories modiferCategory(Long id, updateCategoryParent newCategory) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (newCategory.getName() != null) {
            category.setNom(newCategory.getName());
        }
        if (newCategory.getDescription() != null) {
            category.setDescription(newCategory.getDescription());
        }

        if (newCategory.getParentId() != null) {
            Categories parentCategory = categoriesRepository.findById(newCategory.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent Category not found"));
            category.setParent(parentCategory);
        }

        if (newCategory.getPhoto() != null) {
            category.setPhoto(newCategory.getPhoto());
        }

        return categoriesRepository.save(category);
    }

    public Categories getCategory(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public List<Categories> getCategoriesWithSubCategories() {
        return categoriesRepository.findAllRootCategoriesWithSubs();
    }
}