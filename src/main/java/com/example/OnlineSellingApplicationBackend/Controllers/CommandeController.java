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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        Commande commande = commandeService.createCommand(clientId, commandRequest.getAddress(), commandRequest.getProducts());
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
    public ResponseEntity<?> getDailyOrders(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            // 🔥 Debug : Afficher les valeurs reçues avant nettoyage
            System.out.println("Raw startDate: '" + startDate + "', Raw endDate: '" + endDate + "'");

            // Nettoyer les espaces ou caractères cachés
            startDate = startDate.trim();
            endDate = endDate.trim();

            // 🔥 Debug : Afficher après nettoyage
            System.out.println("Cleaned startDate: '" + startDate + "', Cleaned endDate: '" + endDate + "'");

            // Try to parse dates in multiple formats
            LocalDate start;
            LocalDate end;

            try {
                // First try ISO format (YYYY-MM-DD)
                start = LocalDate.parse(startDate);
                end = LocalDate.parse(endDate);
            } catch (DateTimeParseException e) {
                // Then try DD/MM/YYYY format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                start = LocalDate.parse(startDate, formatter);
                end = LocalDate.parse(endDate, formatter);
            }

            // 🔥 Debug : Log the converted dates
            System.out.println("Parsed start date: " + start + ", Parsed end date: " + end);

            List<DailyOrdersDTO> results = commandeService.getDailyOrders(start, end);

            // 🔥 Debug : Log the result size
            System.out.println("Result size: " + results.size());

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace(); // Add this to get full stack trace
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }

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
