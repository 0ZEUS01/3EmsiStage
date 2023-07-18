package com.example.carfleet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Button btnRegister;
    private EditText txtRegistrationPlate;
    private EditText txtCarName;

    private String static_api_ip = "http://sbapi.ddns.net:8082";

    private final OkHttpClient client = new OkHttpClient();
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtRegistrationPlate = findViewById(R.id.txtRegistrationPlate);
        txtCarName = findViewById(R.id.txtCarName);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize the LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registrationPlate = txtRegistrationPlate.getText().toString();
                String carName = txtCarName.getText().toString();

                // Create a JSON object to hold the car information
                JSONObject carJson = new JSONObject();
                try {
                    carJson.put("registrationPlate", registrationPlate);
                    carJson.put("nameCar", carName);
                    carJson.put("isDeleted", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Create the request body with the JSON data
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(JSON, carJson.toString());

                // Create the request with the endpoint URL
                String endpointUrl = static_api_ip + "/api/cars";
                Request request = new Request.Builder()
                        .url(endpointUrl)
                        .post(requestBody)
                        .build();

                // Make the request asynchronously
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Failed to send data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        int statusCode = response.code();
                        String responseBody = response.body().string();

                        // Print the response details for debugging
                        Log.d("API Response", "Status Code: " + statusCode);
                        Log.d("API Response", "Response Body: " + responseBody);

                        // Handle the response based on the status code
                        if (response.isSuccessful()) {
                            // The request was successful
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Car registered successfully", Toast.LENGTH_SHORT).show();

                                    // Request location updates
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            == PackageManager.PERMISSION_GRANTED) {
                                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, MainActivity.this, null);
                                    } else {
                                        // Request location permission
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                1);
                                    }
                                }
                            });
                        } else {
                            // The request failed
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Car not registered", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        response.close();
                    }
                });
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Use the latitude and longitude values in your API call or further processing
        Toast.makeText(this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();

        // Create the JSON object for location data
        JSONObject locationJson = new JSONObject();
        try {
            locationJson.put("id", null);
            locationJson.put("latitude", latitude);
            locationJson.put("longitude", longitude);
            locationJson.put("date", null);
            locationJson.put("time", null);

            // Create the JSON object for the car within the location data
            JSONObject carJson = new JSONObject();
            carJson.put("id", null);
            carJson.put("registrationPlate", txtRegistrationPlate.getText().toString());
            carJson.put("nameCar", txtCarName.getText().toString());
            carJson.put("isDeleted", false);

            // Add the car JSON object to the location JSON object
            locationJson.put("car", carJson);

            // Create the request body with the location JSON data
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody locationRequestBody = RequestBody.create(JSON, locationJson.toString());

            // Create the request with the location endpoint URL
            String locationEndpointUrl = static_api_ip + "/api/locations";
            Request locationRequest = new Request.Builder()
                    .url(locationEndpointUrl)
                    .post(locationRequestBody)
                    .build();

            // Make the location request asynchronously
            client.newCall(locationRequest).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to send location data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // The location request was successful
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Location data sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // The location request failed
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Failed to send location data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    response.close();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Implement other LocationListener methods (onStatusChanged, onProviderEnabled, onProviderDisabled)
    // ...

    // Override onRequestPermissionsResult to handle location permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
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
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
