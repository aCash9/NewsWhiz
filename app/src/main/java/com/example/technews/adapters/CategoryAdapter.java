package com.example.technews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.technews.OnCategorySelectedListener;
import com.example.technews.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories;
    private int selectedPosition = 0; // Initially selected position
    private OnCategorySelectedListener listener;
    public CategoryAdapter(List<String> categories, OnCategorySelectedListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(position);
        holder.categoryButton.setText(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private Button categoryButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryButton = itemView.findViewById(R.id.button);

            categoryButton.setOnClickListener(v -> {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);

                listener.onCategorySelected(categories.get(selectedPosition));
            });
        }

        public void bind(int position) {
            categoryButton.setText(categories.get(position));
            if (position == selectedPosition) {
                // Highlight the selected category button\
                categoryButton.setBackgroundResource(R.drawable.custom_selected_category    );
                categoryButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else {
                // Unhighlight the previously selected category button
                categoryButton.setBackgroundResource(R.drawable.custom_unselected_category);
                categoryButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
            }
        }
    }

    // Helper method to update categories
    public void updateCategories(List<String> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
}

