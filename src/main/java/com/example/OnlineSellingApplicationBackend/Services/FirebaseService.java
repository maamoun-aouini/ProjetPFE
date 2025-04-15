package com.example.OnlineSellingApplicationBackend.Services;

import com.example.OnlineSellingApplicationBackend.Repositories.ClientRepository;
import com.example.OnlineSellingApplicationBackend.entities.Client;
import com.example.OnlineSellingApplicationBackend.entities.Notification;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class    FirebaseService {

    @Autowired
    private ClientRepository clientRepository;

    @PostConstruct
    public void initialize() {
        try {
            // Initialize Firebase App with your service account credentials
            // You need to place your firebase-service-account.json in src/main/resources
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource("firebase-service-account.json").getInputStream()))
                    .build();

            // Check if Firebase app is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
        }
    }

    // Send push notification to a specific client
    public void sendPushNotification(String clientId, String title, String body,
                                     Notification.NotificationType type, Long referenceId) {
        try {
            // 1. Get client's FCM token from database
            String fcmToken = getFcmToken(clientId);

            if (fcmToken == null || fcmToken.isEmpty()) {
                System.out.println("No FCM token available for client: " + clientId);
                return;
            }

            // 2. Create notification message
            Message message = createMessage(fcmToken, title, body, type, referenceId);

            // 3. Send message via Firebase
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent notification: " + response);

        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send push notification: " + e.getMessage());
        }
    }

    // Get client's FCM token from database
    private String getFcmToken(String clientId) {
        try {
            Long id = Long.parseLong(clientId);
            Optional<Client> clientOpt = clientRepository.findById(id);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                // Assuming you've added a fcmToken field to your Client entity
                return client.getFcmToken();
            }
        } catch (NumberFormatException e) {
            // Handle case where clientId is not a valid number
            System.err.println("Invalid client ID format: " + clientId);
        }

        return null;
    }

    // Create a Firebase message with the notification
    private Message createMessage(String token, String title, String body,
                                  Notification.NotificationType type, Long referenceId) {

        // Create notification
        com.google.firebase.messaging.Notification notification =
                com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

        // Create custom data payload
        Map<String, String> data = new HashMap<>();
        data.put("type", type.toString());
        if (referenceId != null) {
            data.put("referenceId", referenceId.toString());
        }

        // Build the message
        return Message.builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setClickAction("OPEN_ACTIVITY_1")
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setCategory("NOTIFICATION")
                                .setSound("default")
                                .build())
                        .build())
                .build();
    }
}