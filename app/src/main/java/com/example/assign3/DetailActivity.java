package com.example.assign3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assign3.apiClient.model.ClientDetails;
import com.example.assign3.apiClient.ApiClient;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView;
    TextView nameText, addressText, ageText, emailText, phoneText;
    Spinner dropdown;
    ApiClient apiClient;
    String authTok;
    int clientId;
    ClientDetails clientDetails;
    private final String[] statuses = new String[]{"Completed", "Refused", "Partial"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        apiClient = ApiClient.getInstance();
        clientId = getIntent().getIntExtra("clientId", 2);
        authTok = getIntent().getStringExtra("authTok");

        initializeViews();
        setUpDropdown();
        getClientDetails();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.ivImage);
        nameText = findViewById(R.id.nameText);
        addressText = findViewById(R.id.addressText);
        ageText = findViewById(R.id.ageText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getClientDetails() {
        CompletableFuture<ClientDetails> futureData = apiClient.getClientDetails(clientId, authTok);
        futureData.thenAccept(clientDetails -> {
            runOnUiThread(() -> {
                this.clientDetails = clientDetails;
                setClientDetails();
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void setUpDropdown() {
        dropdown = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = (String) parent.getItemAtPosition(position);

                if(clientDetails!= null && !Objects.equals(clientDetails.getStatus(), selectedStatus)) {
                    CompletableFuture<ClientDetails> futureData = apiClient.updateClientStatus(clientId, authTok, selectedStatus);
                    futureData.thenAccept(clientDetails -> {
                        runOnUiThread(() -> {
                            Toast.makeText(DetailActivity.this, "UPDATED CLIENT", Toast.LENGTH_SHORT).show();
                        });
                    }).exceptionally(throwable -> {
                        runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
                        return null;
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setDropdownSelection(String clientStatus) {
        for (int i = 0; i < dropdown.getCount(); i++) {
            if (dropdown.getItemAtPosition(i).equals(clientStatus)) {
                dropdown.setSelection(i);
                break;
            }
        }
    }

    private void setClientDetails() {
        if (clientDetails != null) {
            imageView.setImageBitmap(clientDetails.getDecodedBitmap());
            nameText.setText(MessageFormat.format("{0} {1}", clientDetails.getFirstName(), clientDetails.getLastName()));
            addressText.setText(clientDetails.getAddress());
            ageText.setText(String.valueOf(clientDetails.getAge()));
            emailText.setText(clientDetails.getEmail());
            phoneText.setText(clientDetails.getPhone());
            setDropdownSelection(clientDetails.getStatus());
        } else {
            Toast.makeText(DetailActivity.this, "No data available", Toast.LENGTH_SHORT).show();
        }
    }
}