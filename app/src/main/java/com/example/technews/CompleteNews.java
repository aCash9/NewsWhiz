package com.example.technews;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;


public class CompleteNews extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, authorTextView, dateTextView;
    private ImageView newsImageView;
    private BlurView blurView;
    private ImageButton backButton;
    private String task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complete_news);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String author = intent.getStringExtra("author");
        String date = intent.getStringExtra("date");
        String imageUrl = intent.getStringExtra("imageUrl");
        String url = intent.getStringExtra("url");

        blurView = findViewById(R.id.blurView);
        titleTextView = findViewById(R.id.title);
        descriptionTextView = findViewById(R.id.body);
        authorTextView = findViewById(R.id.author);
        dateTextView = findViewById(R.id.date);
        backButton = findViewById(R.id.back_btn);
        newsImageView = findViewById(R.id.image);

        blurBackground();

        backButton.setOnClickListener(v -> finish());

        if(imageUrl != null)
            Glide.with(this).load(imageUrl).into(newsImageView);
        else
            errorImage();

        titleTextView.setText(title);
        authorTextView.setText(author);
        GetDate getDate = new GetDate();
        dateTextView.setText(getDate.convertDate(date));

        new FetchNewsContentTask().execute(url);
    }

    private void errorImage() {
        int random = (int) (Math.random() * 4);
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.error_image);
        images.add(R.drawable.error_image_2);
        images.add(R.drawable.error_image_3);
        images.add(R.drawable.error_image_4);
        Glide.with(this).load(images.get(random)).into(newsImageView);
    }

    private void blurBackground() {
        float radius = 25f;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
    }

    public class FetchNewsContentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            try {
                Document doc = Jsoup.connect(url).get();
                return doc.text(); // Extracts all visible text from the HTML document
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String fullText) {
            if (fullText != null) {
                // Update TextView with the fetched full text
                summarizeUsingBard(fullText);
            } else {
                // Handle case where fetching full text failed
                descriptionTextView.setText("Failed to fetch full text");
            }
        }
    }

    private void summarizeUsingBard(String desc) {
        String bardApi = BuildConfig.BARD_API_KEY;

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", bardApi);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("summarize this news article and the final text you have given must be only alphabets when copied:" + desc)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();;

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();

                runOnUiThread(() -> descriptionTextView.setText(resultText));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }

    private void setImage(String imageUrl) {
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(newsImageView);
        } else {
            errorImage();
        }
    }

}