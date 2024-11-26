package com.mealmate.mealmate;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mealmate.mealmate.adapter.HomeCarouselAdapter;
import com.mealmate.mealmate.adapter.HomePreferencesAdapter;
import com.mealmate.mealmate.adapter.MealGridAdapter;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.data.model.UserProfile;
import com.mealmate.mealmate.databinding.FragmentHomeBinding;
import com.mealmate.mealmate.interfaces.AddMealPlanClickListener;
import com.mealmate.mealmate.interfaces.PreferenceClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements PreferenceClickListener, AddMealPlanClickListener {
    private FragmentHomeBinding binding;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final List<Integer> dataList = new ArrayList<>();
    private List<MealData> mealDataList = new ArrayList<>();
    private final List<MealData> allMealDataList = new ArrayList<>();
    String[] preferences = {"Classic", "Low Carb", "Keto", "Flexitarian", "Paleo", "Vegetarian", "Vegan", "Gluten Free",};
    Handler handler = new Handler();
    HomeCarouselAdapter adapter;
    MealGridAdapter mealGridAdapter;
    HomePreferencesAdapter homePreferencesAdapter;

    Runnable runnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.greeting.setText("Good day");
        emptyState();
        initViewPager();
        getUserProfile();
        getMeals();
        searchFilter();
        return binding.getRoot();
    }

    private void searchFilter() {
        binding.homeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(allMealDataList.isEmpty()) return;
                List<MealData> newList = new ArrayList<>();
                for(MealData meal : allMealDataList) {
                    if(meal.getTitle().toLowerCase().contains(text)) {
                        newList.add(meal);
                    }
                }
                mealDataList.clear();
                mealDataList.addAll(newList);
                mealGridAdapter.notifyDataSetChanged();
            }
        });
    }

    private void emptyState() {
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    private void filledState() {
        binding.emptyState.setVisibility(View.GONE);
    }

    private void getPreferences() {
        homePreferencesAdapter = new HomePreferencesAdapter(Arrays.asList(preferences), this);
        binding.homeListPreferences.setAdapter(homePreferencesAdapter);
        binding.homeListPreferences.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initViewPager() {
        dataList.add(R.drawable.c1);
        dataList.add(R.drawable.c2);
        dataList.add(R.drawable.c3);
        dataList.add(R.drawable.c4);
        adapter = new HomeCarouselAdapter(dataList);
        binding.homeViewpager.setAdapter(adapter);
        binding.homeViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = binding.homeViewpager.getCurrentItem();
                int itemCount = adapter.getItemCount();

                if (currentItem < itemCount - 1) {
                    binding.homeViewpager.setCurrentItem(currentItem + 1, true);
                } else {
                    binding.homeViewpager.setCurrentItem(0, true);
                }
                handler.postDelayed(this, 3000);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    public void getUserProfile() {
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

    public void getMeals() {
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        mealGridAdapter = new MealGridAdapter(this);
        binding.homeGrid.setAdapter(mealGridAdapter);
        binding.homeGrid.setLayoutManager(new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL));
        mealGridAdapter.dataList = mealDataList;

        CollectionReference docRef = firestore.collection("meals");
        docRef.get().addOnSuccessListener(task -> {
            for (DocumentSnapshot snapshot : task.getDocuments()) {
                MealData mealData = snapshot.toObject(MealData.class);
                mealDataList.add(mealData);
                allMealDataList.add(mealData);
                mealGridAdapter.notifyItemInserted(mealDataList.size());
            }
            if(!mealDataList.isEmpty()) filledState();
            if(mealDataList.isEmpty()) emptyState();
            getPreferences();
        });
    }

    private void message() {
        Snackbar.make(binding.getRoot(), "Added", Snackbar.LENGTH_LONG).show();
    }

    private void setProfile(UserProfile userProfile) {
        if (getContext() == null) return;
        binding.username.setText(userProfile.getFullName());
        Glide.with(getContext()).load(userProfile.getImageUrl()).circleCrop().into(binding.homeImage);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClicked(String data) {
        mealDataList.clear();
        List<MealData> newList = new ArrayList<>();
        for(MealData meal : allMealDataList) {
            if(meal.getType().equalsIgnoreCase(data)) {
                newList.add(meal);
            }
        }
        mealDataList.addAll(newList);
        if(!mealDataList.isEmpty()) filledState();
        if(mealDataList.isEmpty()) emptyState();
        mealGridAdapter.notifyDataSetChanged();
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
                .set(data).addOnSuccessListener(v-> message());
    }
}