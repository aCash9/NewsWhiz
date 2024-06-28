package com.example.technews.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.technews.CompleteNews;
import com.example.technews.GetDate;
import com.example.technews.NewsObject;
import com.example.technews.R;

import java.util.ArrayList;
import java.util.List;


public class RecyclerNewsAdapter extends RecyclerView.Adapter<RecyclerNewsAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<NewsObject> list;

    public RecyclerNewsAdapter(Context context, ArrayList<NewsObject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.news_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerNewsAdapter.ViewHolder holder, int position) {
        NewsObject news = list.get(position);
        if(news.getImageURl() != null)
            Glide.with(context).load(news.getImageURl()).into(holder.image);
        else
            setErrorImage(holder);

        holder.title.setText(news.getTitle());
        holder.author.setText(news.getAuthor());

        GetDate getDate = new GetDate();
        holder.date.setText(getDate.convertDate(news.getDate()));

        holder.title.setOnClickListener(v -> {
            fullNews(news);
        });
        holder.date.setOnClickListener(v -> {
            fullNews(news);
        });
        holder.author.setOnClickListener(v -> {
            fullNews(news);
        });

        holder.image.setOnClickListener(v -> {
            fullNews(news);
        });

    }

    private void setErrorImage(@NonNull ViewHolder holder) {
        int random = (int) (Math.random() * 4);
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.error_image);
        images.add(R.drawable.error_image_2);
        images.add(R.drawable.error_image_3);
        images.add(R.drawable.error_image_4);
        Glide.with(context).load(images.get(random)).into(holder.image);
    }

    private void fullNews(NewsObject news) {
        Intent intent = new Intent(context, CompleteNews.class);
        intent.putExtra("title", news.getTitle());
        intent.putExtra("author", news.getAuthor());
        intent.putExtra("imageUrl", news.getImageURl());
        intent.putExtra("description", news.getDescription());
        intent.putExtra("date", news.getDate());
        intent.putExtra("url", news.getUrl());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, author, date;

        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
            cardView = itemView.findViewById(R.id.cardView);
        }

    }
}
