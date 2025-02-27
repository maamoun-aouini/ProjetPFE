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
        // Vérifier si le client existe
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isEmpty()) {
            throw new RuntimeException("Client not found");
        }
        Client client = clientOptional.get();

        // Vérifier TypeCommande
        String typeCommandeStr = "Pack";
        System.out.println("TypeCommand received: " + typeCommandeStr);

        // Trouver ou créer le Pays et la Ville
        Pays pays = paysRepository.findByNom(addressRequest.getNomPays())
                .orElseGet(() -> {
                    Pays newPays = new Pays();
                    newPays.setNom(addressRequest.getNomPays());
                    return paysRepository.save(newPays);
                });

        Ville ville = villeRepository.findByNomAndPays(addressRequest.getNomVille(), pays)
                .orElseGet(() -> {
                    Ville newVille = new Ville();
                    newVille.setNom(addressRequest.getNomVille());
                    newVille.setPays(pays);
                    return villeRepository.save(newVille);
                });
        // Créer l'adresse
        Adresse adresse = new Adresse();
        adresse.setRue(addressRequest.getRue());
        adresse.setNumero(addressRequest.getNumero());
        adresse.setIndication(addressRequest.getIndication());
        adresse.setVille(ville);
        Adresse savedAdresse = adresseRepository.save(adresse);
        // Créer la commande
        Commande commande = new Commande();
        try {
            commande.setType(TypeCommande.valueOf(typeCommandeStr)); // Vérifier l'Enum
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid TypeCommande value: " + typeCommandeStr);
        }
        commande.setClient(client);
        commande.setAdresseLivraison(savedAdresse);
        commande.setDateCommande(new Date());
        commande.setEtat(EtatCommande.EnCoursDeTraitement);
        // Ajouter les packs à la commande
        List<LigneCommandPack> ligneCommands = new ArrayList<>();
        for (PackSellingRequest pack : packData) {
            Paquet packfetch = paquetRepository.findById(pack.getId_Pack())
                    .orElseThrow(() -> new RuntimeException("Pack with ID " + pack.getId_Pack()+ " not found"));
            LigneCommandPack ligneCommand = new LigneCommandPack();
            ligneCommand.setCommande(commande);
            ligneCommand.setPaquet(packfetch);
            ligneCommand.setQuantite(pack.getQuantite());
            ligneCommands.add(ligneCommand);
        }
        // Sauvegarder la commande et ses packs
        Commande savedCommande = commandeRepository.save(commande);
        ligneCommands.forEach(lc -> lc.setCommande(savedCommande));
        ligneCommandPackRepository.saveAll(ligneCommands);

        return savedCommande;
    }

}
