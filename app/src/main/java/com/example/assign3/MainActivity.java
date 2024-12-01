package com.example.assign3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assign3.apiClient.ApiClient;
import com.example.assign3.apiClient.model.Client;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ClientManager clientManager;
    private Spinner sortSpinner;
    private SearchView  searchView;
    private Button openFilterButton;
    private String token;
    private ApiClient apiClient;
    private List<Client> clientList;

    private final ActivityResultLauncher<Intent> filterActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String minAge = result.getData().getStringExtra("minAge");
                    String maxAge = result.getData().getStringExtra("maxAge");
                    String status = result.getData().getStringExtra("status");
                    filterClients(minAge, maxAge, status);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiClient = ApiClient.getInstance();

        setUpAuth();
        getClientsFromApi(token);
        setUpAddClientButton();
        setUpRefreshButton();
    }

    private void setUpAuth() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        token = sharedPreferences.getString("token", null);
        long loginTimestamp = sharedPreferences.getLong("loginTimestamp", 0);

        long oneDayInMillis = 24 * 60 * 60 * 1000;
        long currentTime = System.currentTimeMillis();
        boolean isTokenExpired = currentTime - loginTimestamp > oneDayInMillis;

        if (!isLoggedIn || TextUtils.isEmpty(token) || isTokenExpired) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setUpAddClientButton() {
        Button addClientButton = findViewById(R.id.addClientButton);
        addClientButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddClientActivity.class);
            intent.putExtra("authTok", token);
            startActivity(intent);
        });
    }

    private void setUpRefreshButton() {
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            getClientsFromApi(token);
        });
    }

    private void setClientManager() {
        RecyclerView recyclerView = findViewById(R.id.clientRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        clientManager = new ClientManager(this, recyclerView, clientList);
    }

    private void setUpSearch() {
        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clientManager.filterClients(newText);
                return true;
            }
        });
    }

    private void setUpSort() {
        sortSpinner = findViewById(R.id.sortSpinner);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clientManager.sortClients(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setUpFiltering() {
        openFilterButton = findViewById(R.id.openFilterButton);
        openFilterButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            filterActivityResultLauncher.launch(intent);
        });

        Button clearFiltersButton = findViewById(R.id.clearFiltersButton);
        clearFiltersButton.setOnClickListener(v -> {
            clientManager.clearFilters();
        });
    }

    private void getClientsFromApi(String token) {
        this.apiClient.getClients(token).thenAccept(clients -> {
            clientList = clients;
            runOnUiThread(() -> {
                setClientManager();
                setUpSearch();
                setUpSort();
                setUpFiltering();
            });
        });
    }

    private void filterClients(String minAge, String maxAge, String status) {
        int min = minAge == null ? Integer.MIN_VALUE : Integer.parseInt(minAge);
        int max = maxAge == null ? Integer.MAX_VALUE : Integer.parseInt(maxAge);

        clientManager.filterClientsByAgeRangeAndStatus(min, max, status);
    }
}