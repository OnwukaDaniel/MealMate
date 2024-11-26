package com.mealmate.mealmate.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealmate.mealmate.R;
import com.mealmate.mealmate.model.OnboardingModel;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
    private List<OnboardingModel> dataList;
    Context context;

    public OnboardingAdapter(List<OnboardingModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnboardingModel datum = dataList.get(position);
        holder.imageView.setImageResource(datum.getImage());
        holder.title.setText(datum.getTitle());
        holder.subTitle.setText(datum.getSubTitle());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView subTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.onboarding_image);
            title = itemView.findViewById(R.id.carousel_title);
            subTitle = itemView.findViewById(R.id.carousel_sub_title);
        }
    }
}
