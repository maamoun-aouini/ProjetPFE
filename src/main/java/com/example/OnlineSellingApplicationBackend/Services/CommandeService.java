package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.Repositories.*;
import com.example.OnlineSellingApplicationBackend.entities.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommandeService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private ProduitsRepository produitRepository;

    @Autowired
    private PaquetRepository paquetRepository;

    @Autowired
    private FavorisRepository favorisRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private AdresseRepository adresseRepository;

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public Commande createCommand(Long clientId, AddressResponse addressRequest, List<ProductRequest> productData) {
        // Verify client exists
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Process delivery address using shared logic
        Adresse deliveryAddress = processAddress(addressRequest);

        // Create command with proper enum handling
        Commande commande = new Commande();
        commande.setType(TypeCommande.Produit);
        commande.setClient(client);
        commande.setAdresseLivraison(deliveryAddress);
        commande.setDateCommande(new Date());
        commande.setEtat(EtatCommande.EnCoursDeTraitement);

        // Process command lines
        List<LigneCommande> ligneCommands = productData.stream()
                .map(product -> {
                    Produits produit = produitRepository.findById(product.getId_product())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + product.getId_product()));

                    LigneCommande lc = new LigneCommande();
                    lc.setCommande(commande);
                    lc.setProduit(produit);
                    lc.setQuantite(product.getQuantité());
                    return lc;
                })
                .toList();

        // Set command lines
        commande.setLigneCommandes(ligneCommands);

        // Calculate and set total
        double total = ligneCommands.stream()
                .mapToDouble(lc -> lc.getProduit().getPrix() * lc.getQuantite())
                .sum();
        commande.setTotal(total);  // Make sure this is set before saving

        return commandeRepository.save(commande);
    }

    private Adresse processAddress(AddressResponse request) {
        // Normalize inputs
        String normalizedPays = request.getPays().trim().toLowerCase();
        String normalizedVille = request.getVille().trim().toLowerCase();
        String normalizedRue = request.getRue().trim().toLowerCase();
        String normalizedNumero = request.getNumero().trim().toLowerCase();
        String normalizedIndication = request.getIndication().trim().toLowerCase();
        // Check existing address using DTO projection
        List<AddressResponse> existingAddresses = adresseRepository.findExistingAddress(
                normalizedRue,
                normalizedNumero,
                normalizedVille,
                normalizedPays,
                normalizedIndication
        );

        if (!existingAddresses.isEmpty()) {
            // Always return the first one consistently (or add logic to pick the most relevant)
            return adresseRepository.findById(existingAddresses.get(0).getId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));
        }


        // Create new address components with proper persistence
        List<Pays> existingPays = paysRepository.findByNomIgnoreCase(normalizedPays);
        Pays pays;
        if (!existingPays.isEmpty()) {
            pays = existingPays.get(0); // Take first existing Pays
        } else {
            pays = new Pays(normalizedPays);
            pays = paysRepository.save(pays);
        }

        // Process Ville
        List<Ville> existingVilles = villeRepository.findByNomIgnoreCaseAndPays(normalizedVille, pays);
        Ville ville;
        if (!existingVilles.isEmpty()) {
            ville = existingVilles.get(0); // Take first existing Ville
        } else {
            ville = new Ville(normalizedVille, pays);
            ville = villeRepository.save(ville);
        }
        Adresse newAdresse = new Adresse();
        newAdresse.setRue(normalizedRue);
        newAdresse.setNumero(normalizedNumero);
        newAdresse.setIndication(normalizedIndication);
        newAdresse.setVille(ville);
        return adresseRepository.save(newAdresse);
    }

    public List<Commande> getOrderHistory(Long clientId) {
        // Fetch and return all commandes for the client
        return commandeRepository.findCommandesByClientId(clientId);
    }

    public List<OrderHistoryDTO> mapCommandeToDTO(List<Commande> commandes) {
        List<OrderHistoryDTO> dtoList = new ArrayList<>();

        for (Commande commande : commandes) {
            OrderHistoryDTO dto = new OrderHistoryDTO();
            dto.setOrderId(commande.getIdCommande());
            dto.setOrderDate(commande.getDateCommande());
            dto.setOrderState(commande.getEtat().toString());

            List<OrderHistoryDTO.LigneCommandeDTO> ligneCommandes = new ArrayList<>();
            for (LigneCommande ligneCommande : commande.getLigneCommandes()) {
                OrderHistoryDTO.LigneCommandeDTO ligneDTO = new OrderHistoryDTO.LigneCommandeDTO();
                ligneDTO.setProductId(ligneCommande.getProduit().getId());
                ligneDTO.setProductName(ligneCommande.getProduit().getNom());
                ligneDTO.setQuantity(ligneCommande.getQuantite());
                ligneCommandes.add(ligneDTO);
            }
            dto.setLigneCommandes(ligneCommandes);
            dtoList.add(dto);
        }
        return dtoList;
    }

    // Add these methods to CommandeService
    public List<Commande> getAllOrders() {
        return commandeRepository.findAll();
    }

    public List<AdminOrderDTO> mapToAdminOrderDTO(List<Commande> commandes) {
        return commandes.stream().map(commande -> {
            AdminOrderDTO dto = new AdminOrderDTO();
            dto.setOrderId(commande.getIdCommande());
            dto.setOrderType(commande.getType());

            Client client = commande.getClient();
            dto.setCustomer(client.getNom());

            dto.setTotal(commande.getTotal());
            dto.setStatus(commande.getEtat());
            dto.setDate(commande.getDateCommande());

            return dto;
        }).collect(Collectors.toList());
    }
    public void updateOrderStatus(Long orderId, String newStatus) {
        Commande commande = commandeRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        commande.setEtat(EtatCommande.valueOf(newStatus));
        commandeRepository.save(commande);
    }

    public CommandeDetailDTO getOrderDetails(Long orderId) {
        // Fetch the order with line items and address info
        Commande commande = commandeRepository.findByIdWithLigneCommandes(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Fetch the order with line packs separately
        Optional<Commande> commandeWithPacks = commandeRepository.findByIdWithLigneCommandePack(orderId);

        // Merge the ligneCommandePack if found
        commandeWithPacks.ifPresent(c -> {
            commande.setLigneCommandePack(c.getLigneCommandePack());

            // Maintain bidirectional relationships
            if (commande.getLigneCommandePack() != null) {
                commande.getLigneCommandePack().forEach(lcp -> lcp.setCommande(commande));
            }
        });

        return mapToCommandeDetailDTO(commande);
    }

    private CommandeDetailDTO mapToCommandeDetailDTO(Commande commande) {
        CommandeDetailDTO dto = new CommandeDetailDTO();

        // Basic info
        dto.setOrderId(commande.getIdCommande());
        dto.setOrderType(commande.getType().toString());
        dto.setOrderDate(commande.getDateCommande());
        dto.setStatus(commande.getEtat().toString());

        // Client info
        CommandeDetailDTO.ClientInfoDTO clientInfo = new CommandeDetailDTO.ClientInfoDTO();
        clientInfo.setName(commande.getClient().getNom());
        clientInfo.setEmail(commande.getClient().getEmail());
        clientInfo.setTel(commande.getClient().getTel());
        dto.setClient(clientInfo);

        // Address info
        CommandeDetailDTO.AddressDTO addressDTO = new CommandeDetailDTO.AddressDTO();
        Adresse adresse = commande.getAdresseLivraison();
        if (adresse != null) {
            addressDTO.setStreet(adresse.getRue());
            addressDTO.setNumber(adresse.getNumero());
            addressDTO.setCity(adresse.getVille().getNom());
            addressDTO.setCountry(adresse.getVille().getPays().getNom());
        }
        dto.setDeliveryAddress(addressDTO);

        // Items mapping
        List<CommandeDetailDTO.OrderItemDTO> items = new ArrayList<>();

        // Process products
        if (commande.getLigneCommandes() != null) {
            commande.getLigneCommandes().forEach(lc -> {
                CommandeDetailDTO.OrderItemDTO item = new CommandeDetailDTO.OrderItemDTO();
                item.setName(lc.getProduit().getNom());
                item.setItemType("product");
                item.setQuantity(lc.getQuantite());
                items.add(item);
            });
        }

        // Process packs
        // Process packs
        if (commande.getLigneCommandePack() != null) {
            // Use a Set to avoid duplicates or group by pack ID
            Map<Long, CommandeDetailDTO.OrderItemDTO> packMap = new HashMap<>();

            commande.getLigneCommandePack().forEach(lcp -> {
                // If this pack hasn't been processed yet
                if (!packMap.containsKey(lcp.getPaquet().getId())) {
                    CommandeDetailDTO.OrderItemDTO item = new CommandeDetailDTO.OrderItemDTO();
                    item.setName(lcp.getPaquet().getNom());
                    item.setItemType("pack");
                    item.setQuantity(lcp.getQuantite());

                    // Map pack contents
                    List<String> contents = new ArrayList<>();
                    if (lcp.getPaquet() != null && lcp.getPaquet().getLignePaquets() != null) {
                        lcp.getPaquet().getLignePaquets().forEach(lp -> {
                            if (lp.getProduit() != null) {
                                contents.add(lp.getProduit().getNom() + " (x" + lp.getQuantite() + ")");
                            }
                        });
                    }
                    item.setPackContents(contents.isEmpty() ? null : contents);
                    packMap.put(lcp.getPaquet().getId(), item);
                }
            });

            // Add all unique packs to the items list
            items.addAll(packMap.values());
        }

        dto.setItems(items);
        dto.setTotal(commande.getTotal());

        return dto;
    }





    public List<SalesDataDTO> getSalesData(String range) {
        final ZoneId zone = ZoneId.of("Europe/Paris");
        Date endDate = new Date();

        // Calcul des dates avec gestion des fuseaux horaires
        LocalDateTime now = LocalDateTime.now(zone);
        Date startDate;
        String groupBy;

        switch (range.toUpperCase()) {
            case "THIS WEEK" -> {
                startDate = Date.from(now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .toInstant(zone.getRules().getOffset(now)));
                groupBy = "DAY";
            }
            case "THIS MONTH" -> {
                startDate = Date.from(now.withDayOfMonth(1)
                        .toInstant(zone.getRules().getOffset(now)));
                groupBy = "DAY";
            }
            case "THIS QUARTER" -> {
                int quarterStartMonth = ((now.getMonthValue() - 1) / 3) * 3 + 1;
                startDate = Date.from(now.withMonth(quarterStartMonth).withDayOfMonth(1)
                        .toInstant(zone.getRules().getOffset(now)));
                groupBy = "MONTH";
            }
            default -> { // This Year
                startDate = Date.from(now.withDayOfYear(1)
                        .toInstant(zone.getRules().getOffset(now)));
                groupBy = "MONTH";
            }
        }

        return commandeRepository.getSalesData(startDate, endDate, groupBy)
                .stream()
                .map(result -> {
                    SalesDataDTO dto = new SalesDataDTO();
                    String period = (String) result[0];

                    // Convertir en ISO-8601 complet
                    if ("DAY".equalsIgnoreCase(groupBy)) {
                        dto.setPeriod(period + "T00:00:00Z"); // Jour complet
                    } else {
                        dto.setPeriod(period + "-01T00:00:00Z"); // Ajouter un jour pour les mois
                    }

                    dto.setTotal((Double) result[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DailySalesDTO> getDailySales(LocalDate startDate, LocalDate endDate) {
        return commandeRepository.findDailySalesBetweenDates(startDate, endDate)
                .stream()
                .map(result -> new DailySalesDTO(
                        ((String) result[0]).substring(0, 3), // Format jour (Mon, Tue, etc.)
                        ((Number) result[1]).doubleValue()
                ))
                .collect(Collectors.toList());
    }
    public List<CategorySalesDTO> getSalesByCategory() {
        return commandeRepository.findSalesByCategory()
                .stream()
                .map(result -> new CategorySalesDTO(
                        ((String) result[0]).toLowerCase(), // Uniformisation des noms
                        ((Number) result[1]).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getSalesMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Total Revenue (last 30 days)
        Double totalRevenue = commandeRepository.getTotalRevenueBetweenDates(
                LocalDate.now().minusDays(30),
                LocalDate.now()
        );
        metrics.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        // Average Order Value
        Double avgOrderValue = commandeRepository.getAverageOrderValue();
        metrics.put("averageOrderValue", avgOrderValue != null ? avgOrderValue : 0.0);

        // Conversion Rate
        metrics.put("conversionRate", calculateConversionRate());

        // Sales Growth
        metrics.put("salesGrowth", calculateSalesGrowth());

        return metrics;
    }
    // In CommandeService.java
    private double calculateConversionRate() {
        // Get total completed orders
        long successfulOrders = commandeRepository.countByEtatIn(
                List.of(EtatCommande.Livree)
        );

        // Get total visitors/carts (you'll need to implement this)
        // This requires tracking abandoned carts which you might not have.
        // For demo purposes, we'll use total clients as a placeholder
        long totalClients = clientRepository.count();

        if (totalClients == 0) return 0.0;
        return (successfulOrders * 100.0) / totalClients;
    }

    private double calculateSalesGrowth() {
        // Get current period sales (e.g., this month)
        LocalDate now = LocalDate.now();
        LocalDate startCurrent = now.withDayOfMonth(1);
        LocalDate endCurrent = now.withDayOfMonth(now.lengthOfMonth());

        Double currentRevenue = commandeRepository.getTotalRevenueBetweenDates(
                startCurrent, endCurrent
        );

        // Get previous period sales (e.g., last month)
        LocalDate startPrevious = startCurrent.minusMonths(1);
        LocalDate endPrevious = startPrevious.withDayOfMonth(
                startPrevious.lengthOfMonth()
        );

        Double previousRevenue = commandeRepository.getTotalRevenueBetweenDates(
                startPrevious, endPrevious
        );

        if (previousRevenue == null || previousRevenue == 0) return 0.0;
        return ((currentRevenue - previousRevenue) / previousRevenue) * 100;
    }


    public List<DailyOrdersDTO> getDailyOrders(LocalDate startDate, LocalDate endDate) {
        // Adjust end date to include the entire day (if the database stores datetime)
        LocalDate adjustedEndDate = endDate.plusDays(1);

        System.out.println("Querying with adjusted date range: " + startDate + " to " + adjustedEndDate);

        return commandeRepository.findDailyOrdersBetweenDates(startDate, adjustedEndDate)
                .stream()
                .map(result -> {
                    LocalDate date = convertToLocalDate((Date) result[0]);
                    int count = ((Number) result[1]).intValue();
                    System.out.println("Processing result: date=" + date + ", count=" + count);
                    return new DailyOrdersDTO(date, count);
                })
                .collect(Collectors.toList());
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        // For java.sql.Date
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        // For java.util.Date
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // CommandeService.java
    public List<StatusDistributionDTO> getOrderStatusDistribution() {
        return commandeRepository.countOrdersByStatus().stream()
                .map(result -> new StatusDistributionDTO(
                        ((EtatCommande) result[0]).toString(),
                        (Long) result[1]
                ))
                .collect(Collectors.toList());
    }
    // CommandeService.java
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total Orders
        stats.put("totalOrders", commandeRepository.count());

        // Pending Orders (using your existing countByEtatIn method)
        stats.put("pendingOrders", commandeRepository.countByEtatIn(
                List.of(EtatCommande.EnCoursDeTraitement)
        ));

        // Completed Orders
        stats.put("completedOrders", commandeRepository.countByEtatIn(
                List.of(EtatCommande.Livree)
        ));

        // Total Revenue
        stats.put("totalRevenue", commandeRepository.getTotalRevenue());

        return stats;
    }
}