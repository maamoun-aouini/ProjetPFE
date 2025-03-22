package com.example.OnlineSellingApplicationBackend.Services;

/*gestion des admin  /  retirer client */
import com.example.OnlineSellingApplicationBackend.DTO.FileUploadUtil;
import com.example.OnlineSellingApplicationBackend.entities.*;
import com.example.OnlineSellingApplicationBackend.Repositories.*;
import com.example.OnlineSellingApplicationBackend.entities.Admin;
import com.example.OnlineSellingApplicationBackend.entities.SuperAdmin;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;
    // Ajouter un Admin

    public Admin ajouterAdmin(Admin admin) {
        admin.setMotDePasse(passwordEncoder.encode(admin.getMotDePasse()));
        return adminRepository.save(admin);
    }
    public SuperAdmin ajouterSuperAdmin(SuperAdmin superAdmin) {
        Optional<Client> client = clientRepository.findByEmail(superAdmin.getEmail());
        if(client.isEmpty()){
            Optional<Admin> admin = adminRepository.findByEmail(superAdmin.getEmail());
            if(!admin.isEmpty()){
                throw new RuntimeException("The email you are using is registred in the admin table");
            }
        }else{
            throw new RuntimeException("The email you are using is registred in the client table");
        }
        SuperAdmin superadmin=new SuperAdmin();
        superadmin.setProfil(superAdmin.getProfil());
        superadmin.setNom(superAdmin.getNom());
        superadmin.setEmail(superAdmin.getEmail());

        superadmin.setMotDePasse(passwordEncoder.encode(superAdmin.getMotDePasse())); // Now works

        return superAdminRepository.save(superadmin);
    }
    // Modifier un Admin

    public Admin modifierAdmin(Long adminId, String nom, String email, String password, MultipartFile profil) {
        return adminRepository.findById(adminId)
                .map(admin -> {
                    admin.setNom(nom);
                    admin.setEmail(email);

                    if(password != null && !password.isEmpty()) {
                        admin.setMotDePasse(passwordEncoder.encode(password));
                    }

                    if(profil != null && !profil.isEmpty()) {
                        String fileName = fileStorageService.storeFile(profil);
                        admin.setProfil("/uploads/" + fileName);
                    }

                    return adminRepository.save(admin);
                })
                .orElseThrow(() -> new RuntimeException("Admin not found"));
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
    @Transactional
    public SuperAdmin updateProfile(Long id, SuperAdmin updatedSuperAdmin, MultipartFile file) {
        SuperAdmin superAdmin = superAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Super Admin not found!"));

        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            superAdmin.setProfil("/uploads/" + fileName);
        }

        superAdmin.setNom(updatedSuperAdmin.getNom());
        superAdmin.setEmail(updatedSuperAdmin.getEmail());

        if (updatedSuperAdmin.getMotDePasse() != null && !updatedSuperAdmin.getMotDePasse().isEmpty()) {
            superAdmin.setMotDePasse(passwordEncoder.encode(updatedSuperAdmin.getMotDePasse()));
        }

        return superAdminRepository.save(superAdmin);
    }
    public SuperAdmin getCurrentSuperAdmin() {
        // Get the email of the currently authenticated user
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Retrieve the SuperAdmin by email
        return superAdminRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Super Admin not found"));
    }






}
