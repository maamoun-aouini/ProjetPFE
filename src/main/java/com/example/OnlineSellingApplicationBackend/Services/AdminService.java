package com.example.OnlineSellingApplicationBackend.Services;


import com.example.OnlineSellingApplicationBackend.Repositories.ClientRepository;
import com.example.OnlineSellingApplicationBackend.entities.Client;
import com.example.OnlineSellingApplicationBackend.entities.Commande;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final ClientRepository clientRepository;


    public AdminService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Méthode pour désactiver un client
    public boolean desactiverClient(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setActif(false); // Désactiver le client
            clientRepository.save(client);
            return true;
        }
        return false;
    }

    // Method to get statistics: Number of orders per client and their status
    public Map<String, Object> getClientOrderStatistics(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            Map<String, Object> stats = new HashMap<>();
            stats.put("clientId", clientId);
            stats.put("clientName", client.getNom());

            List<Map<String, Object>> orderDetails = new ArrayList<>();
            for (Commande order : client.getCommandes()) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("orderId", order.getIdCommande());
                orderInfo.put("status", order.getEtat()); // Assuming status exists in Commande
                orderDetails.add(orderInfo);
            }

            stats.put("orders", orderDetails);
            return stats;
        }
        return null;
    }

    // Update client information
    public boolean updateClientInfo(Long clientId, Client updatedClient) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();

            // Update fields if provided
            if (updatedClient.getNom() != null) client.setNom(updatedClient.getNom());
            if (updatedClient.getEmail() != null) client.setEmail(updatedClient.getEmail());
            if (updatedClient.getTel() != null) client.setTel(updatedClient.getTel());
            if (updatedClient.getDescription() != null) client.setDescription(updatedClient.getDescription());

            clientRepository.save(client);
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getClientStatistics() {
        List<Client> clients = clientRepository.findAll();

        return clients.stream().map(client -> {
            // Utiliser Optional pour garantir un Set vide si getCommandes() est null
            Set<Map<String, Object>> commandes = Optional.ofNullable(client.getCommandes())
                    .orElse(Collections.emptySet())  // Si null, utiliser un Set vide
                    .stream()
                    .map(commande -> {
                        // Créer une Map<String, Object> pour chaque commande
                        Map<String, Object> commandeMap = new HashMap<>();
                        commandeMap.put("commandeId", commande.getIdCommande());
                        commandeMap.put("date", commande.getDateCommande());
                        commandeMap.put("etat", commande.getEtat());
                        return commandeMap;
                    })
                    .collect(Collectors.toSet());  // Collecter dans un Set<Map<String, Object>>

            // Organiser les informations : d'abord le client, ensuite ses commandes
            Map<String, Object> clientInfo = new HashMap<>();
            clientInfo.put("clientId", client.getId());
            clientInfo.put("nom", client.getNom());
            clientInfo.put("email", client.getEmail());
            clientInfo.put("nombreCommandes", commandes.size());
            clientInfo.put("commandes", commandes);

            return clientInfo;
        }).collect(Collectors.toList());  // Collecter dans une List<Map<String, Object>>
    }



}