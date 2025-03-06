package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.PackRequest;
import com.example.OnlineSellingApplicationBackend.DTO.PackUpdateRequest;
import com.example.OnlineSellingApplicationBackend.Services.AdminService;
import com.example.OnlineSellingApplicationBackend.Services.PackService;
import com.example.OnlineSellingApplicationBackend.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    /** 🔹 Désactiver un client */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @PutMapping("/{clientId}/desactiver")
    public ResponseEntity<String> desactiverClient(@PathVariable Long clientId) {
        if (adminService.desactiverClient(clientId)) {
            return ResponseEntity.ok("Client désactivé avec succès.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client non trouvé.");
    }

    /** 🔹 Récupérer les statistiques des commandes d'un client */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @GetMapping("/{clientId}/stats")
    public ResponseEntity<?> getClientOrderStats(@PathVariable Long clientId) {
        Map<String, Object> stats = adminService.getClientOrderStatistics(clientId);
        return stats != null ? ResponseEntity.ok(stats)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client non trouvé.");
    }

    /** 🔹 Modifier les informations d'un client */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @PutMapping("/{clientId}")
    public ResponseEntity<String> updateClientInfo(@PathVariable Long clientId, @RequestBody Client updatedClient) {
        if (adminService.updateClientInfo(clientId, updatedClient)) {
            return ResponseEntity.ok("Informations du client mises à jour avec succès.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client non trouvé.");
    }

    /** 🔹 Récupérer les statistiques globales des clients */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @GetMapping("/clients/statistics")
    public ResponseEntity<List<Map<String, Object>>> getClientStatistics() {
        return ResponseEntity.ok(adminService.getClientStatistics());
    }
}
