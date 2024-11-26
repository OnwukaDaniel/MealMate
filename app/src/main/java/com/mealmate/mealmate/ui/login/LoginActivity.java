package com.mealmate.mealmate.ui.login;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mealmate.mealmate.HomeActivity;
import com.mealmate.mealmate.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    // ...
// Initialize Firebase Auth

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.loginEmail.getText().toString().trim();
            String password = binding.loginPassword.getText().toString().trim();
            if(email.isEmpty()){
                Snackbar.make(binding.getRoot(), "Email cannot be empty", Snackbar.LENGTH_LONG).show();
                return;
            }
            if(password.isEmpty()){
                Snackbar.make(binding.getRoot(), "Password cannot be empty", Snackbar.LENGTH_LONG).show();
                return;
            }
            binding.loginLoading.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            binding.loginLoading.setVisibility(View.GONE);
                        } else {
                            Snackbar.make(binding.getRoot(), "Failed to sign in. Please retry.", Snackbar.LENGTH_LONG).show();
                            binding.loginLoading.setVisibility(View.GONE);
                        }
                    });
        });
        binding.signUpText.setOnClickListener(v-> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }
}