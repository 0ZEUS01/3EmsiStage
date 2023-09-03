package com.example.carfleetdatasender;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestSender {
    private static String static_api_ip = "http://sbapi.ddns.net:8082";

    public void sendPostRequest(final String API_ENDPOINT, final JSONObject jsonObject, final RequestCallback callback) {
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
                    if (response.isSuccessful()) {
                        callback.onRequestCompleted(true);
                    } else {
                        callback.onRequestCompleted(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onRequestCompleted(false);
                }
            }
        });

        // Shutdown the executor to release resources after the task is done (optional).
        executor.shutdown();
    }

    public void sendPutLocationRequest(final String API_ENDPOINT, final String plate, final JSONObject jsonObject, final RequestCallback callback) {
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
                    if (response.isSuccessful()) {
                        callback.onRequestCompleted(true);
                    } else {
                        callback.onRequestCompleted(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onRequestCompleted(false);
                }
            }
        });

        // Shutdown the executor to release resources after the task is done (optional).
        executor.shutdown();
    }

    public void checkApiResponse(final String registrationPlate, final ApiResponseCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = static_api_ip + "/api/locations/" + registrationPlate;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    boolean responseAvailable = !responseBody.isEmpty();
                    callback.onResponseAvailable(responseAvailable);
                } else {
                    // Handle the non-successful response, e.g., show an error message
                    callback.onResponseAvailable(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                callback.onResponseAvailable(false);
            }
        });
    }

    public interface ApiResponseCallback {
        void onResponseAvailable(boolean responseAvailable);
    }
}
