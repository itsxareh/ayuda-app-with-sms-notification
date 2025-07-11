package com.example.IT3A_PartialApps_grp11;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class introActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            redirectToAppropriateActivity();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(introActivity.this, Login.class));
                    finish();
                }
            }, 1500);
        }
    }

    private void redirectToAppropriateActivity() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("introact", "Current user UID: " + uid);

        DocumentReference df = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String isBrgy = documentSnapshot.getString("isBrgy");
                    String isResi = documentSnapshot.getString("isResi");

                    Log.d("introact", "Document data: " + documentSnapshot.getData());
                    Log.d("introact", "isBrgy: " + isBrgy);
                    Log.d("introact", "isResi: " + isResi);

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
                    Log.d("introact", "User document does not exist.");
                    showAlertDialog("User document does not exist.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("introact", "Failed to retrieve user data: " + e.getMessage());
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            redirectToAppropriateActivity();
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
