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
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.technews.CompleteNews;
import com.example.technews.NewsObject;
import com.example.technews.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<NewsObject> newsObjects;

    public ViewPagerAdapter(Context context, ArrayList<NewsObject> newsObjects) {
        this.context = context;
        this.newsObjects = newsObjects;
    }

    @Override
    public int getCount() {
        return newsObjects.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.top_news_row, container, false);
        container.addView(view, 0);
        ImageView imageView = view.findViewById(R.id.image);
        TextView title = view.findViewById(R.id.title);
        TextView author = view.findViewById(R.id.author);

        NewsObject news = newsObjects.get(position);

        if(news.getImageURl() != null)
            Glide.with(context).load(news.getImageURl()).into(imageView);
        else
            errorImage(imageView);
        title.setText(news.getTitle());
        author.setText("by " + news.getAuthor());

        imageView.setOnClickListener(v -> {
            fullNews(news);
        });

        title.setOnClickListener(v -> {
            fullNews(news);
        });

        author.setOnClickListener(v -> {
            fullNews(news);
        });

        return view;
    }

    private void errorImage(ImageView imageView) {
        int random = (int) (Math.random() * 4);
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.error_image);
        images.add(R.drawable.error_image_2);
        images.add(R.drawable.error_image_3);
        images.add(R.drawable.error_image_4);
        Glide.with(context).load(images.get(random)).into(imageView);
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
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
