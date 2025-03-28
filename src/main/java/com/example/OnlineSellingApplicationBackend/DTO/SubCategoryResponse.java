package com.example.OnlineSellingApplicationBackend.DTO;

import java.util.List;

public record SubCategoryResponse(
        Long id,
        String nom,
        String description,
        List<String> photos,
        List<SubCategoryResponse> subCategories
) {
    
}