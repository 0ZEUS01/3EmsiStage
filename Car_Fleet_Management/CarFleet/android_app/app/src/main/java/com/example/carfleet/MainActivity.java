package com.example.carfleet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Button btnRegister;
    private EditText txtRegistrationPlate;
    private EditText txtCarName;

    private static String static_api_ip = "http://sbapi.ddns.net:8082";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean isLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegistrationPlate = findViewById(R.id.txtRegistrationPlate);
        txtCarName = findViewById(R.id.txtCarName);
        btnRegister = findViewById(R.id.btnRegister);


        // Check if location permission is granted or not
        isLocationPermissionGranted = checkLocationPermission();

        // Request location permission if not granted
        if (!isLocationPermissionGranted) {
            requestLocationPermission();
        }

        showToast("Activate Locations !!!!");
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a LocationCallback object
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                // Get the last known location from the location result
                Location location = locationResult.getLastLocation();

                // Call the onLocationChanged method to update the car's location
                onLocationChanged(location);
            }
        };

        // Request location updates
        fusedLocationClient.requestLocationUpdates(getLocationRequest(), locationCallback, null);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registrationPlate = txtRegistrationPlate.getText().toString();
                String carName = txtCarName.getText().toString();

                // Create the JSON data to send
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("registrationPlate", registrationPlate);
                    jsonObject.put("nameCar", carName);
                    jsonObject.put("isDeleted", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(e.toString());
                }
                showToast("Registration Plate : " + registrationPlate + " Car Name : " + carName);
                // Call the sendPostRequest method
                sendPostRequest("/api/cars", jsonObject, new RequestCallback() {
                    @Override
                    public void onRequestCompleted(boolean success) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    // Request succeeded, handle success scenario
                                    showToast("Car registration successful!");

                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                                    carJson.put("registrationPlate", txtRegistrationPlate.getText().toString());
                                                    carJson.put("nameCar", txtCarName.getText().toString());
                                                    carJson.put("isDeleted", false);
                                                    // Add the car JSON object to the location JSON object
                                                    locationJson.put("car", carJson);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                // Send the second POST request to create a location
                                                sendPostRequest("/api/locations", locationJson, new RequestCallback(){
                                                    @Override
                                                    public void onRequestCompleted(boolean success){
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run(){
                                                                if (success){
                                                                    // Request succeeded, handle success scenario
                                                                    showToast("Car location created successfully!");

                                                                    // Location created successfully, now start receiving location updates
                                                                    showToast("Start receiving location updates.");
                                                                }else{
                                                                    // Request failed, handle failure scenario
                                                                    showToast("Failed to create car location.");
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
                                } else {
                                    // Request failed, handle failure scenario
                                    showToast("Failed to register car.");
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update interval in milliseconds (e.g., every 10 seconds)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {
        // The implementation here is the same as mentioned in the previous response
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            JSONObject locationJson = new JSONObject();

            try {
                locationJson.put("latitude", latitude);
                locationJson.put("longitude", longitude);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Now that the car is registered, send additional data
            sendPutLocationRequest("/api/locations/", txtRegistrationPlate.getText().toString(), locationJson, new RequestCallback(){
                @Override
                public void onRequestCompleted(boolean success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run(){
                            if (success){
                                // Request succeeded, handle success scenario
                                showToast("Car location updated successfully!");
                            }else{
                                // Request failed, handle failure scenario
                                showToast("Failed to update car location.");
                            }
                        }
                    });
                }
            });
        } else {
            showToast("Failed to get location. Ensure location is enabled.");
        }
    }


    private void sendPostRequest(final String API_ENDPOINT, final JSONObject jsonObject, final RequestCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

                Request request = new Request.Builder()
                        .url(static_api_ip + API_ENDPOINT)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    // Handle the response if needed
                    if (response.isSuccessful()) {
                        // Request successful
                        String responseBody = response.body().string();
                        // Do something with the response
                        // ...

                        // Notify the callback of successful request
                        callback.onRequestCompleted(true);
                    } else {
                        // Request failed
                        // Handle the error
                        // ...

                        // Notify the callback of failed request
                        callback.onRequestCompleted(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception
                    // ...

                    // Notify the callback of failed request
                    callback.onRequestCompleted(false);
                }
            }
        });

        // Shutdown the executor to release resources after the task is done (optional).
        executor.shutdown();
    }

    private void sendPutLocationRequest(final String API_ENDPOINT, final String plate, final JSONObject jsonObject, final RequestCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

                Request request = new Request.Builder()
                        .url(static_api_ip + API_ENDPOINT + plate)
                        .put(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    // Handle the response if needed
                    if (response.isSuccessful()) {
                        // Request successful
                        String responseBody = response.body().string();
                        // Do something with the response
                        showToast(" UPDATED ");

                        // Notify the callback of successful request
                        callback.onRequestCompleted(true);
                    } else {
                        // Request failed
                        // Handle the error
                        showToast("NOT UPDATED ERROR");

                        // Notify the callback of failed request
                        callback.onRequestCompleted(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception
                    showToast("ERROR");

                    // Notify the callback of failed request
                    callback.onRequestCompleted(false);
                }
            }
        });

        // Shutdown the executor to release resources after the task is done (optional).
        executor.shutdown();
    }

    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    // Method to check if location permission is granted
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    // Method to request location permission
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    // Override onRequestPermissionsResult to handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission is granted
                isLocationPermissionGranted = true;
                // You can now proceed with using the location
            } else {
                // Location permission is denied
                showToast("Location permission is required to use this app.");
                // You can handle what to do if the permission is denied (e.g., disable location-related features)
            }
        }
    }
}
