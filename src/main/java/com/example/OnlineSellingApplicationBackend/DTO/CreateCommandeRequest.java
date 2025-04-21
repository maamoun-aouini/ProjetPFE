
package com.example.OnlineSellingApplicationBackend.DTO;

import com.example.OnlineSellingApplicationBackend.entities.TypePaiment;

import java.util.List;

public class CreateCommandeRequest {
    private Long clientId;
    private AddressRequest addressRequest;
    private List<PackSellingRequest> packs;
    private TypePaiment paymentType;

    public List<PackSellingRequest> getPacks() {
        return packs;
    }
    public void setPack(List<PackSellingRequest> packs) {
        this.packs = packs;
    }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public void setPacks(List<PackSellingRequest> packs) {
        this.packs = packs;
    }

    public TypePaiment getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(TypePaiment paymentType) {
        this.paymentType = paymentType;
    }

    public AddressRequest getAddressRequest() { return addressRequest; }
    public void setAddressRequest(AddressRequest addressRequest) { this.addressRequest = addressRequest; }


}