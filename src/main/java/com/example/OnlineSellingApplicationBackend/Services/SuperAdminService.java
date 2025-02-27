package com.example.OnlineSellingApplicationBackend.Services;

/*gestion des admin  /  retirer client */
import com.example.OnlineSellingApplicationBackend.entities.*;
import com.example.OnlineSellingApplicationBackend.Repositories.*;
import com.example.OnlineSellingApplicationBackend.entities.Admin;
import com.example.OnlineSellingApplicationBackend.entities.SuperAdmin;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuperAdminService {
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private AdresseRepository adresseRepository;

    private NoteRepository noteRepository;
    @Autowired
    private FavorisRepository favorisRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Ajouter un Admin
    public Admin ajouterAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // Modifier un Admin
    public Admin modifierAdmin(Long adminId, Admin adminDetails) {
        Optional<Admin> existingAdmin = adminRepository.findById(adminId);
        if (existingAdmin.isPresent()) {
            Admin admin = existingAdmin.get();
            admin.setNom(adminDetails.getNom());
            admin.setEmail(adminDetails.getEmail());
            admin.setMotDePasse(adminDetails.getMotDePasse());
            admin.setProfil(adminDetails.getProfil());
            return adminRepository.save(admin);
        } else {
            throw new RuntimeException("Admin non trouvé !");
        }
    }

    // Supprimer un Admin
    public void supprimerAdmin(Long adminId) {
        adminRepository.deleteById(adminId);
    }

    // Lister tous les Admins
    public List<Admin> listerAdmins() {
        return adminRepository.findAll();
    }
    //modifier profil SuperAdmin

    public SuperAdmin modifierProfil(Long id, SuperAdmin updatedSuperAdmin) {
        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(id);

        if (superAdminOptional.isEmpty()) {
            throw new RuntimeException("Super Admin non trouvé !");
        }

        SuperAdmin superAdmin = superAdminOptional.get();
        superAdmin.setNom(updatedSuperAdmin.getNom());
        superAdmin.setEmail(updatedSuperAdmin.getEmail());
        superAdmin.setMotDePasse(updatedSuperAdmin.getMotDePasse());
        superAdmin.setSuperAdminSpecificField(updatedSuperAdmin.getSuperAdminSpecificField()); // Modifier un champ spécifique

        return superAdminRepository.save(superAdmin);
    }
}