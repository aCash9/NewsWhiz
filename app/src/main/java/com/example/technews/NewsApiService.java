package com.example.technews;

import com.example.technews.json.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlinesByCategory(@Query("country") String country,
                                                 @Query("category") String category,
                                                 @Query("apiKey") String apiKey);
}
