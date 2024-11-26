package com.mealmate.mealmate.adapter;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealmate.mealmate.R;
import com.mealmate.mealmate.interfaces.IngredientsFillListener;
import com.mealmate.mealmate.model.OnboardingModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private Map<String, String> dataList = new LinkedHashMap<>();
    private final IngredientsFillListener listener;
    Context context;

    public IngredientAdapter(Map<String, String> dataList, IngredientsFillListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<String> keys = new ArrayList<>(dataList.keySet());
        List<String> values = new ArrayList<>(dataList.values());
        String key = keys.get(position);
        String value = values.get(position);
        System.out.println("Got key **************** "+ key);
        holder.title.setText(key);
        holder.quantity.setText(value);
        holder.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    listener.onItemFilled(s.toString(), holder.getAdapterPosition(), true);
                }
            }
        });
        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    listener.onItemFilled(s.toString(), holder.getAdapterPosition(), false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.values().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText title;
        EditText quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ingredient_name);
            quantity = itemView.findViewById(R.id.ingredient_quantity);
        }
    }
}
