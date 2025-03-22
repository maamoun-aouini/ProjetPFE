package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.updateCategoryParent;
import com.example.OnlineSellingApplicationBackend.Repositories.CategoriesRepository;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

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

    public Categories modiferCategory(Long id, updateCategoryParent newCategory) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Update name and description if provided
        if (newCategory.getName() != null) {
            category.setNom(newCategory.getName());
        }
        if (newCategory.getDescription() != null) {
            category.setDescription(newCategory.getDescription());
        }

        // Update parent if provided
        if (newCategory.getParentId() != null) {
            Categories parentCategory = categoriesRepository.findById(newCategory.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent Category not found"));
            category.setParent(parentCategory);
        }

        // Update images if provided
        if (newCategory.getPhoto() != null) {
            category.setPhoto(newCategory.getPhoto());
        }

        return categoriesRepository.save(category);
    }
    public Categories getCategory(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public List<Categories> getCategories() {
        List<Categories> categories = categoriesRepository.findByParentIsNull();
        if (categories.isEmpty()) {
            throw new RuntimeException("Categories is empty");
        }
        return categories;
    }
}