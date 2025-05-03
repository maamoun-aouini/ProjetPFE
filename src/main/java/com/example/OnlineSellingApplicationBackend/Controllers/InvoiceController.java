package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.Services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * Generate and download a PDF invoice for an order
     * This endpoint is optimized for mobile downloads via Expo FileSystem
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId) {
        try {
            // Generate the PDF
            byte[] pdfBytes = invoiceService.generateInvoicePdf(orderId);

            // Set up HTTP headers specifically for mobile download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            // Use inline content disposition for direct download
            // Mobile apps handle this better than attachment
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice_" + orderId + ".pdf");

            // These cache headers help with mobile downloads
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            // Set content length for better download handling
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error generating invoice: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}