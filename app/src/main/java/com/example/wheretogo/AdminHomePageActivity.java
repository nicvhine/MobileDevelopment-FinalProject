package com.example.wheretogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Import LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminHomePageActivity extends AppCompatActivity {
    private RecyclerView cafesRecyclerView;
    private CafeAdapter cafeAdapter;
    private List<Cafe> cafeList = new ArrayList<>();
    private List<Cafe> filteredCafes = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView addCafe;
    private TextView signOut;
    private TextView manageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        cafesRecyclerView = findViewById(R.id.cafesRecyclerView);

        // Set LinearLayoutManager for one item per row
        cafesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with filtered cafes list
        cafeAdapter = new CafeAdapter(this, filteredCafes);
        cafesRecyclerView.setAdapter(cafeAdapter);

        // Fetch cafes from Firestore
        fetchCafes();

        // Setup SearchView for filtering
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

        // Initialize other buttons (Add Cafe, SignOut, Manage List)
        addCafe = findViewById(R.id.addCafeButton);
        signOut = findViewById(R.id.logouttext);
        manageList = findViewById(R.id.manage);

        // Add Cafe button action
        addCafe.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, AddCafeActivity.class);
            startActivity(intent);
        });

        // Manage List button action
        manageList.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageCafeActivity.class);
            startActivity(intent);
        });

        // Sign Out button action
        signOut.setOnClickListener(v -> {
            finish();
        });
    }

    // Fetch cafes from Firestore
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
                        filteredCafes.addAll(cafeList); // Initially display all cafes
                        cafeAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminHomePageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Filter cafes based on search query
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
