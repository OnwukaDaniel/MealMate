package com.mealmate.mealmate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mealmate.mealmate.adapter.CartAdapter;
import com.mealmate.mealmate.databinding.FragmentCartBinding;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);

        CartAdapter adapter = new CartAdapter(this);
        binding.cartViewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.cartTabLayout, binding.cartViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Meal plan list");
                    break;
                case 1:
                    tab.setText("Purchased meals");
                    break;
            }
        }).attach();
        return binding.getRoot();
    }
}

