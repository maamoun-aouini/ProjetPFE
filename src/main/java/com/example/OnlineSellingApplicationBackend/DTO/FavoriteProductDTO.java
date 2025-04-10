package com.example.OnlineSellingApplicationBackend.DTO;

import java.util.List;

public class FavoriteProductDTO {
    private Long clientId;
    private Long productId;
    private String name;
    private String description;
    private double partnerPromotion;
    private double individualPromotion;
    private String selection;
    private Object photos;  // Changed to Object to handle both single and multiple photos
    private Double price;
    private boolean availability;

    // Constructor that matches your usage
    public FavoriteProductDTO(Long clientId, Long productId, String name, String description,
                              double partnerPromotion, double individualPromotion, String selection,
                              Object photos, Double price, boolean availability) {
        this.clientId = clientId;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.partnerPromotion = partnerPromotion;
        this.individualPromotion = individualPromotion;
        this.selection = selection;
        this.photos = photos;
        this.price = price;
        this.availability = availability;
    }

    // Getters and setters
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPartnerPromotion() {
        return partnerPromotion;
    }

    public void setPartnerPromotion(double partnerPromotion) {
        this.partnerPromotion = partnerPromotion;
    }

    public double getIndividualPromotion() {
        return individualPromotion;
    }

    public void setIndividualPromotion(double individualPromotion) {
        this.individualPromotion = individualPromotion;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public Object getPhotos() {
        return photos;
    }

    public void setPhotos(Object photos) {
        this.photos = photos;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}