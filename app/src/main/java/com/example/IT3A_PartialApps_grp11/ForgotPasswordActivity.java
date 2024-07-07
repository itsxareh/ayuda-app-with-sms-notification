package com.example.IT3A_PartialApps_grp11;

import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button resetBtn, cancelReset;
    private EditText resetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //getSupportActionBar().setTitle("Forgot Password");

        resetEmail = findViewById(R.id.reset_email);
        resetBtn = findViewById(R.id.resetBtn);
        progressBar = findViewById(R.id.progressBar);
        cancelReset = findViewById(R.id.cancelReset);


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = resetEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your registered email!",Toast.LENGTH_SHORT).show();resetEmail.setError("Email is required");
                    resetEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter correct email!",Toast.LENGTH_SHORT).show();
                    resetEmail.setError("Email is invalid");
                    resetEmail.requestFocus();
                }else {
                    progressBar.setVisibility(v.VISIBLE);
                    resetPassword(email);
                }
            }
            private void resetPassword(String email) {
                authProfile = FirebaseAuth.getInstance();
                authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                        Toast.makeText(ForgotPasswordActivity.this, "Please check your gmail inbox for password reset link",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Login.class));
                            finish();
                        }else {
                            Toast.makeText(ForgotPasswordActivity.this, "Something went wrong!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this,Login.class);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                });
            }
        });
        cancelReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
}