package com.example.OnlineSellingApplicationBackend.DTO;




import java.util.List;

public class PackRequest {
    private Long id;
    private String nomPaquet;
    private List<Long> produitIds;
    private List<Integer> quantites;
    private double prixPack;  // Prix réduit appliqué sur le pack

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomPaquet() {
        return nomPaquet;
    }

    public void setNomPaquet(String nomPaquet) {
        this.nomPaquet = nomPaquet;
    }

    public List<Long> getProduitIds() {
        return produitIds;
    }

    public void setProduitIds(List<Long> produitIds) {
        this.produitIds = produitIds;
    }

    public List<Integer> getQuantites() {
        return quantites;
    }

    public void setQuantites(List<Integer> quantites) {
        this.quantites = quantites;
    }

    public double getPrixPack() {
        return prixPack;
    }

    public void setPrixPack(double prixPack) {
        this.prixPack = prixPack;
    }
}