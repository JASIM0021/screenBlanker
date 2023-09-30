package com.jasim0021.blankscreen;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // Your code to handle device admin being enabled (e.g., show a message).
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);
        // Prevent power off
//        devicePolicyManager.setCameraDisabled(adminComponent, true);


    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        // Your code to handle device admin being disabled (e.g., show a message).
    }
}
