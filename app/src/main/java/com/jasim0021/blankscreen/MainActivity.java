package com.jasim0021.blankscreen;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdminComponent;

    // Method to send a push notification
    private void sendPushNotification(Boolean isBlank) {
        // Create a new FCM message
        RemoteMessage.Builder messageBuilder = new RemoteMessage.Builder("your_fcm_token");
        messageBuilder.addData("isBlank", String.valueOf(isBlank));

        // Send the FCM message
        FirebaseMessaging.getInstance().send(messageBuilder.build());
    }
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Mainactivity111", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkOverlayPermission();
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        startService(serviceIntent);
        FirebaseApp.initializeApp(this);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("isBlank");



        // Add a ValueEventListener to listen for changes in 'isBlank'
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Boolean isBlank = snapshot.getValue(Boolean.class);
//                if (isBlank != null && isBlank) {
//                    // Start the service when isBlank is true
//                    startService();
//                } else {
//                    // Stop the service when isBlank is false
//                    stopService();
////                    Window window = new Window();
//                }
////                sendPushNotification(isBlank);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("dbError",error.toString());
//            }
//
//
//
//
//        });
//        checkOverlayPermission();

        Button startBtn;
        startBtn = findViewById(R.id.start_window);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOverlayPermission();
                startService();
                requestDeviceAdminActivation();

            }
        });

    }
    // method for starting the service
        public void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the user has granted the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // Stop the service if it's running
                stopService(new Intent(this, ForegroundService.class));

                // Start the service based on the Android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, ForegroundService.class));
                } else {
                    startService(new Intent(this, ForegroundService.class));
                }
            }
        } else {
            // Stop the service if it's running
            stopService();
        }
    }

    public  void stopService(){
        stopService(new Intent(this, ForegroundService.class));
        startService(new Intent(this, ForegroundService.class));
    }

    // method to ask user to grant the Overlay permission
    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }
    // the device settings, and start the service
    @Override
    protected void onResume() {
        super.onResume();
//        startService();
    }
    private void requestDeviceAdminActivation() {
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);

        // Check if your app is already a device administrator
        if (!devicePolicyManager.isAdminActive(deviceAdminComponent)) {
            // If not, request activation
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponent);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin to use this feature.");
            startActivityForResult(intent, 1);
        } else {
            // Your app is already a device administrator; no need to request activation again.
            // You can perform admin-related tasks here.
            Toast.makeText(this, "Alrady enable admin permission", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        int keyCode = event.getKeyCode();
        // Intercept the power key events and prevent them
        if (keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_SLEEP) {
            // Do nothing or show a message indicating that the action is disabled
            return true; // Event handled
        }
        return super.dispatchKeyEvent(event);
    }
}