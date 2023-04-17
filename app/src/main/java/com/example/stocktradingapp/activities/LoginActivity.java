package com.example.stocktradingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.stocktradingapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    public void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> {
            if (binding.etEmail.getText().toString().length() > 0 && binding.etPassword.getText().toString().length() > 0) {
                firebaseAuth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        //Auth Failed!
                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}