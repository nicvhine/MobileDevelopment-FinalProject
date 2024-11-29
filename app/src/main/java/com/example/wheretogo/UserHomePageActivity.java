package com.example.wheretogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserHomePageActivity extends AppCompatActivity {
    private RecyclerView cafesRecyclerView;
    private CafeAdapter cafeAdapter;
    private List<Cafe> cafeList = new ArrayList<>();
    private List<Cafe> filteredCafes = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        db = FirebaseFirestore.getInstance();
        cafesRecyclerView = findViewById(R.id.cafesRecyclerView);

        cafesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cafeAdapter = new CafeAdapter(this, filteredCafes);
        cafesRecyclerView.setAdapter(cafeAdapter);

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
            Intent intent = new Intent(UserHomePageActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }

    private void fetchCafes() {
        db.collection("Cafes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        cafeList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Cafe cafe = document.toObject(Cafe.class);
                            cafeList.add(cafe);
                        }
                        filteredCafes.addAll(cafeList);
                        cafeAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserHomePageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
