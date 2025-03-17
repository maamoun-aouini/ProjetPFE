package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.Repositories.*;
import com.example.OnlineSellingApplicationBackend.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PartnerService {
    @Autowired
    private LigneCommandPackRepository ligneCommandPackRepository;
    @Autowired
    private PaquetRepository paquetRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private ProduitsRepository produitRepository;

    @Autowired
    private FavorisRepository favorisRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;
    @Autowired
    private PaysRepository paysRepository;
    @Autowired
    private VilleRepository villeRepository;
    @Autowired
    private AdresseRepository adresseRepository;
    @Autowired
    private NoteRepository noteRepository;
    public Commande createCommand(Long clientId, AddressRequest addressRequest, List<PackSellingRequest> packData) {
        // Verify client exists
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Process delivery address using shared logic
        Adresse deliveryAddress = processAddress(addressRequest);

        // Create command with proper enum handling
        Commande commande = new Commande();
        commande.setType(TypeCommande.Pack); // Direct enum access
        commande.setClient(client);
        commande.setAdresseLivraison(deliveryAddress);
        commande.setDateCommande(new Date());
        commande.setEtat(EtatCommande.EnCoursDeTraitement);

        // Process command lines
        List<LigneCommandPack> ligneCommands = packData.stream()
                .map(pack -> {
                    Paquet paquet = paquetRepository.findById(pack.getId_Pack())
                            .orElseThrow(() -> new RuntimeException("Pack not found: " + pack.getId_Pack()));

                    LigneCommandPack lc = new LigneCommandPack();
                    lc.setCommande(commande);
                    lc.setPaquet(paquet);
                    lc.setQuantite(pack.getQuantite());
                    return lc;
                })
                .toList();

        // Save command and lines
        commande.setLigneCommandePack(ligneCommands);
        return commandeRepository.save(commande);
    }

    private Adresse processAddress(AddressRequest addressRequest) {
        // Normalize inputs
        String normalizedPays = addressRequest.getNomPays().trim().toLowerCase();
        String normalizedVille = addressRequest.getNomVille().trim().toLowerCase();

        // Find or create country
        List<Pays> existingPays = paysRepository.findByNomIgnoreCase(normalizedPays);
        Pays pays;
        if (!existingPays.isEmpty()) {
            pays = existingPays.get(0); // Take first existing Pays
        } else {
            pays = new Pays(normalizedPays);
            pays = paysRepository.save(pays);
        }

        // Process Ville
        List<Ville> existingVilles = villeRepository.findByNomIgnoreCaseAndPays(normalizedVille, pays);
        Ville ville;
        if (!existingVilles.isEmpty()) {
            ville = existingVilles.get(0); // Take first existing Ville
        } else {
            ville = new Ville(normalizedVille, pays);
            ville = villeRepository.save(ville);
        }
        // Find existing addresses
        List<Adresse> existingAddresses = adresseRepository.findByRueIgnoreCaseAndNumeroIgnoreCaseAndIndicationIgnoreCaseAndVille(
                addressRequest.getRue().trim(),
                addressRequest.getNumero().trim(),
                addressRequest.getIndication() != null ? addressRequest.getIndication().trim() : "",
                ville
        );

        // Return first existing address or create a new one
        if (!existingAddresses.isEmpty()) {
            return existingAddresses.get(0); // Reuse the first matching address
        } else {
            Adresse newAdresse = new Adresse();
            newAdresse.setRue(addressRequest.getRue().trim());
            newAdresse.setNumero(addressRequest.getNumero().trim());
            newAdresse.setIndication(addressRequest.getIndication() != null ?
                    addressRequest.getIndication().trim() : "");
            newAdresse.setVille(ville);
            return adresseRepository.save(newAdresse);
        }
    }

}
