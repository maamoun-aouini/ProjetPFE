
package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.PackRequest;
import com.example.OnlineSellingApplicationBackend.DTO.PackUpdateRequest;
import com.example.OnlineSellingApplicationBackend.Services.AdminService;
import com.example.OnlineSellingApplicationBackend.Services.PackService;
import com.example.OnlineSellingApplicationBackend.entities.Client;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final PackService packService;

    public AdminController(AdminService adminService,PackService packService) {
        this.adminService = adminService;
        this.packService = packService;
    }

    // Désactiver un client
    @PutMapping("/clients/{clientId}/desactiver")
    public ResponseEntity<String> desactiverClient(@PathVariable Long clientId) {
        boolean success = adminService.desactiverClient(clientId);
        if (success) {
            return ResponseEntity.ok("Client désactivé avec succès.");
        } else {
            return ResponseEntity.badRequest().body("Client non trouvé.");
        }
    }

    // Get statistics of a client's orders
    @GetMapping("/clients/{clientId}/stats")
    public ResponseEntity<?> getClientOrderStats(@PathVariable Long clientId) {
        Map<String, Object> stats = adminService.getClientOrderStatistics(clientId);
        if (stats != null) {
            return ResponseEntity.ok(stats);
        } else {
            return ResponseEntity.badRequest().body("Client not found.");
        }
    }

    @PutMapping("/clients/{clientId}/update")
    public ResponseEntity<String> updateClientInfo(@PathVariable Long clientId, @RequestBody Client updatedClient) {
        boolean success = adminService.updateClientInfo(clientId, updatedClient);
        if (success) {
            return ResponseEntity.ok("Client information updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Client not found.");
        }
    }

    // Consulter les statistiques des clients (avec le nombre de commandes et leur état)
    @GetMapping("/clients/statistics")
    public ResponseEntity<List<Map<String, Object>>> getClientStatistics() {
        List<Map<String, Object>> stats = adminService.getClientStatistics();
        return ResponseEntity.ok(stats);
    }

    //creer paquet

    @PostMapping("/packs/create")
    public ResponseEntity<?> createPack(@RequestBody PackRequest packRequest) {
        try {
            Map<String, Object> response = packService.createPack(
                    packRequest.getNomPaquet(),
                    packRequest.getProduitIds(),
                    packRequest.getQuantites(),
                    packRequest.getPrixPack()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/packs")
    public ResponseEntity<List<Map<String, Object>>> getAllPacks() {
        return ResponseEntity.ok(packService.getAllPacks());
    }

    @GetMapping("/packs/{id}")
    public ResponseEntity<Map<String, Object>> getPackById(@PathVariable Long id) {
        return ResponseEntity.ok(packService.getPackById(id));
    }



    @DeleteMapping("packs/delete/{id}")
    public ResponseEntity<String> deletePack(@PathVariable Long id) {
        packService.deletePack(id);
        return ResponseEntity.ok("Pack supprimé avec succès !");
    }
    @PutMapping("/pack/{id}")
    public ResponseEntity<?> updatePack(
            @PathVariable Long id,
            @RequestBody PackUpdateRequest request
    ) {
        Map<String, Object> response = packService.updatePack(id, request);

        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
}