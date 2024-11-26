package com.mealmate.mealmate.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mealmate.mealmate.CartFragment;
import com.mealmate.mealmate.MealPlanFragment;
import com.mealmate.mealmate.PurchasedListFragment;

public class CartAdapter extends FragmentStateAdapter {

    public CartAdapter(@NonNull CartFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new PurchasedListFragment();
        }
        return new MealPlanFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
