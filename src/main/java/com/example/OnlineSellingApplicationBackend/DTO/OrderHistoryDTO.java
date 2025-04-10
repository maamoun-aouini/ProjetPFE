package com.example.OnlineSellingApplicationBackend.DTO;

import com.example.OnlineSellingApplicationBackend.entities.TypePaiment;

import java.util.Date;
import java.util.List;

public class OrderHistoryDTO {
    private Long orderId;
    private Date orderDate;
    private String orderState;
    private List<LigneCommandeDTO> ligneCommandes;

    private TypePaiment paymentType;

    public TypePaiment getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(TypePaiment paymentType) {
        this.paymentType = paymentType;
    }

    public OrderHistoryDTO() {

    }

    public static class LigneCommandeDTO {
        private Long productId;
        private String productName;
        private int quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

    }

    public OrderHistoryDTO(Long orderId, Date orderDate, String orderState, List<LigneCommandeDTO> ligneCommandes) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderState = orderState;
        this.ligneCommandes = ligneCommandes;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public List<LigneCommandeDTO> getLigneCommandes() {
        return ligneCommandes;
    }

    public void setLigneCommandes(List<LigneCommandeDTO> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }
}
