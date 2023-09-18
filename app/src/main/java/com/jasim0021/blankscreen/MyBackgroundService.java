package com.jasim0021.blankscreen;



import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyBackgroundService extends Service {
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private DatabaseReference dbIsblank,dbIsHide,dbIsLock;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdminComponent;

    private ValueEventListener valueEventListener;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeReceiver, filter);
        // Initialize Firebase Database reference
        dbIsblank = FirebaseDatabase.getInstance().getReference("isBlank");
        dbIsHide = FirebaseDatabase.getInstance().getReference("isHide");
        dbIsLock = FirebaseDatabase.getInstance().getReference("isLock");

        // Create a DevicePolicyManager instance
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);



        // Create a ValueEventListener to listen for changes
        dbIsblank.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isBlank = snapshot.getValue(Boolean.class);
                if (isBlank != null && isBlank) {
                    // Start the service when isBlank is true
                    startService();
                } else {
                    // Stop the service when isBlank is false
                    stopService();
//                    Window window = new Window();
                }
//                sendPushNotification(isBlank);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("dbError",error.toString());
            }




        });
        dbIsHide.addValueEventListener(new ValueEventListener() {
            PackageManager packageManager = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName(), "com.jasim0021.blankscreen.MainActivity");


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isHide = snapshot.getValue(Boolean.class);
                if (isHide != null && isHide) {
                    packageManager.setComponentEnabledSetting(
                            componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                    );

                } else {
                     packageManager.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
            );
////                    Window window = new Window();
                }
                Log.d("IsHidenChange", isHide.toString());
//                sendPushNotification(isBlank);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("dbError",error.toString());
            }




        });
        dbIsLock.addValueEventListener(new ValueEventListener() {



            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isLock = snapshot.getValue(Boolean.class);
                if (isLock != null && isLock) {
                    lockDevice();

                } else {

////                    Window window = new Window();
                }
                Log.d("IsHidenChange", isLock.toString());
//                sendPushNotification(isBlank);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("dbError",error.toString());
            }




        });

    }

    private BroadcastReceiver airplaneModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                boolean isAirplaneModeOn = Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

                // Handle airplane mode change (e.g., start/stop your background tasks).
                if (isAirplaneModeOn) {
//                    Toast.makeText(context, "Airplane mode is on", Toast.LENGTH_SHORT).show();

                    // Create an Intent to launch your app's main activity
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.jasim0021.blankscreen");

                    if (launchIntent != null) {
                        // Start your app's main activity
                        context.startActivity(launchIntent);
                    } else {
                        // Handle the case where the app is not installed or the launch intent is null
                        // You can prompt the user to install the app or take appropriate action
                        Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Airplane mode is off.
                    Toast.makeText(context, "AirPlane mode is off", Toast.LENGTH_SHORT).show();
                    // Perform actions as needed.
                }
            }
        }
    };
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
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // This flag will make the service restart if killed by the system
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the ValueEventListener when the service is destroyed

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void lockDevice() {
        if (devicePolicyManager.isAdminActive(deviceAdminComponent)) {
            devicePolicyManager.lockNow(); // Lock the device immediately
        } else {
            // Device admin not enabled; handle this case accordingly
        }
    }


}
