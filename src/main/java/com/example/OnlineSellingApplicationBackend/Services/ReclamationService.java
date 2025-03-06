package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.Repositories.ClientRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.CommandeRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ReclamationRepository;
import com.example.OnlineSellingApplicationBackend.entities.Client;
import com.example.OnlineSellingApplicationBackend.entities.Commande;
import com.example.OnlineSellingApplicationBackend.entities.Reclamation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReclamationService {

    @Autowired
    private ReclamationRepository reclamationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    // Méthode pour ajouter une réclamation
    public ResponseEntity<?> ajouterReclamation(Long clientId, Reclamation reclamation) {
        Client client = clientRepository.findById(clientId).orElse(null);

        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Client non trouvé.");
        }

        // Vérifier si le client a passé au moins une commande
        if (client.getCommandes() == null || client.getCommandes().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Le client doit d'abord passer des commandes avant de soumettre une réclamation.");
        }

        // Vérifier que la commande associée à la réclamation existe bien et appartient au client
        Commande commande = commandeRepository.findById(reclamation.getCommande().getIdCommande()).orElse(null);

        if (commande == null || !commande.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La commande spécifiée n'existe pas ou n'appartient pas à ce client.");
        }

        // Associer la réclamation au client et à la commande
        reclamation.setClient(client);
        reclamation.setCommande(commande);

        // Sauvegarde de la réclamation
        Reclamation savedReclamation = reclamationRepository.save(reclamation);

        return ResponseEntity.ok(savedReclamation);
    }
    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findAll();
    }

    public List<Reclamation> getReclamationsByClient(Long clientId) {
        return reclamationRepository.findAllByClientId(clientId);
    }
}

