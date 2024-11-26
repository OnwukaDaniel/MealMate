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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mealmate.mealmate.MealDetails;
import com.mealmate.mealmate.NewMealActivity;
import com.mealmate.mealmate.R;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.interfaces.AddMealPlanClickListener;

import java.util.ArrayList;
import java.util.List;

public class ProfilePlanAdapter extends RecyclerView.Adapter<ProfilePlanAdapter.ViewHolder> {
    public List<MealData> dataList = new ArrayList<>();
    public AddMealPlanClickListener addMealPlanClickListener;
    Context context;

    public ProfilePlanAdapter(AddMealPlanClickListener addMealPlanClickListener) {
        this.addMealPlanClickListener = addMealPlanClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_plan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealData datum = dataList.get(position);
        Glide.with(context).load(datum.getImage()).centerCrop().into(holder.imageView);
        holder.title.setText(datum.getTitle());
        holder.btn.setOnClickListener(v -> {
            addMealPlanClickListener.onItemClicked(datum);
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealDetails.class);
            intent.putExtra("meal_data", datum);
            context.startActivity(intent);
        });
        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewMealActivity.class);
            intent.putExtra("meal_data", datum);
            context.startActivity(intent);
        });
        holder.delete.setOnClickListener(v -> {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String collectionName = "my_created_plans";
            if (user == null) return;
            firestore.collection(collectionName)
                    .document(user.getUid())
                    .collection("my_meals")
                    .document(datum.getId())
                    .delete()
                    .addOnSuccessListener(i -> Snackbar.make(holder.itemView, "Deleted", Snackbar.LENGTH_LONG).show()).addOnFailureListener(x -> Snackbar.make(holder.itemView, "Delete failed. Please retry.", Snackbar.LENGTH_LONG).show());
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView edit;
        CardView delete;
        TextView title;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_plan_image);
            edit = itemView.findViewById(R.id.my_plan_edit);
            delete = itemView.findViewById(R.id.my_plan_delete);
            title = itemView.findViewById(R.id.profile_plan_title);
            btn = itemView.findViewById(R.id.profile_plan_add_btn);
        }
    }
}
