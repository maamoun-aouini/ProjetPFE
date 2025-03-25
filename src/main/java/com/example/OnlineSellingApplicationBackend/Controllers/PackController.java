package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.PackRequest;
import com.example.OnlineSellingApplicationBackend.Services.PackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/packs")
public class PackController {

    private final PackService packService;

    @Autowired
    public PackController(PackService packService) {
        this.packService = packService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPack(
            @RequestPart("pack") String packJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        try {
            PackRequest packRequest = new ObjectMapper().readValue(packJson, PackRequest.class);
            packRequest.setPhotos(photos);
            return ResponseEntity.ok(packService.createPack(packRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePack(
            @PathVariable Long id,
            @RequestPart("pack") String packJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        try {
            PackRequest packRequest = new ObjectMapper().readValue(packJson, PackRequest.class);
            packRequest.setPhotos(photos);
            return ResponseEntity.ok(packService.updatePack(id, packRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPacks() {
        try {
            return ResponseEntity.ok(packService.getAllPacks());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPackById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(packService.getPackById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePack(@PathVariable Long id) {
        try {
            packService.deletePack(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}