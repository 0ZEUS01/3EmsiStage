package com.example.carfleetdatasender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    private static final String SHARED_PREFS_KEY = "com.example.carfleet.sharedprefs";
    private static final String REGISTRATION_PLATE_KEY = "registration_plate";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BOOT", "Boot completed");

        Toast.makeText(context, "Boot completed", Toast.LENGTH_LONG).show();

        // Retrieve Registration Plate And Car Name
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String savedRegistrationPlate = sharedPreferences.getString(REGISTRATION_PLATE_KEY, "");

        Log.d("Chekcing RP", savedRegistrationPlate);

        // Check if the savedRegistrationPlate is empty or not
        if (savedRegistrationPlate.isEmpty()) {
            Toast.makeText(context, "App Should Be Configured", Toast.LENGTH_LONG).show();
        } else {
            Log.d("BootReceiver", "Starting BackgroundLocationService");
            Intent serviceIntent = new Intent(context, BackgroundLocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
