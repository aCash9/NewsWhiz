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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

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

public class UserNewsActivity extends AppCompatActivity {
    private TextView titleTextView, descriptionTextView, authorTextView, dateTextView;
    private ImageView newsImageView;
    private BlurView blurView;
    private ImageButton backButton;
    private LottieAnimationView lottieAnimationView;

    private String task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_news);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
        Intent intent = getIntent();
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
                getInfo(fullText);
            } else {
                // Handle case where fetching full text failed
                descriptionTextView.setText("Failed to fetch full text");
            }
        }
    }

    private void getInfo(String text) {
        String bardApi = BuildConfig.BARD_API_KEY;

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", bardApi);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("Extract the following information from the news article text: title, author name, image URL, date, and description. Here is the article text:\n" + text)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.d("BardAPI", "Response: " + resultText); // Log the response

                // Parsing the formatted string
                String title = extractInfo(resultText, "Title:");
                String author = extractInfo(resultText, "Author Name:");
                String imageUrl = extractInfo(resultText, "Image URL:");
                String date = extractInfo(resultText, "Date:");
                String description = extractInfo(resultText, "Description:");

                runOnUiThread(() -> {
                    // Set the extracted information to the respective views
                    titleTextView.setText(title.isEmpty() ? "Title not found" : title);
                    authorTextView.setText(author.isEmpty() ? "Author not found" : author);
                    errorImage();
                    dateTextView.setText(date.isEmpty() ? "Date not found" : date);
                    descriptionTextView.setText(description.isEmpty() ? "Description not found" : description);
                    lottieAnimationView.pauseAnimation();
                    lottieAnimationView.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> descriptionTextView.setText("Request failed"));
            }
        }, executor);
    }

    private String extractInfo(String text, String label) {
        int startIndex = text.indexOf("**" + label + "**");
        if (startIndex == -1) return "";
        startIndex += label.length() + 4; // Move past the label and the formatting
        int endIndex = text.indexOf("\n", startIndex);
        if (endIndex == -1) endIndex = text.length();
        return text.substring(startIndex, endIndex).trim();
    }

}