package com.example.OnlineSellingApplicationBackend.DTO;

import java.util.List;

public class CommandRequest {
    private AddressRequest address;
    private List<ProductRequest> products;

    // Getters and Setters
    public AddressRequest getAddress() {
        return address;
    }

    public void setAddress(AddressRequest address) {
        this.address = address;
    }

    public List<ProductRequest> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRequest> products) {
        this.products = products;
    }
}

