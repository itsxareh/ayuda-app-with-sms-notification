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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePass extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText;
    private Button changePasswordButton, backBtn;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        currentPasswordEditText = findViewById(R.id.editTextCurrentPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        backBtn = findViewById(R.id.backBtn);

        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
                startActivity(new Intent(getApplicationContext(), Settings.class));
                finish();
            }
        });

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChangePass.this, Settings.class);
            startActivity(intent);
            finish();
        });
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(ChangePass.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPassword.equals(newPassword)) {
            Toast.makeText(ChangePass.this, "New password cannot be the same as the current password", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = fStore.collection("Users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists() && document.getString("Password").equals(currentPassword)) {
                        docRef.update("Password", newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePass.this, "Password updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChangePass.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(ChangePass.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePass.this, "Failed to fetch current password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
