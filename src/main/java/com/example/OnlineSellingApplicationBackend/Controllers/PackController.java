package com.example.OnlineSellingApplicationBackend.Controllers;
import com.example.OnlineSellingApplicationBackend.Services.PackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/packs")
public class PackController {

    @Autowired
    private PackService packService;

    // Get all packs
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPacks() {
        List<Map<String, Object>> packs = packService.getAllPacks();
        return ResponseEntity.ok(packs);
    }

    // Get single pack by ID
    @GetMapping("/{packId}")
    public ResponseEntity<?> getPackById(@PathVariable Long packId) {
        try {
            Map<String, Object> pack = packService.getPackById(packId);
            return ResponseEntity.ok(pack);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", "error",
                    "message", ex.getMessage()
            ));
        }
    }
}
