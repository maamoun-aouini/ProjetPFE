package com.example.OnlineSellingApplicationBackend.DTO;

public class FormattedReclamationResponse {
    private String clientName;
    private String title;
    private String description;
    private String dateReclamation;
    private Long commandeId;
    private Long idReclamation;

    public FormattedReclamationResponse(String clientName, String title, String description, String dateReclamation, Long commandeId,Long idReclamation) {
        this.clientName = clientName;
        this.title = title;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.commandeId = commandeId;
        this.idReclamation = idReclamation;
    }

    // Getters and Setters
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateReclamation() {
        return dateReclamation;
    }

    public void setDateReclamation(String dateReclamation) {
        this.dateReclamation = dateReclamation;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }
    public Long getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(Long idReclamation) { // Fix parameter name
        this.idReclamation = idReclamation;
    }
}