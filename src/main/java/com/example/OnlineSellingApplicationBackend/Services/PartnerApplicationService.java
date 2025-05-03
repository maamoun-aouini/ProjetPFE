// Create this file: com/example/OnlineSellingApplicationBackend/Services/PartnerApplicationService.java
package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.Repositories.ClientRepository;
import com.example.OnlineSellingApplicationBackend.Repositories.PartnerApplicationRepository;
import com.example.OnlineSellingApplicationBackend.entities.Client;
import com.example.OnlineSellingApplicationBackend.entities.Notification;
import com.example.OnlineSellingApplicationBackend.entities.PartnerApplication;
import com.example.OnlineSellingApplicationBackend.entities.TypeClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartnerApplicationService {

    @Autowired
    private PartnerApplicationRepository partnerApplicationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private NotificationService notificationService;
    @Transactional
    public PartnerApplication createApplication(
            String clientEmail,
            String businessName,
            String businessAddress,
            String businessDescription,
            String contactPerson, // We'll ignore this
            String contactPhone, // We'll ignore this
            String documentPath) {

        // Find client by email
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Check if client already has a pending application
        List<PartnerApplication> existingApplications = partnerApplicationRepository.findByClientId(client.getId());

        if (!existingApplications.isEmpty()) {
            // Check latest application status (assuming applications are ordered by submission date)
            PartnerApplication latestApplication = existingApplications.stream()
                    .max((a1, a2) -> a1.getSubmissionDate().compareTo(a2.getSubmissionDate()))
                    .orElse(null);

            if (latestApplication != null) {
                if (latestApplication.getStatus() == PartnerApplication.ApplicationStatus.PENDING) {
                    throw new RuntimeException("You have already submitted a request, and it is currently being processed. Thank you for your patience!");
                } else if (latestApplication.getStatus() == PartnerApplication.ApplicationStatus.REJECTED) {
                    throw new RuntimeException("Your request has been declined. If you believe this is a mistake or would like more information, please feel free to contact our support team.");
                }
                // If APPROVED, we'll allow them to continue (shouldn't happen normally)
            }
        }

        // Create new application
        PartnerApplication application = new PartnerApplication();
        application.setClient(client);
        application.setBusinessName(businessName);
        application.setBusinessAddress(businessAddress);
        application.setBusinessDescription(businessDescription);
        application.setContactPerson(client.getNom()); // Use client's name from DB
        application.setContactPhone(client.getTel()); // Use client's phone from DB
        application.setDocumentPath(documentPath);
        PartnerApplication result = partnerApplicationRepository.save(application);
        notificationService.sendNotification(
                "New Partner Application",
                "A new partner application has been submitted by " + client.getNom(),
                Notification.NotificationType.PARTNER_APPLICATION,
                "admin",
                application.getId()
        );
        return result;
    }

    public List<PartnerApplication> getPendingApplications() {
        return partnerApplicationRepository.findByStatus(PartnerApplication.ApplicationStatus.PENDING);
    }

    @Transactional
    public Client approveApplication(Long applicationId) {
        PartnerApplication application = partnerApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Update application status
        application.setStatus(PartnerApplication.ApplicationStatus.APPROVED);
        application.setReviewDate(LocalDateTime.now());
        partnerApplicationRepository.save(application);

        // Update client to partner status
        Client client = application.getClient();
        client.setType(TypeClient.Partner);
        Client result = clientRepository.save(client);

        // Notify about application status change
        notificationService.notifyPartnerApplicationStatusChange(
                applicationId,
                client.getId().toString(),
                "APPROVED"
        );

        // IMPORTANT: Send role change notification
        notificationService.notifyRoleChange(
                client.getId().toString(),
                "Partner"
        );

        return result;
    }
    @Transactional
    public PartnerApplication rejectApplication(Long applicationId, String rejectionReason) {
        PartnerApplication application = partnerApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(PartnerApplication.ApplicationStatus.REJECTED);
        application.setRejectionReason(rejectionReason);
        application.setReviewDate(LocalDateTime.now());
        PartnerApplication result = partnerApplicationRepository.save(application);
        notificationService.notifyPartnerApplicationStatusChange(
                applicationId,
                application.getClient().getId().toString(),
                "REJECTED"
        );
        return result;
    }
    // Helper method to check application status for a client
    public PartnerApplication.ApplicationStatus getClientApplicationStatus(Long clientId) {
        List<PartnerApplication> applications = partnerApplicationRepository.findByClientId(clientId);
        if (applications.isEmpty()) {
            return null;
        }

        // Get the latest application by submission date
        return applications.stream()
                .max((a1, a2) -> a1.getSubmissionDate().compareTo(a2.getSubmissionDate()))
                .map(PartnerApplication::getStatus)
                .orElse(null);
    }
}