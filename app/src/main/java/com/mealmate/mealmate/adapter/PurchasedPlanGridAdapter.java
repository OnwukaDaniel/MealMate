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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mealmate.mealmate.MealDetails;
import com.mealmate.mealmate.R;
import com.mealmate.mealmate.data.model.MealData;

import java.util.ArrayList;
import java.util.List;

public class PurchasedPlanGridAdapter extends RecyclerView.Adapter<PurchasedPlanGridAdapter.ViewHolder> {
    public List<MealData> dataList = new ArrayList<>();
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Context context;

    public PurchasedPlanGridAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_plan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealData data = dataList.get(position);
        Glide.with(context).load(data.getImage()).centerCrop().into(holder.imageView);
        holder.title.setText(data.getTitle());
        holder.remove.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            firestore.collection("my_purchased").document(user.getUid()).collection("my_meals").document(data.getTitle() + data.getType()).delete();
        });
        holder.itemView.setOnClickListener(v-> {
            Intent intent = new Intent(context, MealDetails.class);
            intent.putExtra("meal_data", data);
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
        Button add;
        Button remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.meal_purchase_image);
            title = itemView.findViewById(R.id.meal_purchase_title);
            remove = itemView.findViewById(R.id.remove_from_purchase);
        }
    }
}
