package com.example.carfleetdatasender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button btnRegister;
    private EditText txtRegistrationPlate;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean isLocationPermissionGranted = false;


    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_KEY = "com.example.carfleet.sharedprefs";
    private static final String REGISTRATION_PLATE_KEY = "registration_plate";
    private static final String REG_BUTTON = "true";


    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRegister = findViewById(R.id.btnRegister);
        txtRegistrationPlate = findViewById(R.id.txtRegistrationPlate);

        isLocationPermissionGranted = checkLocationPermission();
        if (!isLocationPermissionGranted) {
            requestLocationPermission();
        }

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Retrieve Registration Plate And Car Name
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);
        String savedRegistrationPlate = sharedPreferences.getString(REGISTRATION_PLATE_KEY, "");
        String savedRegisterButton = sharedPreferences.getString(REG_BUTTON, "");

        // Setting The Values To The Inputs
        txtRegistrationPlate.setText(savedRegistrationPlate);
        if(!savedRegisterButton.isEmpty()){
            btnRegister.setEnabled(Boolean.valueOf(savedRegisterButton));
        }


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registrationPlate = txtRegistrationPlate.getText().toString();
                if (registrationPlate.isEmpty()) {
                    showToast("Please Fill The Inputs");
                } else {
                    // Saving The Values
                    saveUserInput(registrationPlate);

                    showToast("Car Registration Plate : " + registrationPlate);

                    // Log message to track the start of the HTTP request
                    Log.d("API_REQUEST", "Sending data to the api...");

                    RequestSender RS = new RequestSender();

                    if (RS.checkApiResponse(registrationPlate)) {
                        // Register The Car Location In The DataBase Using The API
                        registerLocation(registrationPlate);
                    } else {
                        // Location created successfully, now start receiving location updates
                        showToast("Start receiving location updates.");

                        // Start the LocationForegroundService when the activity is created
                        Intent serviceIntent = new Intent(MainActivity.this, BackgroundLocationService.class);
                        startService(serviceIntent);
                    }
                }
            }
        });
    }
    private void registerLocation(String registrationPlate) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Now that the car is registered, send additional data
                    JSONObject locationJson = new JSONObject();
                    try {
                        locationJson.put("id", null);
                        locationJson.put("latitude", latitude);
                        locationJson.put("longitude", longitude);
                        locationJson.put("date", "2023-07-18");
                        locationJson.put("time", "09:48:54");

                        // Create the JSON object for the car within the location data
                        JSONObject carJson = new JSONObject();
                        carJson.put("id", null);
                        carJson.put("registrationPlate", registrationPlate);
                        carJson.put("nameCar", "RANDOMTEXT");
                        carJson.put("isDeleted", false);
                        // Add the car JSON object to the location JSON object
                        locationJson.put("car", carJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestSender RS = new RequestSender();

                    // Send the second POST request to create a location
                    RS.sendPostRequest("/api/locations", locationJson, new RequestCallback(){
                        @Override
                        public void onRequestCompleted(boolean success){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run(){
                                    if (success){
                                        // Disable register button

                                        saveButtonRegister("false");
                                        btnRegister.setEnabled(false);

                                        // Request succeeded, handle success scenario
                                        showToast("Car location created successfully!");
                                        Log.d("API_RESPONSE", "Car location created successful!");

                                        // Location created successfully, now start receiving location updates
                                        showToast("Start receiving location updates.");


                                        // Start the LocationForegroundService when the activity is created
                                        Intent serviceIntent = new Intent(MainActivity.this, BackgroundLocationService.class);
                                        startService(serviceIntent);
                                    }else{
                                        // Request failed, handle failure scenario
                                        showToast("Failed to create car location.");
                                        Log.d("API_RESPONSE", "Failed to register car location.");
                                    }
                                }
                            });
                        }
                    });
                } else {
                    showToast("Failed to get location. Ensure location is enabled.");
                }
            }
        });
    }

    // Method to check if location permission is granted
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }
    private void saveUserInput(String registrationPlate) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REGISTRATION_PLATE_KEY, registrationPlate);
        editor.apply();
    }
    private void saveButtonRegister(String isLocked){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REG_BUTTON, isLocked);
        editor.apply();
    }

    // Helper method to show messages
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}