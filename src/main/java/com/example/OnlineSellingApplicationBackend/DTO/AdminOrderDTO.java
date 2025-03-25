package com.example.OnlineSellingApplicationBackend.DTO;

import com.example.OnlineSellingApplicationBackend.entities.EtatCommande;
import com.example.OnlineSellingApplicationBackend.entities.TypeCommande;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class AdminOrderDTO {
    private Long orderId;
    private TypeCommande orderType;
    private String customer;
    private double total;
    private EtatCommande status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public TypeCommande getOrderType() { return orderType; }
    public void setOrderType(TypeCommande orderType) { this.orderType = orderType; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public EtatCommande getStatus() { return status; }
    public void setStatus(EtatCommande status) { this.status = status; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}