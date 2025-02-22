package com.example.OnlineSellingApplicationBackend.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.OnlineSellingApplicationBackend.Service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/partners")
public class PartnerController {
    @Autowired
    private PartnerService partnerService;
    // Endpoint to get packs and their associated offers
    @GetMapping("/packs-offers")
    public ResponseEntity<List<Map<String, Object>>> getPacksAndOffers() {
        List<Map<String, Object>> response = partnerService.getPacksAndOffers();
        return ResponseEntity.ok(response);
    }
}
