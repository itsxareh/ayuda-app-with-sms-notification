package com.example.IT3A_PartialApps_grp11;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText;
    private Button saveButton, backBtn;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        fullNameEditText = findViewById(R.id.editTextFullName);
        emailEditText = findViewById(R.id.editTextEmail);
        saveButton = findViewById(R.id.saveButton);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfile.this, Settings.class);
            startActivity(intent);
            finish();
        });

        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            loadUserData(userId);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                startActivity(new Intent(getApplicationContext(), Settings.class));
                finish();
            }
        });
    }

    private void loadUserData(String userId) {
        DocumentReference docRef = fStore.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    fullNameEditText.setText(documentSnapshot.getString("FullName"));
                    emailEditText.setText(documentSnapshot.getString("UserEmail"));
                } else {
                    Toast.makeText(EditProfile.this, "No such document", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfile() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = fStore.collection("Users").document(userId);
        docRef.update("FullName", fullName, "UserEmail", email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
