package com.example.IT3A_PartialApps_grp11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class SubsidyDetailsActivity extends AppCompatActivity {

    Spinner subsidySpinner;
    ImageButton saveButton;
    String fullName;
    String currentSubsidyStatus;
    String userId;
    int positionToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_subsidy);

        // Retrieve data from intent extras
        fullName = getIntent().getStringExtra("FullName");
        currentSubsidyStatus = getIntent().getStringExtra("SubsidyStatus");
        userId = getIntent().getStringExtra("userId");
        positionToUpdate = getIntent().getIntExtra("positionToUpdate", -1);

        // Initialize views
        subsidySpinner = findViewById(R.id.subsidymenu);
        saveButton = findViewById(R.id.saveButton);

        // Set up spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.subsidy_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subsidySpinner.setAdapter(adapter);

        // Set initial selection based on current subsidy status
        int position = adapter.getPosition(currentSubsidyStatus);
        subsidySpinner.setSelection(position);

        // Spinner item selected listener
        subsidySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle spinner item selection
                currentSubsidyStatus = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            updateSubsidyStatus(userId, currentSubsidyStatus, positionToUpdate);
        });
    }

    private void updateSubsidyStatus(String userId, String newStatus, int positionToUpdate) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("Users").document(userId).update("subsidyStatus", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("NewSubsidyStatus", newStatus);
                    resultIntent.putExtra("PositionToUpdate", positionToUpdate);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                });
    }
}
