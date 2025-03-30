package com.example.OnlineSellingApplicationBackend.DTO;

import java.time.LocalDate;

public class DailyOrdersDTO {
    private LocalDate date;
    private int orderCount;

    public DailyOrdersDTO(LocalDate date, int orderCount) {
        this.date = date;
        this.orderCount = orderCount;
    }

    // Getters and setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    @Override
    public String toString() {
        return "DailyOrdersDTO{" +
                "date=" + date +
                ", orderCount=" + orderCount +
                '}';
    }
}