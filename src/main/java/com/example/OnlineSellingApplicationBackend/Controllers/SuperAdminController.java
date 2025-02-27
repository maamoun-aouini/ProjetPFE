package com.example.OnlineSellingApplicationBackend.Controllers;



import com.example.OnlineSellingApplicationBackend.entities.Admin;
import com.example.OnlineSellingApplicationBackend.Services.SuperAdminService;
import com.example.OnlineSellingApplicationBackend.entities.SuperAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    @Autowired
    private SuperAdminService superAdminService;
    // Ajouter un Admin
    @PostMapping("/ajouterAdmin")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        Admin registeredAdmin = superAdminService.ajouterAdmin(admin);
        return ResponseEntity.ok(registeredAdmin);
    }

    // Modifier un Admin
    @PutMapping("/modifierAdmin/{adminId}")
    public ResponseEntity<Admin> modifierAdmin(@PathVariable Long adminId, @RequestBody Admin adminDetails) {
        return ResponseEntity.ok(superAdminService.modifierAdmin(adminId, adminDetails));
    }

    // Supprimer un Admin
    @DeleteMapping("/supprimerAdmin/{adminId}")
    public ResponseEntity<String> supprimerAdmin(@PathVariable Long adminId) {
        superAdminService.supprimerAdmin(adminId);
        return ResponseEntity.ok("Admin supprimé avec succès !");
    }
    // Lister tous les Admins
    @GetMapping("/listerAdmins")
    public ResponseEntity<List<Admin>> listerAdmins() {
        return ResponseEntity.ok(superAdminService.listerAdmins());
    }
    //modifier profil superAdmin
    @PutMapping("/modifierProfil/{id}")
    public ResponseEntity<SuperAdmin> modifierProfil(
            @PathVariable Long id,
            @RequestBody SuperAdmin updatedSuperAdmin) {
        SuperAdmin modifiedSuperAdmin = superAdminService.modifierProfil(id, updatedSuperAdmin);
        return ResponseEntity.ok(modifiedSuperAdmin);
    }
}