package com.example.carfleetdatasender;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class BackgroundLocationService extends Service {

    private static final String TAG = "BackgroundLocation";
    private static final int NOTIFICATION_ID = 123;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_KEY = "com.example.carfleet.sharedprefs";
    private static final String REGISTRATION_PLATE_KEY = "registration_plate";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
        startLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Background Location Service")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(8000); // 5 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(40); // 40 meters
    }

    // Create the location callback
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // You can handle the location updates here
                    Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());

                    // Displaying a message
                    showLocationToast(location.getLatitude(), location.getLongitude());

                    // Call your custom method to handle the location change event
                    onLocationChange(location);
                }
            }
        };
    }

    private void showLocationToast(double latitude, double longitude) {
        String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
        showToast(message);
    }

    private void onLocationChange(Location location) {
        // Your custom logic here to handle the location change event
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Show a toast with the new location coordinates
        String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
        showToast(message);

        // Sending PUT request to update the location in the DataBase
        RequestSender RS = new RequestSender();

        JSONObject locationJson = new JSONObject();
        try {
            locationJson.put("latitude", latitude);
            locationJson.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Retrieve Registration Plate And Car Name
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);
        String savedRegistrationPlate = sharedPreferences.getString(REGISTRATION_PLATE_KEY, "");

        showToast("This Is The Stored Registration Plate : " + savedRegistrationPlate);

        RS.sendPutLocationRequest("/api/locations/", savedRegistrationPlate, locationJson, new RequestCallback() {
            @Override
            public void onRequestCompleted(boolean success) {
                if (success) {
                    showToast("Car location updated successfully!");
                } else {
                    showToast("Failed to update car location.");
                }
            }
        });
    }

    // Start location updates
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission request here, if needed.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Log.d("BACKGROUND_SERVICE", "Service started.");
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // For Android 8.0 (API level 26) and above, create a notification channel
    private String createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "location_service_channel";
            String channelName = "Background Location Service";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            return channelId;
        }
        return null;
    }



    // Helper method to show messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
