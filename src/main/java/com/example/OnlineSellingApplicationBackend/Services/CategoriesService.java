package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.CategoryResponse;
import com.example.OnlineSellingApplicationBackend.DTO.updateCategoryParent;
import com.example.OnlineSellingApplicationBackend.DTO.ProduitCatDTO;

import com.example.OnlineSellingApplicationBackend.Repositories.CategoriesRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ProduitsRepository;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import com.example.OnlineSellingApplicationBackend.entities.Produits;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;
    @PersistenceContext
    private EntityManager entityManager;
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
    public void deleteCategory(Long id, boolean deleteSubCategories) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        removeCategoryAssociationsAndPhotos(category.getId());
        if (deleteSubCategories) {
            deleteSubcategoriesRecursively(category);
        } else {
            // Réassignation explicite des sous-catégories
            Categories newParent = category.getParent();
            List<Categories> subCategories = new ArrayList<>(category.getSubCategories());

            for (Categories sub : subCategories) {
                sub.setParent(newParent);
                categoriesRepository.save(sub);
            }
            category.getSubCategories().clear(); // Vider la liste avant suppression
            categoriesRepository.save(category);
        }

        // Suppression finale de la catégorie
        categoriesRepository.delete(category);

    }

    private void reassignSubcategories(Categories category) {
        Categories parentCategory = category.getParent();
        List<Categories> subCategories = new ArrayList<>(category.getSubCategories());

        for (Categories subCategory : subCategories) {
            subCategory.setParent(parentCategory);
            categoriesRepository.save(subCategory);
            category.getSubCategories().remove(subCategory); // Retirer de la liste
        }

        categoriesRepository.save(category); // Sauvegarder les changements
        entityManager.flush();
    }

    private void deleteSubcategoriesRecursively(Categories category) {
        // Create a copy to avoid concurrent modification
        List<Categories> subCategories = new ArrayList<>(category.getSubCategories());

        for (Categories subCategory : subCategories) {
            // Recursively delete nested subcategories
            deleteSubcategoriesRecursively(subCategory);

            // Remove associations
            removeCategoryAssociationsAndPhotos(subCategory.getId());

            // Delete subcategory
            categoriesRepository.delete(subCategory);
        }
    }

    private void removeCategoryAssociationsAndPhotos(Long categoryId) {
        // Remove product associations
        entityManager.createNativeQuery("DELETE FROM produit_categorie WHERE categorie_id = :categoryId")
                .setParameter("categoryId", categoryId)
                .executeUpdate();

        // Remove photos
        entityManager.createNativeQuery("DELETE FROM category_photos WHERE category_id = :categoryId")
                .setParameter("categoryId", categoryId)
                .executeUpdate();
    }


    public Categories modiferCategory(Long id, updateCategoryParent newCategory) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Mise à jour conditionnelle du nom
        if (newCategory.getName() != null) {
            category.setNom(newCategory.getName());
        }

        // Mise à jour conditionnelle de la description
        if (newCategory.getDescription() != null) {
            category.setDescription(newCategory.getDescription());
        }

        // Gestion explicite du parent
        if (newCategory.getParentId() != null) {
            Categories parentCategory = categoriesRepository.findById(newCategory.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent Category not found"));
            category.setParent(parentCategory);
        } else {
            category.setParent(null); // Suppression du parent existant
        }

        // Mise à jour des images
        if (newCategory.getPhoto() != null && !newCategory.getPhoto().isEmpty()) {
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
    public List<ProduitCatDTO> findProductsByCategoryId(Long categoryId) {
        // Get all category IDs including subcategories
        Set<Long> allCategoryIds = new HashSet<>();
        allCategoryIds.add(categoryId); // Add the parent category ID
        collectSubcategoryIds(categoryId, allCategoryIds);

        // Get products from all categories (parent and subcategories)
        List<Produits> produits = produitsRepository.findByCategoriesIdIn(allCategoryIds);

        return produits.stream()
                .map(ProduitCatDTO::new)
                .collect(Collectors.toList());
    }

    private void collectSubcategoryIds(Long categoryId, Set<Long> categoryIds) {
        Categories category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Recursively collect subcategory IDs
        for (Categories subCategory : category.getSubCategories()) {
            categoryIds.add(subCategory.getId());
            collectSubcategoryIds(subCategory.getId(), categoryIds);
        }
    }
    @Transactional
    public void removeProductFromCategory(Long categoryId, Long productId) {
        produitsRepository.removeProductFromCategory(categoryId, productId);

        // Clear persistence context to reflect changes immediately
        entityManager.flush();
        entityManager.clear();
    }

}
