package com.example.wheretogo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private CafeAdapter cafeAdapter;
    private List<Cafe> favoriteCafes = new ArrayList<>();
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        favoritesRecyclerView = findViewById(R.id.cafesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFavorites();

        cafeAdapter = new CafeAdapter(this, favoriteCafes);
        favoritesRecyclerView.setAdapter(cafeAdapter);

        backArrow = findViewById(R.id.backarrow);
        backArrow.setOnClickListener(view -> startActivity(new Intent(FavoriteActivity.this, UserHomePageActivity.class)));

    }

    private void loadFavorites() {
        SharedPreferences prefs = getSharedPreferences("UserFavorites", MODE_PRIVATE);
        String json = prefs.getString("favorites", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Cafe>>() {}.getType();
            favoriteCafes = gson.fromJson(json, type);
        }

        if (favoriteCafes == null) {
            favoriteCafes = new ArrayList<>();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
        favoritesRecyclerView.setAdapter(new FavoritesAdapter(this, favoriteCafes));
    }


}
