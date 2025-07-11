package com.example.IT3A_PartialApps_grp11;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SNIHostName;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Admin extends AppCompatActivity {

    private static final AtomicInteger messageId = new AtomicInteger(0);
    private OkHttpClient client = new OkHttpClient();
    DrawerLayout drawerLayout;
    TextView nameView, emailView, username;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        username = findViewById(R.id.usernameText);

        CardView cardBeneficiaries = findViewById(R.id.cardBeneficiaries);
        CardView cardLogout = findViewById(R.id.cardLogout);
        CardView cardAnnouncement = findViewById(R.id.cardAnnouncement);
        CardView cardSettings = findViewById(R.id.cardSettings);
        CardView cardSubsidy = findViewById(R.id.cardSubsidy);
        CardView cardReports = findViewById(R.id.cardReports);

        cardReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, Reports.class);
                startActivity(intent);
            }
        });

        cardBeneficiaries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, BeneficiariesList.class);
                startActivity(intent);
            }
        });

        cardSubsidy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, SubsidyList.class);
                startActivity(intent);
            }
        });

        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, Settings.class);
                startActivity(intent);
            }
        });

        cardAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                announcement();
            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Admin.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), Login.class);

                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        // Set user information in the header
        createNotificationChannel();
        setUserInformation();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SubsidyNotification";
            String description = "Subsidy Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("SUBSIDY_CHANNEL", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void setUserInformation() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("USER ID", userId);
            fStore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            username.setText(user.getFullName());
                        }
                    }
                }
            });

            // Fetch FCM token and update in Firestore
//            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//                @Override
//                public void onComplete(@NonNull Task<String> task) {
//                    if (!task.isSuccessful()) {
//                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
//                        return;
//                    }
//                    String token = task.getResult();
//
//                    fStore.collection("Users").document(userId).update("FCMtoken", token)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("FCM", "Token saved successfully");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w("FCM", "Error saving token", e);
//                                }
//                            });
//                }
//            });
        }
    }


    private void sendSms(String phoneNumber, String message) {
        InfobipSmsUtils.sendSms(phoneNumber, message);
    }

    private void announcement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Notification");

        final EditText input = new EditText(this);
        builder.setView(input);
        input.setHint("Enter your message here");

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customMessage = input.getText().toString().trim();
                if (!customMessage.isEmpty()) {
                    sendEmailsToBeneficiaries(customMessage);
                } else {
                    Toast.makeText(Admin.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendEmailsToBeneficiaries(String customMessage) {
        fStore.collection("Users")
                .whereEqualTo("SubsidyStatus", "Claiming")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            User user = documentSnapshot.toObject(User.class);
                            Log.d("adminjava", user.getUserEmail());
                            if (user.getFcmToken() != null) {
                                sendPushNotification(user.getFcmToken(), "Subsidy Status", customMessage);
                            } else {
                                Log.d("FCMTOKEN", "FCM Token is null, skip push notification.");
                            }
                            MailUtils.sendEmail(user.getUserEmail(), "SUBSIDY STATUS: FOR CLAIMING", customMessage);
                            String formattedPhoneNumber = formatPhoneNumber(user.getPhoneNumber());
                            sendSms(formattedPhoneNumber, customMessage);

                        }
                        Toast.makeText(Admin.this, "Emails sent successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+63" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
    private void sendPushNotification(String fcmToken, String title, String customMessage) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SendNotification notificationSender =
                        new SendNotification(fcmToken, title, customMessage, Admin.this);

                notificationSender.SendNotifications();
            }
        }, 300);
    }

}
