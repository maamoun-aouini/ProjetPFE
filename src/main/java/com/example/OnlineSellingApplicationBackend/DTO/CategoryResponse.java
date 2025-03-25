package com.example.OnlineSellingApplicationBackend.DTO;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        List<String> photos,
        Long parentId,
        List<SubCategoryResponse> subCategories
) {}