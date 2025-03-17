package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.entities.Admin;
import com.example.OnlineSellingApplicationBackend.Services.SuperAdminService;
import com.example.OnlineSellingApplicationBackend.entities.SuperAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    @Autowired
    private SuperAdminService superAdminService;
    @PostMapping
    public ResponseEntity<?> addSuperAdmin(@RequestBody SuperAdmin superAdmin) {
        try{
            SuperAdmin registeredAdmin = superAdminService.ajouterSuperAdmin(superAdmin);
            return ResponseEntity.ok(registeredAdmin);
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }

    }
    /**
     * Add a new Admin.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN')")
    @PostMapping("/admins")
    public ResponseEntity<Admin> addAdmin(@RequestBody Admin admin) {
        Admin registeredAdmin = superAdminService.ajouterAdmin(admin);
        return ResponseEntity.ok(registeredAdmin);
    }
    /**
     * Update an Admin.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN')")
    @PutMapping("/admins/{adminId}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long adminId, @RequestBody Admin adminDetails) {
        return ResponseEntity.ok(superAdminService.modifierAdmin(adminId, adminDetails));
    }

    /**
     * Delete an Admin.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN')")
    @DeleteMapping("/admins/{adminId}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long adminId) {
        superAdminService.supprimerAdmin(adminId);
        return ResponseEntity.ok("Admin deleted successfully!");
    }

    /**
     * Get all Admins.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN')")
    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(superAdminService.listerAdmins());
    }

    /**
     * Update SuperAdmin profile.
     */
    //@PreAuthorize("hasAnyRole('SUPERADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SuperAdmin> updateSuperAdminProfile(
            @PathVariable Long id,
            @RequestBody SuperAdmin updatedSuperAdmin) {
        SuperAdmin modifiedSuperAdmin = superAdminService.modifierProfil(id, updatedSuperAdmin);
        return ResponseEntity.ok(modifiedSuperAdmin);
    }
}
