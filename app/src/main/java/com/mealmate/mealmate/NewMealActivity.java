package com.mealmate.mealmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mealmate.mealmate.adapter.IngredientAdapter;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.databinding.ActivityNewMealBinding;
import com.mealmate.mealmate.interfaces.IngredientsFillListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewMealActivity extends AppCompatActivity implements IngredientsFillListener {
    private ActivityNewMealBinding binding;
    private Map<String, String> ingredients = new LinkedHashMap<>();
    private IngredientAdapter adapter;
    private MealData mealData = new MealData();
    Boolean isPermissionGranted = false;
    Uri uri = null;
    boolean editMode = false;
    String[] items = {"Classic", "Low Carb", "Keto", "Flexitarian", "Paleo", "Vegetarian", "Vegan", "Gluten Free",};

    ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    this.uri = uri;
                    Glide.with(getApplicationContext()).load(uri).centerCrop().into(binding.ingredientImage);
                    message("Image selected.");
                } else {
                    message("No image selected.");
                }
            });

    ActivityResultLauncher<String[]> requestPermissions =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isPermissionGranted = results.get(android.Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    isPermissionGranted = results.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (Boolean.TRUE.equals(isPermissionGranted)) {
                    openImagePicker();
                } else {
                    message("App requires permission to select image");
                }
            });

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.backArrow.setOnClickListener(v -> onBackPressed());
        binding.ingredientImage.setOnClickListener(v -> requestPermission());
        binding.fabAddImage.setOnClickListener(v -> requestPermission());
        binding.recipeSubmit.setOnClickListener(v -> uploadImage());
        ingredients();
        spinner();

        if (getIntent().hasExtra("meal_data")) {
            mealData = getIntent().getParcelableExtra("meal_data");
            editMode = true;
            binding.newMealTitle.setText("Edit Meal");
            binding.recipeName.setText(mealData.getTitle());
            binding.recipeTime.setText(String.valueOf(mealData.getTime()));
            binding.recipeInstructions.setText(mealData.getInstructions());
            Glide.with(getApplicationContext()).load(mealData.getImage()).centerCrop().into(binding.ingredientImage);
            //ingredients = mealData.getIngredients();
            for(int i = 0; i < mealData.getIngredients().size(); i++) {
                String key = new ArrayList<>(mealData.getIngredients().keySet()).get(i);
                String value = new ArrayList<>(mealData.getIngredients().values()).get(i);
                ingredients.put(key, value);
                adapter.notifyItemInserted(i);
            }
            System.out.println("Got key **************** "+ ingredients);
        }
    }

    private void loading() {
        binding.mealLoading.setVisibility(View.VISIBLE);
        binding.recipeSubmit.setVisibility(View.GONE);
    }

    private void stopLoading() {
        binding.mealLoading.setVisibility(View.GONE);
        binding.recipeSubmit.setVisibility(View.VISIBLE);
    }

    private void submit() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collectionName = "my_created_plans";
        if (editMode) {
            db.collection(collectionName)
                    .document(user.getUid())
                    .collection("my_meals")
                    .document(mealData.getId())
                    .set(mealData, SetOptions.merge())
                    .addOnSuccessListener(documentReference -> {
                        stopLoading();
                        message("Success");
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        stopLoading();
                        message("Error editing meal: " + e.getMessage());
                    });
        } else {
            CollectionReference collectionRef = db.collection(collectionName)
                    .document(user.getUid())
                    .collection("my_meals");
            DocumentReference docRef = collectionRef.document();
            String documentId = docRef.getId();
            mealData.setId(documentId);
            docRef.set(mealData)
                    .addOnSuccessListener(documentReference -> {
                        stopLoading();
                        message("Success");
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        stopLoading();
                        message("Error adding meal: " + e.getMessage());
                    });
        }
    }

    public void uploadImage() {
        String name = binding.recipeName.getText().toString().trim();
        String time = binding.recipeTime.getText().toString().trim();
        String instructions = binding.recipeInstructions.getText().toString().trim();
        String preference = binding.preferenceSpinner.getSelectedItem().toString();
        if (name.isEmpty()) {
            message("Name can't be empty");
            return;
        }
        if (time.isEmpty()) {
            message("Time can't be empty");
            return;
        }
        if (instructions.isEmpty()) {
            message("Cooking instructions can't be empty");
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        List<String> keys = new ArrayList<>(ingredients.keySet());
        List<String> values = new ArrayList<>(ingredients.values());
        if (keys.isEmpty()) {
            message("Enter at least an ingredient and quantity");
            return;
        }
        String key = keys.get(0);
        String value = values.get(0);
        if (key.isEmpty() || value.isEmpty()) {
            message("Enter at least an ingredient and quantity");
            return;
        }
        if (uri == null && mealData.getImage().isEmpty()) {
            message("Select meal image");
            return;
        }
        mealData.setTitle(name);
        mealData.setTime(Long.valueOf(time));
        mealData.setType(preference);
        mealData.setInstructions(instructions);
        mealData.setIngredients(ingredients);

        loading();
        if(editMode && uri == null) {
            submit();
        } else {
            FirebaseUser user = auth.getCurrentUser();
            assert user != null;
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            String fileName = "profile_photo/" + user.getUid() + ".jpg";
            StorageReference imageRef = storageRef.child(fileName);
            UploadTask uploadTask = imageRef.putFile(uri);
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                mealData.setImage(downloadUrl);
                submit();
            })).addOnFailureListener(e -> {
                stopLoading();
                message("File upload failed: " + e.getMessage());
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void ingredients() {
        adapter = new IngredientAdapter(ingredients, this);
        binding.ingredientsAdapter.setAdapter(adapter);
        binding.ingredientsAdapter.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        binding.addNewIngredient.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.ingredients_item, null);

            EditText name = dialogView.findViewById(R.id.ingredient_name);
            dialogView.setPadding(46, 46, 46, 46);
            EditText quantity = dialogView.findViewById(R.id.ingredient_quantity);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Enter New ingredient")
                    .setView(dialogView)
                    .setPositiveButton("Submit", (dialogInterface, i) -> {
                        String value1 = name.getText().toString().trim();
                        String value2 = quantity.getText().toString().trim();
                        ingredients.put(value1, value2);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .create();
            dialog.show();
        });
    }

    private void spinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.preferenceSpinner.setAdapter(adapter);
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

    @Override
    public void onItemFilled(String data, Integer position, boolean isKey) {
        if (isKey) ingredients = setKeyOnMapAtPosition(ingredients, position, data);
        if (!isKey) ingredients = setValueMapAtPosition(ingredients, position, data);
        System.out.println(ingredients);
    }

    public static Map<String, String> setKeyOnMapAtPosition(Map<String, String> map, int position, String newKey) {
        List<Map.Entry<String, String>> entryList = new ArrayList<>(map.entrySet());
        if (position < 0 || position >= entryList.size()) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }

        Map.Entry<String, String> currentMap = entryList.get(position);
        String currentValue = currentMap.getValue();
        entryList.set(position, new AbstractMap.SimpleEntry<>(newKey, currentValue));
        Map<String, String> updatedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : entryList) {
            updatedMap.put(entry.getKey(), entry.getValue());
        }
        return updatedMap;
    }

    public static Map<String, String> setValueMapAtPosition(Map<String, String> map, int position, String newValue) {
        List<Map.Entry<String, String>> entryList = new ArrayList<>(map.entrySet());
        if (position < 0 || position >= entryList.size()) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }

        Map.Entry<String, String> currentMap = entryList.get(position);
        String currentKey = currentMap.getKey();
        entryList.set(position, new AbstractMap.SimpleEntry<>(currentKey, newValue));
        Map<String, String> updatedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : entryList) {
            updatedMap.put(entry.getKey(), entry.getValue());
        }
        return updatedMap;
    }
}