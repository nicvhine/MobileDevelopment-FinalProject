package com.example.wheretogo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserDashboardActivity extends AppCompatActivity {

    private Button exploreButton, logoutButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        exploreButton = findViewById(R.id.exploreButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Explore recommendations
        exploreButton.setOnClickListener(v -> {
            // Navigate to a screen that shows the recommendations (e.g., RecommendationListActivity)
//            startActivity(new Intent(UserDashboardActivity.this, RecommendationListActivity.class));
        });

        // Handle logout action
//        logoutButton.setOnClickListener(v -> {
//            mAuth.signOut();
//            startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
//            finish();
//        });

        // Fetch all recommendations (just like the admin does, but for display purposes)
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        db.collection("Recommendations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String category = document.getString("category");
                            String description = document.getString("description");

                            // You can display these recommendations in a list (e.g., RecyclerView)
                            // For simplicity, we'll just log the recommendation
                            System.out.println("Recommendation: " + name + ", Category: " + category);
                        }
                    } else {
                        Toast.makeText(UserDashboardActivity.this, "Error getting recommendations.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
