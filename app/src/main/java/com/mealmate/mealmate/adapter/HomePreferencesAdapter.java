package com.mealmate.mealmate.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mealmate.mealmate.R;
import com.mealmate.mealmate.data.model.MealData;
import com.mealmate.mealmate.interfaces.PreferenceClickListener;

import java.util.ArrayList;
import java.util.List;

public class HomePreferencesAdapter extends RecyclerView.Adapter<HomePreferencesAdapter.ViewHolder> {
    public List<String> dataList;
    private PreferenceClickListener listener;
    Context context;

    public HomePreferencesAdapter(List<String> dataList, PreferenceClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_preferences_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataList.get(position));
        holder.title.setOnClickListener(v-> listener.onItemClicked(dataList.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.home_item_title);
        }
    }
}
