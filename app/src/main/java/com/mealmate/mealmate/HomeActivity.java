package com.mealmate.mealmate;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.mealmate.mealmate.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        loadFragment(new HomeFragment());
        bottomNav();
    }

    private void clearAllNavBg() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.transparent, getTheme());
        binding.navHomeIcon.setBackground(drawable);
        binding.navCartIcon.setBackground(drawable);
        binding.navProfileIcon.setBackground(drawable);
    }

    private void bottomNav() {
        binding.navHome.setOnClickListener(v-> {
            clearAllNavBg();
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_nav_item, getTheme());
            binding.navHomeIcon.setBackground(drawable);
            loadFragment(new HomeFragment());
        });
        binding.navCart.setOnClickListener(v-> {
            clearAllNavBg();
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_nav_item, getTheme());
            binding.navCartIcon.setBackground(drawable);
            loadFragment(new CartFragment());
        });
        binding.navProfile.setOnClickListener(v-> {
            clearAllNavBg();
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_nav_item, getTheme());
            binding.navProfileIcon.setBackground(drawable);
            loadFragment(new ProfileFragment());
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
    }
}