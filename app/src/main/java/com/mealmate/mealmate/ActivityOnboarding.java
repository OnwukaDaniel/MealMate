package com.mealmate.mealmate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.mealmate.mealmate.adapter.OnboardingAdapter;
import com.mealmate.mealmate.databinding.ActivityOnboardingBinding;
import com.mealmate.mealmate.model.OnboardingModel;
import com.mealmate.mealmate.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityOnboarding extends AppCompatActivity {

    ActivityOnboardingBinding binding;
    List<OnboardingModel> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        dataList.add(new OnboardingModel(
                "Personalized Meal Planning",
                "Pick your week's meal in minutes with over 200 personalization options, eat exactly how you want to eat",
                R.drawable.onboarding1
        ));
        dataList.add(new OnboardingModel(
                "Simple, Stress-free grocery shopping",
                "Grocery shop oce in a week with an organized done-for-you shopping list.",
                R.drawable.onboarding2
        ));
        dataList.add(new OnboardingModel(
                "Delicious, Healthy meal made easily",
                "Easily cook delicious, healthy meal in about 30min from start to finish",
                R.drawable.onboarding3
        ));
        OnboardingAdapter adapter = new OnboardingAdapter(dataList);
        binding.onboardingViewpager.setAdapter(adapter);
        binding.onboardingViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.onboardingViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                setIndicator(position);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                String stateDescription = state == ViewPager2.SCROLL_STATE_IDLE ? "Idle" :
                        state == ViewPager2.SCROLL_STATE_DRAGGING ? "Dragging" :
                                "Settling";

            }
        });
        binding.onboardingContinue.setOnClickListener(v->{
            int index = binding.onboardingViewpager.getCurrentItem();
            System.out.println("Index **************** " + index);

            switch (index) {
                case 0:
                case 1:
                    binding.onboardingViewpager.setCurrentItem(index + 1);
                    break;
                case 2:
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        });
    }

    public void defaultSize() {
        ViewGroup.LayoutParams params = binding.indicator1.getLayoutParams();
        ViewGroup.LayoutParams params2 = binding.indicator2.getLayoutParams();
        ViewGroup.LayoutParams params3 = binding.indicator3.getLayoutParams();
        params.width = 30;
        params2.width = 30;
        params3.width = 30;
        binding.indicator1.setLayoutParams(params);
        binding.indicator2.setLayoutParams(params2);
        binding.indicator3.setLayoutParams(params3);
    }

    public void setIndicator(Integer index) {
        switch (index) {
            case 0:
                defaultSize();
                binding.indicator1.setCardBackgroundColor(Color.parseColor("#577B00"));
                binding.indicator2.setCardBackgroundColor(Color.GRAY);
                binding.indicator3.setCardBackgroundColor(Color.GRAY);
                ViewGroup.LayoutParams params = binding.indicator1.getLayoutParams();
                params.width = 90;
                binding.indicator1.setLayoutParams(params);
                break;
            case 1:
                defaultSize();
                binding.indicator1.setCardBackgroundColor(Color.GRAY);
                binding.indicator2.setCardBackgroundColor(Color.parseColor("#577B00"));
                binding.indicator3.setCardBackgroundColor(Color.GRAY);
                ViewGroup.LayoutParams params2 = binding.indicator2.getLayoutParams();
                params2.width = 90;
                binding.indicator2.setLayoutParams(params2);
                break;
            case 2:
                defaultSize();
                binding.indicator1.setCardBackgroundColor(Color.GRAY);
                binding.indicator2.setCardBackgroundColor(Color.GRAY);
                binding.indicator3.setCardBackgroundColor(Color.parseColor("#577B00"));
                ViewGroup.LayoutParams params3 = binding.indicator3.getLayoutParams();
                params3.width = 90;
                binding.indicator3.setLayoutParams(params3);
                break;
            default:
                break;
        }
    }
}