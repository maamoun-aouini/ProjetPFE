package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.DTO.PaymentIntentDTO;
import com.example.OnlineSellingApplicationBackend.DTO.PaymentRequest;
import com.example.OnlineSellingApplicationBackend.DTO.PaymentResponse;
import com.example.OnlineSellingApplicationBackend.Services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Create a payment intent without an order yet
     */
    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentDTO> createPaymentIntent(@RequestBody Map<String, Object> request) {
        Double amount = Double.parseDouble(request.get("amount").toString());
        PaymentIntentDTO intent = paymentService.createStandalonePaymentIntent(amount);
        return ResponseEntity.ok(intent);
    }

    /**
     * Process a payment intent before order creation
     */
    @PostMapping("/process-intent")
    public ResponseEntity<PaymentResponse> processPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.processStandalonePayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a payment intent for an existing order
     */
    @PostMapping("/intent/{orderId}")
    public ResponseEntity<PaymentIntentDTO> createOrderPaymentIntent(@PathVariable Long orderId) {
        PaymentIntentDTO intent = paymentService.createPaymentIntent(orderId);
        return ResponseEntity.ok(intent);
    }

    /**
     * Process a payment for an existing order
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processOrderPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment details for an order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(@PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentDetails(orderId);
        return ResponseEntity.ok(response);
    }
}