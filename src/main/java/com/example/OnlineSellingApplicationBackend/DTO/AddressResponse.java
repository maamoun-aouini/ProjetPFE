package com.example.OnlineSellingApplicationBackend.DTO;

import com.example.OnlineSellingApplicationBackend.entities.Adresse;

public class AddressResponse {
    private Long id;
    private String rue;
    private String numero;
    private String indication;
    private String ville;
    private String pays;

    // Default constructor (required by Jackson)
    public AddressResponse() {
    }

    // Constructor from Entity
    public AddressResponse(Adresse adresse) {
        this.id = adresse.getId();
        this.rue = adresse.getRue();
        this.numero = adresse.getNumero();
        this.indication = adresse.getIndication();

        // Add null checks for ville and pays
        if (adresse.getVille() != null) {
            this.ville = adresse.getVille().getNom();
            if (adresse.getVille().getPays() != null) {
                this.pays = adresse.getVille().getPays().getNom();
            }
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIndication() {
        return indication;
    }

    public void setIndication(String indication) {
        this.indication = indication;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }
}