package com.jasim0021.blankscreen;// Create a FirebaseMessagingService class
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check the content of the push notification
        if (remoteMessage.getData().containsKey("isBlank")) {
            String isBlankValue = remoteMessage.getData().get("isBlank");
            Boolean isBlank = Boolean.parseBoolean(isBlankValue);

            // Handle the received value of isBlank
            if (isBlank) {
                // Start the service when isBlank is true
                // You can call your startService() method here if needed
            } else {
                // Stop the service when isBlank is false
                // You can call your stopService() method here if needed
            }
        }
    }
}
