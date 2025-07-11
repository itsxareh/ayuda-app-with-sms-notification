package com.example.IT3A_PartialApps_grp11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button loginBtn, forgot;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        forgot = findViewById(R.id.forgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkField(email) || !checkField(password)) {
                    showAlertDialog("Please fill all the fields.");
                    return;
                }

                Log.d(TAG, "Attempting to sign in with email: " + email.getText().toString());

                fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d(TAG, "Sign-in successful");
                                Toast.makeText(Login.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                checkUserAccessLevel(authResult.getUser().getUid());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Sign-in failed: " + e.getMessage());
                                showAlertDialog("Password is incorrect or field is incomplete.");
                            }
                        });
            }
        });
    }

    private void checkUserAccessLevel(String uid) {
        Log.d(TAG, "Checking user access level for UID: " + uid);

        DocumentReference df = fStore.collection("Users").document(uid);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String isBrgy = documentSnapshot.getString("isBrgy");
                    String isResi = documentSnapshot.getString("isResi");

                    Log.d(TAG, "Document data: " + documentSnapshot.getData());
                    Log.d(TAG, "isBrgy: " + isBrgy);
                    Log.d(TAG, "isResi: " + isResi);

                    if (isBrgy != null && isBrgy.equals("1")) {
                        startActivity(new Intent(getApplicationContext(), Admin.class));
                        finish();
                    } else if (isResi != null && isResi.equals("2")) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        showAlertDialog("User access level is undefined.");
                    }
                } else {
                    Log.d(TAG, "User document does not exist.");
                    showAlertDialog("User document does not exist.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to retrieve user data: " + e.getMessage());
                showAlertDialog("Failed to retrieve user data.");
            }
        });
    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Field cannot be empty");
            return false;
        } else {
            return true;
        }
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
