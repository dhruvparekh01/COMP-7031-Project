package com.example.assign3;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assign3.apiClient.ApiClient;
import com.example.assign3.apiClient.model.ClientDetails;
import com.example.assign3.apiClient.model.PostClientResponse;

import java.util.concurrent.CompletableFuture;

public class AddClientActivity extends AppCompatActivity {
    private Spinner clientStatusSpinner;
    private Bitmap clientPhoto;
    private ApiClient apiClient;
    String authTok;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                clientPhoto = (Bitmap) extras.get("data");
                updatePhotoView();
            }
        }
    );

    private final ActivityResultLauncher<Intent> selectPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        clientPhoto = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                        updatePhotoView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        apiClient = ApiClient.getInstance();
        authTok = getIntent().getStringExtra("authTok");

        EditText firstNameEditText = findViewById(R.id.clientFirstNameEditText);
        EditText lastNameEditText = findViewById(R.id.clientLastNameEditText);
        EditText addressEditText = findViewById(R.id.clientAddressEditText);
        EditText emailEditText = findViewById(R.id.clientEmailEditText);
        EditText phoneEditText = findViewById(R.id.clientPhoneEditText);
        EditText ageEditText = findViewById(R.id.clientAgeEditText);
        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        Button saveClientButton = findViewById(R.id.saveClientButton);
        clientStatusSpinner = findViewById(R.id.clientStatusSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientStatusSpinner.setAdapter(adapter);

        uploadPhotoButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Upload Photo")
                    .setItems(new String[]{"Take Photo", "Select from Gallery"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureLauncher.launch(takePictureIntent);
                        } else {
                            Intent selectPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            selectPictureLauncher.launch(selectPictureIntent);
                        }
                    })
                    .show();
        });

        saveClientButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            int age = Integer.parseInt(ageEditText.getText().toString());
            String status = clientStatusSpinner.getSelectedItem().toString();

            ClientDetails newClient = new ClientDetails(clientPhoto, null, firstName, lastName, address, status, age, email, phone);

            CompletableFuture<PostClientResponse> futureData = apiClient.postClientDetails(newClient, authTok);
            futureData.thenAccept(response -> {
                if(response.getResponseCode() == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddClientActivity.this, "Successully added client", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else if (response.getResponseCode() == 409) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddClientActivity.this, "Client already exists", Toast.LENGTH_SHORT).show();
                    });
                }
            }).exceptionally(throwable -> {
                runOnUiThread(() -> Toast.makeText(AddClientActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
                return null;
            });
        });
    }

    private void updatePhotoView() {
        ImageView photoImageView = findViewById(R.id.photoImageView);
        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        if (clientPhoto != null) {
            photoImageView.setImageBitmap(clientPhoto);
            photoImageView.setVisibility(View.VISIBLE);
            uploadPhotoButton.setVisibility(View.GONE);
        }
    }
}