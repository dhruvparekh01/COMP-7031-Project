package com.example.assign3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assign3.apiClient.ApiClient;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        apiClient = ApiClient.getInstance();

        initializeViews();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        setUpLoginButton();
        setUpSignupButton();
    }

    private void setUpLoginButton() {
        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                emptyFieldsToast();
            } else {
                authenticateUser(username, password);
            }
        });
    }

    private void setUpSignupButton() {
        signUpButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                emptyFieldsToast();
            } else {
                signUpUser(username, password);
            }
        });
    }

    private void signUpUser(String username, String password) {
        apiClient.signUpUser(username, password).thenAccept(response -> runOnUiThread(() -> {
            if (response.getResponseCode() == 201) {
                shortToast("User created successfully. Please login to continue");
            } else {
                shortToast(response.getMessage());
            }
        }));
    }

    private void authenticateUser(String username, String password) {
        apiClient.loginUser(username, password).thenAccept(response -> {
            if (response.getResponseCode() == 200) {
                saveToken(response.getToken());
            }
            runOnUiThread(() -> {
                if (response.getResponseCode() == 200) {
                    shortToast("User logged in successfully");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    shortToast(response.getMessage());
                }
            });
        });
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("token", token);
        editor.putLong("loginTimestamp", System.currentTimeMillis());
        editor.apply();
    }

    private void emptyFieldsToast() {
        Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
    }

    private void shortToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}