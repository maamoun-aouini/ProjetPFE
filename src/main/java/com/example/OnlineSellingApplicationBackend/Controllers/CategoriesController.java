package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.Services.CategoriesService;
import com.example.OnlineSellingApplicationBackend.entities.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {
    @Autowired
    private CategoriesService categoriesService;
    @PostMapping
    public Categories addCategory(@RequestBody Categories category, @RequestParam(required = false) Long parentId) {
        return categoriesService.addCategory(category, parentId);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoriesService.deleteCategory(id);
    }
    @PutMapping("/modifier/{id}")
    public ResponseEntity<Categories> modifierCategory(@PathVariable Long id, @RequestBody Categories new_category) {
        Categories updatedCategory = categoriesService.modiferCategory(id, new_category);
        return ResponseEntity.ok(updatedCategory); // 200 OK response with updated category
    }
    @GetMapping("/Category/{id}")
    public ResponseEntity<Categories> getCategory(@PathVariable long id) {
        Categories category = categoriesService.getCategory(id);
        return ResponseEntity.ok(category);
    }
    @GetMapping("/AllCategories")
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = categoriesService.getCategories();
        return ResponseEntity.ok(categories);
    }

}


