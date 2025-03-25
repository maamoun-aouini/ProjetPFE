package com.example.OnlineSellingApplicationBackend.DTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DailyOrdersDTO {
    private String date;  // Formatted as "MM/dd"
    private Integer orders;

    public DailyOrdersDTO(LocalDate date, Integer orders) {
        this.date = date.format(DateTimeFormatter.ofPattern("MM/dd"));
        this.orders = orders;
    }



    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }
}