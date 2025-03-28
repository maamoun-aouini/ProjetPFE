package com.example.OnlineSellingApplicationBackend.DTO;

import java.util.List;

public record CategoryResponse(
        Long id,
        String nom,
        String description,
        List<String> photos,
        Long parentId,
        List<SubCategoryResponse> subCategories
) {}