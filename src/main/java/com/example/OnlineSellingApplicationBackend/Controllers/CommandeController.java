package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.Repositories.ClientRepository;
import com.example.OnlineSellingApplicationBackend.Services.CommandeService;
import com.example.OnlineSellingApplicationBackend.Services.PartnerService;
import com.example.OnlineSellingApplicationBackend.Services.ClientService;

import com.example.OnlineSellingApplicationBackend.entities.Client;
import com.example.OnlineSellingApplicationBackend.entities.Commande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private ClientRepository clientRepository;


    /** 🔹 Créer une commande Pack pour le partenaire */
    //@PreAuthorize("hasAnyRole('USERPARTNER')")
    @PostMapping("/{clientId}/Pack")
    public ResponseEntity<Commande> createPartnerCommand(@RequestBody CreateCommandeRequest commandRequest) {
        Commande commande = partnerService.createCommand(commandRequest.getClientId(), commandRequest.getAddressRequest(), commandRequest.getPacks(),commandRequest.getPaymentType() );
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
        Commande commande = commandeService.createCommand(clientId, commandRequest.getAddress(), commandRequest.getProducts(),commandRequest.getPaymentType() );
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
            @RequestParam(required = false) String startDateParam,
            @RequestParam(required = false) String endDateParam) {

        try {
            LocalDate startDate;
            LocalDate endDate;

            if (startDateParam != null && endDateParam != null) {
                // Use the provided dates
                startDate = LocalDate.parse(startDateParam);
                endDate = LocalDate.parse(endDateParam);
            } else {
                // Default to last 7 days
                endDate = LocalDate.now();
                startDate = endDate.minusDays(6);
            }

            System.out.println("Using date range: " + startDate + " to " + endDate);

            List<DailyOrdersDTO> results = commandeService.getDailyOrders(startDate, endDate);
            System.out.println("Query returned " + results.size() + " results");

            return ResponseEntity.ok(results);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use yyyy-MM-dd");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
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
