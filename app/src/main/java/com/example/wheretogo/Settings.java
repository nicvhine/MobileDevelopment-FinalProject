package com.example.wheretogo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    private ImageView profileImageView, backArrow;
    private TextView nameTextView, emailTextView;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button updateProfileButton, deleteProfileButton;
    private ImageView editProfileButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        editProfileButton = findViewById(R.id.editProfileButton);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        deleteProfileButton = findViewById(R.id.deleteProfileButton);
        backArrow = findViewById(R.id.backarrow);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backArrow.setOnClickListener(view -> startActivity(new Intent(Settings.this, UserHomePageActivity.class)));


        fetchUserProfile();

        editProfileButton.setOnClickListener(v -> {
            toggleEditProfile(true);
        });

        updateProfileButton.setOnClickListener(v -> {
            updateUserProfile();
        });

        deleteProfileButton.setOnClickListener(v -> {
            deleteProfile();
        });
    }

    private void fetchUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String base64Image = documentSnapshot.getString("profilepic");

                nameTextView.setText(name);
                emailTextView.setText(email);

                if (base64Image != null && !base64Image.isEmpty()) {
                    Bitmap bitmap = decodeBase64ToBitmap(base64Image);
                    profileImageView.setImageBitmap(bitmap);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(Settings.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void toggleEditProfile(boolean isEditing) {
        nameEditText.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        emailEditText.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        passwordEditText.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        updateProfileButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);

        editProfileButton.setEnabled(!isEditing);
        editProfileButton.setVisibility(isEditing ? View.GONE : View.VISIBLE);
    }

    private void updateUserProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put("name", name);

        if (!email.isEmpty()) {
            updatedProfile.put("email", email);
        }

        if (!password.isEmpty()) {
            updatedProfile.put("password", password);
        }

        db.collection("Users").document(userId)
                .update(updatedProfile)
                .addOnSuccessListener(aVoid -> {
                    toggleEditProfile(false);
                    nameTextView.setText(name);

                    emailTextView.setText(email.isEmpty() ? emailTextView.getText().toString() : email);

                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void deleteProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteProfile", "User document deleted from Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteProfile", "Error deleting Firestore document", e);
                });

        mAuth.getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Settings.this, "Profile deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Settings.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Settings.this, "Error deleting account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}