package com.mealmate.mealmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.databinding.ActivityMealDetailsBinding;

import java.util.Map;

public class MealDetails extends AppCompatActivity {
    MealData mealData;
    ActivityMealDetailsBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMealDetailsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        binding.mealDetailsBackArrow.setOnClickListener(v-> onBackPressed());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mealData = getIntent().getParcelableExtra("meal_data");
        if (mealData == null) {
            return;
        }

        Glide.with(binding.getRoot()).load(mealData.getImage()).centerCrop().into(binding.mealImage);
        binding.mealDetailsTitle.setText(mealData.getTitle());
        binding.mealDetailsTime.setText(mealData.getTime().toString() + " min");
        binding.mealDetailsInstructions.setText(mealData.getInstructions());
        StringBuilder ingredients = new StringBuilder();
        for (Map.Entry<String, String> entry : mealData.getIngredients().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ingredients.append(key).append(": ").append(value).append("\n");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mealData.getTitle())
                .append("\n")
                .append("Cooking time:")
                .append(mealData.getTime())
                .append("min")
                .append("\n")
                .append("Ingredients:")
                .append(ingredients)
                .append("\n\n")
                .append("Instruction:")
                .append(mealData.getInstructions());
        binding.mealDetailsIngredients.setText(ingredients.toString());
        binding.fabShare.setOnClickListener(v-> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        });
        binding.fabAdd.setOnClickListener(v->{
            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;
            firestore.collection("my_meal_plans")
                    .document(user.getUid())
                    .collection("my_meals")
                    .document(mealData.getTitle() + mealData.getType())
                    .set(mealData).addOnSuccessListener(i-> message());
        });
    }

    private void message() {
        Snackbar.make(binding.getRoot(), "Added", Snackbar.LENGTH_LONG).show();
    }
}