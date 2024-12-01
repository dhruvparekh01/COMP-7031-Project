package com.example.assign3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FilterActivity extends Activity {

    private EditText filterMinAgeEditText;
    private EditText filterMaxAgeEditText;
    private Spinner filterStatusSpinner;

    private static final String PREFS_NAME = "FilterPrefs";
    private static final String KEY_MIN_AGE = "minAge";
    private static final String KEY_MAX_AGE = "maxAge";
    private static final String KEY_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        filterMinAgeEditText = findViewById(R.id.filterMinAgeEditText);
        filterMaxAgeEditText = findViewById(R.id.filterMaxAgeEditText);
        filterStatusSpinner = findViewById(R.id.filterStatusSpinner);
        Button applyFilterButton = findViewById(R.id.applyFilterButton);

        // Restore saved state
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        filterMinAgeEditText.setText(preferences.getString(KEY_MIN_AGE, ""));
        filterMaxAgeEditText.setText(preferences.getString(KEY_MAX_AGE, ""));
        String status = preferences.getString(KEY_STATUS, "All");
        for (int i = 0; i < filterStatusSpinner.getCount(); i++) {
            if (filterStatusSpinner.getItemAtPosition(i).toString().equals(status)) {
                filterStatusSpinner.setSelection(i);
                break;
            }
        }

        applyFilterButton.setOnClickListener(v -> {
            String minAge = filterMinAgeEditText.getText().toString();
            String maxAge = filterMaxAgeEditText.getText().toString();
            String statusSelected = filterStatusSpinner.getSelectedItem().toString();

            // Save state
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_MIN_AGE, minAge);
            editor.putString(KEY_MAX_AGE, maxAge);
            editor.putString(KEY_STATUS, statusSelected);
            editor.apply();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("minAge", minAge.isEmpty() ? null : minAge);
            resultIntent.putExtra("maxAge", maxAge.isEmpty() ? null : maxAge);
            resultIntent.putExtra("status", statusSelected.equals("All") ? null : statusSelected);
            resultIntent.putExtra("shouldRefresh", false);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}