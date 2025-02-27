package com.example.OnlineSellingApplicationBackend.Controllers;
import com.example.OnlineSellingApplicationBackend.DTO.CommandRequest;
import com.example.OnlineSellingApplicationBackend.DTO.CreateCommandeRequest;
import com.example.OnlineSellingApplicationBackend.Services.PackService;
import com.example.OnlineSellingApplicationBackend.entities.Commande;
import com.example.OnlineSellingApplicationBackend.entities.LigneCommandPack;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.OnlineSellingApplicationBackend.Services.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/partners")
public class PartnerController {
    @Autowired
    private PackService packService;
    @Autowired
    private PartnerService partnerService;
    // Endpoint to get packs and their associated offers
    @GetMapping("/packs/available")
    public ResponseEntity<List<Map<String, Object>>> getAvailablePacks() {
        return ResponseEntity.ok(packService.getAllPacks()); // Adapter si filtrage nécessaire
    }
    @PostMapping("/{clientId}/commands")
    public ResponseEntity<Commande> createCommand(
            @PathVariable("clientId") Long clientId,  // Correction du PathVariable
            @RequestBody CreateCommandeRequest commandRequest) {  // Utilisation correcte de CommandRequest
        Commande commande = partnerService.createCommand(clientId, commandRequest.getAddressRequest(), commandRequest.getPacks());
        return ResponseEntity.ok(commande);
    }
}
