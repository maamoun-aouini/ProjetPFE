package com.example.OnlineSellingApplicationBackend.Services;
import com.example.OnlineSellingApplicationBackend.DTO.updateCategoryParent;
import com.example.OnlineSellingApplicationBackend.Repositories.CategoriesRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ProduitsRepository;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CategoriesService {

    @Autowired
    private ProduitsRepository produitRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProduitsRepository produitsRepository;

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

    public Categories modiferCategory(Long id , updateCategoryParent new_category) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Categories parentCategory = null;
        Long ID=new_category.getParentId();
        if(ID!=null){
         parentCategory = categoriesRepository.findById(new_category.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent Category not found"));}
        category.setDescription(new_category.getDescription());
        category.setNom(new_category.getName());
        category.setParent(parentCategory);
        return categoriesRepository.save(category);
    }
    public Categories getCategory(Long id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return category;
    }
    public List<Categories> getCategories() {
        List<Categories> categories = categoriesRepository.findByParentIsNull();
        if(categories.isEmpty()){
            new RuntimeException("Categories is empty");
        }
        return categories;
    }

}
