package com.mealmate.mealmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mealmate.mealmate.adapter.PurchasedPlanGridAdapter;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.databinding.FragmentPurchasedBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PurchasedListFragment extends Fragment {
    FragmentPurchasedBinding binding;
    PurchasedPlanGridAdapter adapter;
    List<MealData> mealDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPurchasedBinding.inflate(inflater, container, false);
        emptyState();
        getMeals();
        binding.fabShare.setOnClickListener(v-> shareMeal());
        return binding.getRoot();
    }

    private void emptyState() {
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    private void filledState() {
        binding.emptyState.setVisibility(View.GONE);
    }

    private void shareMeal() {
        StringBuilder ingredients = new StringBuilder();
        for(MealData meal : mealDataList) {
            for (Map.Entry<String, String> entry : meal.getIngredients().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                ingredients.append(key).append(": ").append(value).append("\n");
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(meal.getTitle())
                    .append("\n")
                    .append("Cooking time:")
                    .append(meal.getTime())
                    .append("min")
                    .append("\n")
                    .append("Ingredients:")
                    .append(ingredients)
                    .append("\n\n")
                    .append("Instruction:")
                    .append(meal.getInstructions())
                    .append("\n\n");
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getMeals() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        adapter = new PurchasedPlanGridAdapter();
        binding.purchasedRv.setAdapter(adapter);
        binding.purchasedRv.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));
        adapter.dataList = mealDataList;

        CollectionReference docRef = firestore.collection("my_purchased").document(user.getUid()).collection("my_meals");
        docRef.addSnapshotListener(((value, error) -> {
            if (error != null) {
                message("Error listening for updates: " + error.getMessage());
                return;
            }

            if (value != null) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    switch (change.getType()) {
                        case ADDED:
                            MealData mealData = change.getDocument().toObject(MealData.class);
                            mealDataList.add(mealData);
                            adapter.notifyItemInserted(mealDataList.size());
                            binding.fabShare.setVisibility(View.VISIBLE);
                            if(!mealDataList.isEmpty()) filledState();
                            if(mealDataList.isEmpty()) emptyState();
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            MealData meal = change.getDocument().toObject(MealData.class);
                            mealDataList.removeIf(i-> i.getTitle().equals(meal.getTitle()) && i.getInstructions().equals(meal.getInstructions()));
                            adapter.notifyDataSetChanged();
                            if(mealDataList.isEmpty()) binding.fabShare.setVisibility(View.GONE);
                            if(!mealDataList.isEmpty()) filledState();
                            if(mealDataList.isEmpty()) emptyState();
                            break;
                    }
                }
            }
        }));
    }

    private void message(String msg) {
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
    }
}
