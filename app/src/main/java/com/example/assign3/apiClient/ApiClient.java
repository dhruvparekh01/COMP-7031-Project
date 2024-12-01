package com.example.assign3.apiClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.assign3.apiClient.model.ClientDetails;
import com.example.assign3.R;
import com.example.assign3.apiClient.model.Client;
import com.example.assign3.apiClient.model.LoginResponse;
import com.example.assign3.apiClient.model.PostClientResponse;
import com.example.assign3.apiClient.model.SignupResponse;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String SERVER_HOSTNAME = "http://10.0.2.2:5000";
    private static final String SIGNUP_ENDPOINT = "/signup";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private static ApiClient instance;

    private ApiClient() {}

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    private RequestBody getAuthRequestBody(String username, String password) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        return RequestBody.create(jsonObject.toString(), JSON);
    }

    public CompletableFuture<SignupResponse> signUpUser(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            SignupResponse response = null;
            try {
                RequestBody body = getAuthRequestBody(username, password);
                Request request = new Request.Builder()
                        .url(SERVER_HOSTNAME + SIGNUP_ENDPOINT)
                        .post(body)
                        .build();
                try (Response httpResponse = client.newCall(request).execute()) {
                    JSONObject responseJson = new JSONObject(httpResponse.body().string());
                    if (httpResponse.isSuccessful() && httpResponse.code() == 201) {
                        response = new SignupResponse(responseJson.getString("message"), responseJson.getString("username"), 201);
                    } else {
                        response = new SignupResponse(responseJson.getString("message"), null, httpResponse.code());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = new SignupResponse("Error connecting to server", null, 500);
            }
            return response;
        });
    }

    public CompletableFuture<LoginResponse> loginUser(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            LoginResponse response = null;
            try {
                RequestBody body = getAuthRequestBody(username, password);
                Request request = new Request.Builder()
                        .url(SERVER_HOSTNAME + LOGIN_ENDPOINT)
                        .post(body)
                        .build();
                try (Response httpResponse = client.newCall(request).execute()) {
                    JSONObject responseJson = new JSONObject(httpResponse.body().string());
                    if (httpResponse.isSuccessful() && httpResponse.code() == 200) {
                        response = new LoginResponse(responseJson.getString("message"), responseJson.getString("token"), 200);
                    } else {
                        response = new LoginResponse(responseJson.getString("message"), null, httpResponse.code());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = new LoginResponse("Error connecting to server", null, 500);
            }
            return response;
        });
    }

    public CompletableFuture<List<Client>> getClients(String token) {
        return CompletableFuture.supplyAsync(() -> {
            List<Client> clients = new ArrayList<>();
            try {
                Request request = new Request.Builder()
                        .url(SERVER_HOSTNAME + "/clients")
                        .addHeader("Authorization", token)
                        .build();
                try (Response httpResponse = client.newCall(request).execute()) {
                    if (httpResponse.isSuccessful() && httpResponse.code() == 200) {
                        JSONArray responseJson = new JSONArray(httpResponse.body().string());
                        for (int i = 0; i < responseJson.length(); i++) {
                            JSONObject clientJson = responseJson.getJSONObject(i);
                            clients.add(new Client(
                                    covertToBitmap(clientJson.getString("thumbnail")),
                                    Integer.parseInt(clientJson.getString("client_id")),
                                    clientJson.getString("first_name"),
                                    clientJson.getString("last_name"),
                                    clientJson.getString("address"),
                                    clientJson.getInt("age"),
                                    clientJson.getString("status")
                                    )
                            );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clients;
        });
    }

    public CompletableFuture<ClientDetails> getClientDetails(int clientId, String token) {
        return CompletableFuture.supplyAsync(() -> {
            ClientDetails item = null;
            try {
                Request request = new Request.Builder()
                        .url(SERVER_HOSTNAME + "/clients/" + clientId)
                        .addHeader("Authorization", token)
                        .build();
                try {
                    Response httpResponse = client.newCall(request).execute();
                    if (httpResponse.isSuccessful() && httpResponse.code() == 200) {
                        JSONObject clientJson = new JSONObject(httpResponse.body().string());
                        item = new ClientDetails(
                                covertToBitmap(clientJson.getString("photo")),
                                covertToBitmap(clientJson.getString("thumbnail")),
                                clientJson.getString("first_name"),
                                clientJson.getString("last_name"),
                                clientJson.getString("address"),
                                clientJson.getString("status"),
                                clientJson.getInt("age"),
                                clientJson.getString("email"),
                                clientJson.getString("phone")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return item;
        });
    }

    public CompletableFuture<ClientDetails> updateClientStatus(int clientId, String token, String status) {
        return CompletableFuture.supplyAsync(() -> {
            ClientDetails item = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", status);
                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                Request request = new Request.Builder()
                        .url(SERVER_HOSTNAME + "/clients/" + clientId)
                        .addHeader("Authorization", token)
                        .patch(body)
                        .build();
                try (Response httpResponse = client.newCall(request).execute()) {
                    if (httpResponse.isSuccessful() && httpResponse.code() == 200) {
                        JSONObject responseJson = new JSONObject(httpResponse.body().string());
                        JSONObject clientJson = responseJson.getJSONObject("client");
                        JSONObject clientDetailsJson = clientJson.getJSONObject("client_details");
                        item = new ClientDetails(
                                covertToBitmap(clientJson.getString("photo")),
                                covertToBitmap(clientJson.getString("thumbnail")),
                                clientJson.getString("first_name"),
                                clientJson.getString("last_name"),
                                clientJson.getString("address"),
                                clientJson.getString("status"),
                                clientDetailsJson.getInt("age"),
                                clientDetailsJson.getString("email"),
                                clientDetailsJson.getString("phone")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return item;
        });
    }

    public CompletableFuture<PostClientResponse> postClientDetails(ClientDetails clientDetails, String token) {
        return CompletableFuture.supplyAsync(() -> {
            ClientDetails item = null;
            int responseCode = 500;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("first_name", clientDetails.getFirstName());
                jsonObject.put("last_name", clientDetails.getLastName());
                jsonObject.put("address", clientDetails.getAddress());
                jsonObject.put("status", clientDetails.getStatus());
                jsonObject.put("age", clientDetails.getAge());
                jsonObject.put("email", clientDetails.getEmail());
                jsonObject.put("phone", clientDetails.getPhone());
                jsonObject.put("photo", convertToString(clientDetails.getDecodedBitmap()));
                jsonObject.put("thumbnail", convertToString(Bitmap.createScaledBitmap(clientDetails.getDecodedBitmap(), 100, 100, false)));
                RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                Request request = new Request.Builder()
                        .url(SERVER_HOSTNAME + "/clients")
                        .addHeader("Authorization", token)
                        .post(body)
                        .build();
                try (Response httpResponse = client.newCall(request).execute()) {
                    responseCode = httpResponse.code();
                    if (httpResponse.isSuccessful() && httpResponse.code() == 200) {
                        JSONObject clientJson = new JSONObject(httpResponse.body().string());
                        item = new ClientDetails(
                                covertToBitmap(clientJson.getString("photo")),
                                covertToBitmap(clientJson.getString("thumbnail")),
                                clientJson.getString("first_name"),
                                clientJson.getString("last_name"),
                                clientJson.getString("address"),
                                clientJson.getString("status"),
                                clientJson.getInt("age"),
                                clientJson.getString("email"),
                                clientJson.getString("phone")
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new PostClientResponse(item, responseCode);
        });
    }

    private static Bitmap covertToBitmap(String base64String) {
        try {
            base64String = base64String.trim();
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String convertToString(Bitmap bitmap) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}