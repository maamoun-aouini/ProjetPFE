package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.CommandRequest;
import com.example.OnlineSellingApplicationBackend.DTO.CreateCommandeRequest;
import com.example.OnlineSellingApplicationBackend.DTO.OrderHistoryDTO;
import com.example.OnlineSellingApplicationBackend.Services.PartnerService;
import com.example.OnlineSellingApplicationBackend.Services.ClientService;

import com.example.OnlineSellingApplicationBackend.entities.Commande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private ClientService clientService;


    /** 🔹 Créer une commande Pack pour le partenaire */
    //@PreAuthorize("hasAnyRole('USERPARTNER')")
    @PostMapping("/{clientId}/Pack")
    public ResponseEntity<Commande> createPartnerCommand(@PathVariable Long clientId, @RequestBody CreateCommandeRequest commandRequest) {
        Commande commande = partnerService.createCommand(clientId, commandRequest.getAddressRequest(), commandRequest.getPacks());
        return ResponseEntity.status(201).body(commande);
    }
    /**
     * Create an order for a client.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @PostMapping("/{clientId}")
    public ResponseEntity<Commande> createClientCommand(
            @PathVariable Long clientId,
            @RequestBody CommandRequest commandRequest) {
        Commande commande = clientService.createCommand(clientId, commandRequest.getAddress(), commandRequest.getProducts());
        return ResponseEntity.ok(commande);
    }
    /**
     * Get order history of a client.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER' , 'SUPERADMIN' , 'ADMIN')")
    @GetMapping("/{clientId}")
    public ResponseEntity<List<OrderHistoryDTO>> getCommandsHistory(@PathVariable Long clientId) {
        List<Commande> orderHistory = clientService.getOrderHistory(clientId);
        List<OrderHistoryDTO> orderHistoryDTO = clientService.mapCommandeToDTO(orderHistory);
        return ResponseEntity.ok(orderHistoryDTO);
    }
}
