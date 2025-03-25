package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.Services.CommandeService;
import com.example.OnlineSellingApplicationBackend.Services.PartnerService;
import com.example.OnlineSellingApplicationBackend.Services.ClientService;

import com.example.OnlineSellingApplicationBackend.entities.Commande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private CommandeService commandeService;


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
    @GetMapping("/orders/all")
    public ResponseEntity<List<AdminOrderDTO>> getAllOrdersForAdmin() {
        List<Commande> commandes = commandeService.getAllOrders();
        List<AdminOrderDTO> dtos = commandeService.mapToAdminOrderDTO(commandes);
        return ResponseEntity.ok(dtos);


    }
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId, // ✅ Change to Long
            @RequestBody Map<String, String> statusRequest
    ) {
        commandeService.updateOrderStatus(orderId, statusRequest.get("newStatus"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<CommandeDetailDTO> getOrderDetails(@PathVariable Long orderId) {
        CommandeDetailDTO orderDetails = commandeService.getOrderDetails(orderId);
        return ResponseEntity.ok(orderDetails);
    }
    // In CommandeController.java
    @GetMapping("/orders/daily")
    public ResponseEntity<List<DailyOrdersDTO>> getDailyOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(commandeService.getDailyOrders(startDate, endDate));
    }
    // CommandeController.java
    @GetMapping("/orders/status-distribution")
    public ResponseEntity<List<StatusDistributionDTO>> getOrderStatusDistribution() {
        return ResponseEntity.ok(commandeService.getOrderStatusDistribution());
    }
    // CommandeController.java
    @GetMapping("/orders/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        return ResponseEntity.ok(commandeService.getOrderStats());
    }
}
