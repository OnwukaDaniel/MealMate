package com.mealmate.mealmate.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.mealmate.mealmate.MealDetails;
import com.mealmate.mealmate.R;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.interfaces.AddMealPlanClickListener;
import com.mealmate.mealmate.model.OnboardingModel;

import java.util.ArrayList;
import java.util.List;

public class MealGridAdapter extends RecyclerView.Adapter<MealGridAdapter.ViewHolder> {
    public List<MealData> dataList = new ArrayList<>();
    public AddMealPlanClickListener addMealPlanClickListener;
    Context context;

    public MealGridAdapter(AddMealPlanClickListener addMealPlanClickListener) {
        this.addMealPlanClickListener = addMealPlanClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealData datum = dataList.get(position);
        Glide.with(context).load(datum.getImage()).centerCrop().into(holder.imageView);
        holder.title.setText(datum.getTitle());
        holder.btn.setOnClickListener(v-> {
            addMealPlanClickListener.onItemClicked(datum);
        });
        holder.itemView.setOnClickListener(v-> {
            Intent intent = new Intent(context, MealDetails.class);
            intent.putExtra("meal_data", datum);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.home_grid_image);
            title = itemView.findViewById(R.id.home_item_title);
            btn = itemView.findViewById(R.id.home_item_add_btn);
        }
    }
}
