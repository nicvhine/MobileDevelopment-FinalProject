package com.example.wheretogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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
    private ImageView refreshPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

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

        addCafe = findViewById(R.id.addCafeButton);
        signOut = findViewById(R.id.logouttext);
        manageList = findViewById(R.id.manage);

        addCafe.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, AddCafeActivity.class);
            startActivity(intent);
        });

        manageList.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageCafeActivity.class);
            startActivity(intent);
        });


        signOut.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        manageList.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                manageList.setTextColor(getResources().getColor(android.R.color.holo_orange_light));  // Yellowish color
            } else {
                manageList.setTextColor(getResources().getColor(android.R.color.white));
            }
        });
    }

    private void fetchCafes() {
        db.collection("Cafes").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(AdminHomePageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (queryDocumentSnapshots != null) {
                cafeList.clear();
                filteredCafes.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Cafe cafe = document.toObject(Cafe.class);
                    cafeList.add(cafe);
                }
                filteredCafes.addAll(cafeList);
                cafeAdapter.notifyDataSetChanged();
            }
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
