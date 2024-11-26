package com.mealmate.mealmate.ui.login;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mealmate.mealmate.HomeActivity;
import com.mealmate.mealmate.data.model.UserProfile;
import com.mealmate.mealmate.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ActivitySignUpBinding binding;
    Boolean isPermissionGranted = false;
    Uri uri = null;
    String[] items = {"Classic", "Low Carb", "Keto", "Flexitarian", "Paleo", "Vegetarian", "Vegan", "Gluten Free",};

    ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    this.uri = uri;
                    message("Image selected.");
                } else {
                    message("No image selected.");
                }
            });

    ActivityResultLauncher<String[]> requestPermissions =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isPermissionGranted = results.get(Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    isPermissionGranted = results.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (Boolean.TRUE.equals(isPermissionGranted)) {
                    openImagePicker();
                } else {
                    message("App requires permission to select image");
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.preferenceSpinner.setAdapter(adapter);
        binding.signUpPickImage.setOnClickListener(v -> requestPermission());
        binding.signUpButton.setOnClickListener(v -> authenticate());
        binding.signInText.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    private void authenticate() {
        String fullName = binding.signUpFullName.getText().toString().trim();
        String email = binding.signUpEmail.getText().toString().trim();
        String phone = binding.signUpPhone.getText().toString().trim();
        String password = binding.signUpPassword.getText().toString().trim();
        String cPassword = binding.signUpRePassword.getText().toString().trim();
        String preference = binding.preferenceSpinner.getSelectedItem().toString();
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || cPassword.isEmpty()) {
            message("Empty fields");
            return;
        }
        if (!password.equals(cPassword)) {
            message("Password don't match");
            return;
        }
        if (uri == null) {
            message("Select an image");
            return;
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(email);
        userProfile.setFullName(fullName);
        userProfile.setPhone(phone);
        userProfile.setDietaryPreference(preference);
        userProfile.setEmail(email);
        binding.loginLoading.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        uploadImage(userProfile);
                    } else {
                        binding.loginLoading.setVisibility(View.GONE);
                        Snackbar.make(binding.getRoot(), "Failed to sign in. Please retry.", Snackbar.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(v -> {
                    binding.loginLoading.setVisibility(View.GONE);
                });
    }

    public void uploadImage(UserProfile userProfile) {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String fileName = "profile_photo/" + user.getUid() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);
        UploadTask uploadTask = imageRef.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                userProfile.setUid(user.getUid());
                userProfile.setImageUrl(downloadUrl);
                uploadProfile(userProfile);
                Log.d("FirebaseStorage", "File uploaded. Download URL: " + downloadUrl);
            });
        }).addOnFailureListener(e -> {
            binding.loginLoading.setVisibility(View.GONE);
            message("File upload failed: " + e.getMessage());
        });
    }

    private void uploadProfile(UserProfile userProfile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collectionName = "userProfiles";
        db.collection(collectionName)
                .document(userProfile.getUid())
                .set(userProfile)
                .addOnSuccessListener(documentReference -> {
                    binding.loginLoading.setVisibility(View.GONE);
                    message("Success");
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    binding.loginLoading.setVisibility(View.GONE);
                    message("Error adding UserProfile: " + e.getMessage());
                });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
            });
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
            });
        } else {
            requestPermissions.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }

    }

    private void message(String msg) {
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
    }

    private void openImagePicker() {
        pickImageLauncher.launch("image/*");
    }
}
