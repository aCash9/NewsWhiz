package com.example.technews;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.technews.adapters.CategoryAdapter;
import com.example.technews.adapters.RecyclerNewsAdapter;
import com.example.technews.adapters.ViewPagerAdapter;
import com.example.technews.json.NewsResponse;
import com.example.technews.json.Article;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnCategorySelectedListener{
    private RecyclerView recyclerView;
    private RecyclerNewsAdapter adapter;
    private ArrayList<NewsObject> topNewsList;
    private ArrayList<NewsObject> categoryNewsList;
    private static final String BASE_URL = "https://newsapi.org/";
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;

    private ViewPager viewPager;
    private Dialog dialog;
    private ImageButton link;
    private ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<String> categories = new ArrayList<>(Arrays.asList("Health", "Business", "Sports", "Technology", "Science", "Entertainment"));
        categoriesRecyclerView = findViewById(R.id.categories);
        categoryAdapter = new CategoryAdapter(categories, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(layoutManager);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        link = findViewById(R.id.link);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewPager = findViewById(R.id.viewPager);

        categoryNewsList = new ArrayList<>();

        topNewsList = new ArrayList<>();
        adapter = new RecyclerNewsAdapter(this, categoryNewsList);
        recyclerView.setAdapter(adapter);

        viewPagerAdapter = new ViewPagerAdapter(this, topNewsList);
        viewPager.setPadding(100, 0, 100,0);
        viewPager.setAdapter(viewPagerAdapter);

        fetchTopHeadlinesByCategory("in", "health");
        fetchTopHeadlines();


        link.setOnClickListener(v -> {
            showAddEventDialogBox();
        });
    }
    private void fetchTopHeadlines() {
        String API_KEY = BuildConfig.NEWS_API_KEY;
        NewsApiService newsApiService = RetrofitClient.getClient(BASE_URL).create(NewsApiService.class);
        Call<NewsResponse> call = newsApiService.getTopHeadlines("in", API_KEY);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    // Handle the list of articles here
                    ArrayList<NewsObject> newNewsList = new ArrayList<>();
                    for (Article article : articles) {
                        NewsObject news = new NewsObject(
                                article.getUrlToImage(),
                                article.getTitle(),
                                article.getAuthor(),
                                article.getPublishedAt(),
                                article.getContent(),
                                article.getUrl(),
                                article.getDescription()
                        );
                        newNewsList.add(news);
                    }
                    topNewsList.clear();
                    topNewsList.addAll(newNewsList);
                    viewPagerAdapter.notifyDataSetChanged();
                } else {
                    Log.e("MainActivity", "Response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("MainActivity", "Request failed", t);
            }
        });
    }

    private void fetchTopHeadlinesByCategory(String country, String category) {
        String API_KEY = BuildConfig.NEWS_API_KEY;
        NewsApiService newsApiService = RetrofitClient.getClient(BASE_URL).create(NewsApiService.class);
        Call<NewsResponse> call = newsApiService.getTopHeadlinesByCategory(country, category, API_KEY);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    ArrayList<NewsObject> newNewsList = new ArrayList<>();
                    for (Article article : articles) {
                        NewsObject news = new NewsObject(
                                article.getUrlToImage(),
                                article.getTitle(),
                                article.getAuthor(),
                                article.getPublishedAt(),
                                article.getContent(),
                                article.getUrl(),
                                article.getDescription()
                        );
                        newNewsList.add(news);
                    }
                    categoryNewsList.clear();
                    categoryNewsList.addAll(newNewsList);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("MainActivity", "Response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("MainActivity", "Request failed", t);
            }
        });
    }

    @Override
    public void onCategorySelected(String category) {
        fetchTopHeadlinesByCategory("in", category);
    }

    private void showAddEventDialogBox() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.search_news);

        Button upload = dialog.findViewById(R.id.url);

        upload.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClip().getItemCount() > 0) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                CharSequence textToPaste = item.getText();

                if (textToPaste != null) {
                    Intent intent = new Intent(this, UserNewsActivity.class);
                    intent.putExtra("url", textToPaste.toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No text found in clipboard", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
}