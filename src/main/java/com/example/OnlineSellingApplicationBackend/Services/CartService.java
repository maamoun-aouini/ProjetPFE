package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.DTO.CartDTO;
import com.example.OnlineSellingApplicationBackend.DTO.CartItemDTO;
import com.example.OnlineSellingApplicationBackend.DTO.CartOperationRequest;
import com.example.OnlineSellingApplicationBackend.Repositories.CartItemRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.CartRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ClientRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.PaquetRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.ProduitsRepository;
import com.example.OnlineSellingApplicationBackend.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProduitsRepository produitRepository;

    @Autowired
    private PaquetRepository paquetRepository;

    /**
     * Get cart for a client, creating one if it doesn't exist
     */
    @Transactional
    public CartDTO getOrCreateCart(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        // Find existing cart or create new one
        Cart cart = cartRepository.findByClientId(clientId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setClient(client);
                    newCart.setCreatedAt(new Date());
                    newCart.updateExpirationTime(); // Set initial expiration
                    return cartRepository.save(newCart);
                });

        // If cart exists but is expired, reset it
        if (cart.isExpired()) {
            resetExpiredCart(cart);
        }

        return convertToDTO(cart);
    }

    /**
     * Reset an expired cart - return items to stock, keep the cart but remove items and reset timers
     */
    @Transactional
    private void resetExpiredCart(Cart cart) {
        // Return items to stock
        returnCartItemsToStock(cart);

        // Clear all items
        cart.getCartItems().clear();

        // Reset timestamps
        cart.setCreatedAt(new Date());
        cart.updateExpirationTime();

        cartRepository.save(cart);
    }

    /**
     * Reset the cart's expiration time - used when a cart was empty and items are added again
     */
    @Transactional
    private void resetEmptyCart(Cart cart) {
        // Reset timestamps
        cart.setCreatedAt(new Date());
        cart.updateExpirationTime();

        cartRepository.save(cart);
    }

    /**
     * Helper method to return cart items to stock
     */
    @Transactional
    private void returnCartItemsToStock(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduit() != null) {
                // Return product to stock
                Produits product = item.getProduit();
                product.setQuantite(product.getQuantite() + item.getQuantity());
                produitRepository.save(product);
            } else if (item.getPaquet() != null) {
                // Return pack to stock
                Paquet pack = item.getPaquet();
                pack.setQuantite(pack.getQuantite() + item.getQuantity());
                paquetRepository.save(pack);
            }
        }
    }

    /**
     * Add an item to the cart
     */
    @Transactional
    public CartDTO addToCart(Long clientId, CartOperationRequest request) {
        if (request.getProductId() == null && request.getPackId() == null) {
            throw new IllegalArgumentException("Either productId or packId must be provided");
        }

        // Get or create cart
        Cart cart = getCartEntity(clientId);

        // Reset cart if expired
        if (cart.isExpired()) {
            resetExpiredCart(cart);
        }
        // Reset expiration time if the cart is empty (treat like a new shopping session)
        else if (cart.getCartItems().isEmpty()) {
            resetEmptyCart(cart);
        }

        // Add item to cart based on type
        if (request.getProductId() != null) {
            addProductToCart(cart, request.getProductId(), request.getQuantity());
        } else {
            addPackToCart(cart, request.getPackId(), request.getQuantity());
        }

        // Save cart without updating expiration time for active non-empty carts
        cart = cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Update item quantity in cart
     */
    @Transactional
    public CartDTO updateCartItemQuantity(Long clientId, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Get cart
        Cart cart = getCartEntity(clientId);

        // Check if cart is expired
        if (cart.isExpired()) {
            throw new RuntimeException("Cart has expired");
        }

        // Find and update cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify the item belongs to the correct cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to this cart");
        }

        // Calculate difference in quantity
        int quantityDifference = quantity - cartItem.getQuantity();

        // Handle stock updates
        if (quantityDifference != 0) {
            if (cartItem.getProduit() != null) {
                Produits product = cartItem.getProduit();
                // Check if increasing quantity
                if (quantityDifference > 0) {
                    // Check available stock (not in carts)
                    int availableStock = product.getQuantite();
                    if (quantityDifference > availableStock) {
                        throw new RuntimeException("Not enough stock available");
                    }
                    // Reserve additional stock
                    product.setQuantite(product.getQuantite() - quantityDifference);
                } else {
                    // Return excess stock
                    product.setQuantite(product.getQuantite() - quantityDifference); // Negative difference means adding back
                }
                produitRepository.save(product);
            } else if (cartItem.getPaquet() != null) {
                Paquet pack = cartItem.getPaquet();
                // Check if increasing quantity
                if (quantityDifference > 0) {
                    // Check available stock (not in carts)
                    int availableStock = pack.getQuantite();
                    if (quantityDifference > availableStock) {
                        throw new RuntimeException("Not enough stock available");
                    }
                    // Reserve additional stock
                    pack.setQuantite(pack.getQuantite() - quantityDifference);
                } else {
                    // Return excess stock
                    pack.setQuantite(pack.getQuantite() - quantityDifference); // Negative difference means adding back
                }
                paquetRepository.save(pack);
            }
        }

        // Update quantity
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        // Save cart without updating expiration time
        cart = cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Remove an item from the cart
     */
    @Transactional
    public CartDTO removeCartItem(Long clientId, Long cartItemId) {
        // Get cart
        Cart cart = getCartEntity(clientId);

        // Check if cart is expired
        if (cart.isExpired()) {
            throw new RuntimeException("Cart has expired");
        }

        // Find cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify the item belongs to the correct cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to this cart");
        }

        // Return item's quantity to stock
        if (cartItem.getProduit() != null) {
            Produits product = cartItem.getProduit();
            product.setQuantite(product.getQuantite() + cartItem.getQuantity());
            produitRepository.save(product);
        } else if (cartItem.getPaquet() != null) {
            Paquet pack = cartItem.getPaquet();
            pack.setQuantite(pack.getQuantite() + cartItem.getQuantity());
            paquetRepository.save(pack);
        }

        // Remove the item
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        // Save cart
        cart = cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Clear all items from a cart
     */
    @Transactional
    public CartDTO clearCart(Long clientId) {
        // Get cart
        Cart cart = getCartEntity(clientId);

        // Return all items to stock
        returnCartItemsToStock(cart);

        // Remove all items
        cart.getCartItems().clear();

        // Save cart without updating expiration time
        cart = cartRepository.save(cart);

        return convertToDTO(cart);
    }

    /**
     * Clear cart after successful order creation
     * This is meant to be called by the CommandeService after an order is created
     * Note: We don't need to return items to stock as they've been properly purchased
     */
    @Transactional
    public void clearCartAfterOrder(Long clientId) {
        // Find the cart for this client
        Optional<Cart> cartOpt = cartRepository.findByClientId(clientId);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            // Clear all items
            cart.getCartItems().clear();
            // Save the empty cart
            cartRepository.save(cart);
        }
    }

    /**
     * Helper method to add a product to the cart
     */
    private void addProductToCart(Cart cart, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            quantity = 1; // Default quantity
        }

        // Get product
        Produits product = produitRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check availability
        if (!product.isDisponibilite()) {
            throw new RuntimeException("Product is not available");
        }

        // Check stock
        if (product.getQuantite() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // Check if product already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            // Update existing item
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // Validate against stock
            if (quantity > product.getQuantite()) {
                throw new RuntimeException("Not enough stock available");
            }

            // Reserve additional quantity from stock
            product.setQuantite(product.getQuantite() - quantity);
            produitRepository.save(product);

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Create new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduit(product);
            newItem.setPaquet(null);
            newItem.setQuantity(quantity);

            // Reserve quantity from stock
            product.setQuantite(product.getQuantite() - quantity);
            produitRepository.save(product);

            // Add to cart and save
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }

    /**
     * Helper method to add a pack to the cart
     */
    private void addPackToCart(Cart cart, Long packId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            quantity = 1; // Default quantity
        }

        // Get pack
        Paquet pack = paquetRepository.findById(packId)
                .orElseThrow(() -> new RuntimeException("Pack not found"));

        // Check availability
        if (!pack.isDisponibilite()) {
            throw new RuntimeException("Pack is not available");
        }

        // Check stock
        if (pack.getQuantite() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // Check if pack already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndPackId(cart.getId(), packId);

        if (existingItem.isPresent()) {
            // Update existing item
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // Validate against stock
            if (quantity > pack.getQuantite()) {
                throw new RuntimeException("Not enough stock available");
            }

            // Reserve additional quantity from stock
            pack.setQuantite(pack.getQuantite() - quantity);
            paquetRepository.save(pack);

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Create new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduit(null);
            newItem.setPaquet(pack);
            newItem.setQuantity(quantity);

            // Reserve quantity from stock
            pack.setQuantite(pack.getQuantite() - quantity);
            paquetRepository.save(pack);

            // Add to cart and save
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }

    /**
     * Get cart entity by client ID
     */
    private Cart getCartEntity(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return cartRepository.findByClientId(clientId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setClient(client);
                    newCart.setCreatedAt(new Date());
                    newCart.updateExpirationTime(); // Set initial expiration time only for new carts
                    return cartRepository.save(newCart);
                });
    }


    /**
     * Convert Cart entity to CartDTO
     */
    @Transactional
    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getId());
        dto.setClientId(cart.getClient().getId());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setExpiresAt(cart.getExpiresAt());
        dto.setTimeRemainingMinutes(cart.getRemainingMinutes());

        // Check if the client is a partner based on TypeClient enum
        boolean isPartner = TypeClient.Partner.equals(cart.getClient().getType());

        // Convert cart items
        List<CartItemDTO> itemDTOs = new ArrayList<>();
        double total = 0.0;

        for (CartItem item : cart.getCartItems()) {
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setCartItemId(item.getId());
            itemDTO.setQuantity(item.getQuantity());

            // Set item details based on type
            if (item.getProduit() != null) {
                Produits product = item.getProduit();
                itemDTO.setType("PRODUCT");
                itemDTO.setProductId(product.getId());
                itemDTO.setName(product.getNom());
                itemDTO.setUnitPrice(product.getPrix());

                // Set promotion values
                itemDTO.setPromotionPartenaire(product.getPromotionPartenaire());
                itemDTO.setPromotionParticulier(product.getPromotionParticulier());

                // Calculate applied price based on client type
                double appliedPrice = calculateAppliedPrice(product.getPrix(),
                        product.getPromotionPartenaire(),
                        product.getPromotionParticulier(),
                        isPartner);
                itemDTO.setPrixApplique(appliedPrice);

                // Update subtotal based on applied price
                double subtotal = appliedPrice * item.getQuantity();
                itemDTO.setSubtotal(subtotal);
                itemDTO.setStockRemaining(product.getQuantite());

                // Set category if available
                if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                    Categories firstCategory = product.getCategories().iterator().next();
                    itemDTO.setCategory(firstCategory.getNom());
                }

                // Set image if available
                if (product.getPhotos() != null && !product.getPhotos().isEmpty()) {
                    String firstPhoto = product.getPhotos().iterator().next();
                    itemDTO.setImageUrl(firstPhoto);
                }

                // Add to total using applied price
                total += subtotal;
            } else if (item.getPaquet() != null) {
                Paquet pack = item.getPaquet();
                itemDTO.setType("PACK");
                itemDTO.setPackId(pack.getId());
                itemDTO.setName(pack.getNom());
                itemDTO.setUnitPrice(pack.getPrix());

                // Pack does not have promotions, set default values
                itemDTO.setPromotionPartenaire(0);
                itemDTO.setPromotionParticulier(0);

                // No promotions for packs, so applied price is the same as base price
                double appliedPrice = pack.getPrix();
                itemDTO.setPrixApplique(appliedPrice);

                // Update subtotal (price * quantity)
                double subtotal = appliedPrice * item.getQuantity();
                itemDTO.setSubtotal(subtotal);
                itemDTO.setStockRemaining(pack.getQuantite());

                // Set image if available
                if (pack.getPhotos() != null && !pack.getPhotos().isEmpty()) {
                    String firstPhoto = pack.getPhotos().iterator().next();
                    itemDTO.setImageUrl(firstPhoto);
                }

                // Add to total
                total += subtotal;
            }

            itemDTOs.add(itemDTO);
        }

        dto.setItems(itemDTOs);
        dto.setTotal(total);

        return dto;
    }

    /**
     * Helper method to calculate the applied price based on product information and user role
     */
    private double calculateAppliedPrice(double basePrice,
                                         double promotionPartenaire,
                                         double promotionParticulier,
                                         boolean isPartner) {
        // For partners, check partner promotion
        if (isPartner && promotionPartenaire > 0) {
            if (promotionPartenaire > 1) {
                // This is a markup
                return basePrice * promotionPartenaire;
            } else {
                // This is a discount
                return basePrice * (1 - promotionPartenaire);
            }
        }
        // For regular customers, check regular promotion
        else if (!isPartner && promotionParticulier > 0) {
            return basePrice * (1 - promotionParticulier);
        }

        // No promotion applies
        return basePrice;
    }
    /**
     * Scheduled job to clean up expired carts
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredCarts() {
        Date currentTime = new Date();
        List<Cart> expiredCarts = cartRepository.findExpiredCarts(currentTime);

        for (Cart cart : expiredCarts) {
            // Return items to stock before clearing
            returnCartItemsToStock(cart);

            // Clear items but keep cart record
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }
    }
}