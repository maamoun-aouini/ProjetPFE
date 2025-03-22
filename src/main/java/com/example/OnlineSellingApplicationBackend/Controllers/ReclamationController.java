package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.FormattedReclamationResponse;
import com.example.OnlineSellingApplicationBackend.Services.ReclamationService;
import com.example.OnlineSellingApplicationBackend.entities.Reclamation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
public class ReclamationController {
    @Autowired
    private ReclamationService reclamationService;
    /**
     * Submit a reclamation.
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER')")
    @PostMapping("/{clientId}")
    public ResponseEntity<?> submitReclamation(@PathVariable Long clientId, @RequestBody Reclamation reclamation) {
        return reclamationService.ajouterReclamation(clientId, reclamation);
    }
    /**
     * Get all reclamations (Admin only).
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<FormattedReclamationResponse>> getAllReclamations() {
        List<FormattedReclamationResponse> reclamations = reclamationService.getAllReclamations();
        return ResponseEntity.ok(reclamations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReclamation(@PathVariable Long id) {
        reclamationService.deleteReclamation(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Get reclamations by a specific client (Admin only).
     */
    //@PreAuthorize("hasAnyRole('USERSTANDARD', 'USERPARTNER' , 'SUPERADMIN' , 'ADMIN')")
    @GetMapping("/clients/{clientId}")
    public ResponseEntity<List<Reclamation>> getReclamationsByClient(@PathVariable Long clientId) {
        List<Reclamation> reclamations = reclamationService.getReclamationsByClient(clientId);
        return ResponseEntity.ok(reclamations);
    }
    /**
     * Get a reclamation by its ID.
     */
//@PreAuthorize("hasAnyRole('SUPERADMIN' , 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<FormattedReclamationResponse> getReclamationById(@PathVariable Long id) {
        FormattedReclamationResponse response = reclamationService.getReclamationById(id);
        return ResponseEntity.ok(response);
    }


}