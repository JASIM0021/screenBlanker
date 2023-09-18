package com.jasim0021.blankscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.jasim0021.blankscreen.MainActivity;

public class AirplaneModeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Airplane mode is on", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Check if airplane mode is enabled
            if (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0) {
                // Airplane mode is on, start your background service
                startBackgroundService(context);
            }
            Log.d("OnRecived", "onReceive: ");
        } else {
            // For devices with older Android versions
            if (Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
                // Airplane mode is on, start your background service
                startBackgroundService(context);
            }
            Log.d("OnRecived", "onReceive: ");

        }
    }

    private void startBackgroundService(Context context) {
        // Start your background service here
        Intent serviceIntent = new Intent(context, MyBackgroundService.class);
        context.startService(serviceIntent);
    }
}
