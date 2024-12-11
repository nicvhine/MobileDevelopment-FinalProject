package com.example.wheretogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserHomePageActivity extends AppCompatActivity {
    private RecyclerView cafesRecyclerView;
    private CafeAdapter cafeAdapter;
    private List<Cafe> cafeList = new ArrayList<>();
    private List<Cafe> filteredCafes = new ArrayList<>();
    private List<Cafe> originalCafes = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView signOut, favoriteIcon, settings;
    private ImageView filterIc;
    private boolean isSorted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        db = FirebaseFirestore.getInstance();
        cafesRecyclerView = findViewById(R.id.cafesRecyclerView);
        cafesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cafeAdapter = new CafeAdapter(this, filteredCafes, false);
        cafesRecyclerView.setAdapter(cafeAdapter);

        filterIc = findViewById(R.id.filteric);
        filterIc.setOnClickListener(v -> {
            if (isSorted) {
                filteredCafes.clear();
                filteredCafes.addAll(originalCafes);
                isSorted = false;
            } else {
                Collections.sort(filteredCafes, Comparator.comparing(Cafe::getName));
                isSorted = true;
            }
            cafeAdapter.notifyDataSetChanged();
        });

        fetchCafes();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCafes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCafes(newText);
                return true;
            }
        });

        signOut = findViewById(R.id.logouttext);
        signOut.setOnClickListener(v -> {
            startActivity(new Intent(UserHomePageActivity.this, LoginActivity.class));
        });

        favoriteIcon = findViewById(R.id.favoriteicon);
        favoriteIcon.setOnClickListener(v -> {
            startActivity(new Intent(UserHomePageActivity.this, FavoriteActivity.class));
        });

        settings = findViewById(R.id.settingstxt);
        settings.setOnClickListener(v -> {
            startActivity(new Intent(UserHomePageActivity.this, Settings.class));
        });
    }

    private void fetchCafes() {
        db.collection("Cafes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        cafeList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Cafe cafe = document.toObject(Cafe.class);
                            cafeList.add(cafe);
                        }
                        filteredCafes.clear();
                        filteredCafes.addAll(cafeList);

                        originalCafes.clear();
                        originalCafes.addAll(cafeList);

                        cafeAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(UserHomePageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void filterCafes(String query) {
        filteredCafes.clear();
        if (query.isEmpty()) {
            filteredCafes.addAll(cafeList);
        } else {
            for (Cafe cafe : cafeList) {
                if (cafe.getName().toLowerCase().contains(query.toLowerCase()) ||
                        cafe.getLocation().toLowerCase().contains(query.toLowerCase())) {
                    filteredCafes.add(cafe);
                }
            }
        }
        cafeAdapter.notifyDataSetChanged();
    }

}
