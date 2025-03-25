package com.example.OnlineSellingApplicationBackend.DTO;

import java.util.List;

public record SubCategoryResponse(
        Long id,
        String name,
        String description,
        List<String> photos,
        List<SubCategoryResponse> subCategories
) {}