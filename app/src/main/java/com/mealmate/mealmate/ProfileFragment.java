package com.mealmate.mealmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mealmate.mealmate.adapter.ProfilePlanAdapter;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.data.model.UserProfile;
import com.mealmate.mealmate.databinding.FragmentProfileBinding;
import com.mealmate.mealmate.interfaces.AddMealPlanClickListener;
import com.mealmate.mealmate.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements AddMealPlanClickListener {
    private FragmentProfileBinding binding;
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public List<MealData> dataList = new ArrayList<>();
    private ProfilePlanAdapter mealGridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.myPlansRv.setVisibility(View.GONE);
        binding.profileCreateMeal.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewMealActivity.class);
            startActivity(intent);
        });
        binding.fabAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewMealActivity.class);
            startActivity(intent);
        });
        binding.fabSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
        getUserProfile();
        getMeals();
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getMeals() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (getContext() == null) return;
        if (user == null) return;
        mealGridAdapter = new ProfilePlanAdapter(this);
        binding.myPlansRv.setAdapter(mealGridAdapter);
        binding.myPlansRv.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));
        mealGridAdapter.dataList = dataList;

        CollectionReference docRef = firestore.collection("my_created_plans").document(user.getUid()).collection("my_meals");
        docRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                message("Error listening for updates: " + error.getMessage());
                return;
            }

            if (value != null) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    switch (change.getType()) {
                        case ADDED:
                            MealData mealData = change.getDocument().toObject(MealData.class);
                            dataList.add(mealData);
                            mealGridAdapter.notifyItemInserted(dataList.size());
                            binding.emptyState.setVisibility(View.GONE);
                            binding.myPlansRv.setVisibility(View.VISIBLE);
                            break;
                        case MODIFIED:
                            MealData newMeal = change.getDocument().toObject(MealData.class);
                            int count = 0;
                            for(MealData mealData1: dataList) {
                                if(newMeal.getId().equals(mealData1.getId())) {
                                    dataList.set(count, newMeal);
                                    mealGridAdapter.notifyItemChanged(count);
                                    break;
                                }
                                count++;
                            }
                            break;
                        case REMOVED:
                            MealData meal = change.getDocument().toObject(MealData.class);
                            dataList.removeIf(i -> i.getTitle().equals(meal.getTitle()) && i.getInstructions().equals(meal.getInstructions()));
                            mealGridAdapter.notifyDataSetChanged();
                            if(dataList.isEmpty()) {
                                binding.emptyState.setVisibility(View.VISIBLE);
                                binding.myPlansRv.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            }
        });
    }

    public void getUserProfile() {
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (getContext() == null || user == null) return;
        DocumentReference docRef = firestore.collection("userProfiles").document(user.getUid());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    UserProfile userProfile = document.toObject(UserProfile.class);
                    if (userProfile != null) {
                        setProfile(userProfile);
                    }
                } else {
                    System.out.println("No such document found!");
                }
            } else {
                System.err.println("Error fetching document: " + task.getException());
            }
        });
    }

    private void setProfile(UserProfile userProfile) {
        if (getContext() == null) return;
        binding.profileUser.setText(userProfile.getFullName());
        binding.profileEmail.setText(userProfile.getEmail());
        binding.profilePhone.setText(userProfile.getPhone());
        binding.profileFood.setText(userProfile.getDietaryPreference());
        Glide.with(getContext()).load(userProfile.getImageUrl()).circleCrop().into(binding.profileImage);
    }

    @Override
    public void onItemClicked(MealData data) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (getContext() == null) return;
        if (user == null) return;
        firestore.collection("my_meal_plans")
                .document(user.getUid())
                .collection("my_meals")
                .document(data.getTitle() + data.getType())
                .set(data).addOnSuccessListener(v-> message("Added"));
    }

    private void message(String s) {
        Snackbar.make(binding.getRoot(), s, Snackbar.LENGTH_LONG).show();
    }
}