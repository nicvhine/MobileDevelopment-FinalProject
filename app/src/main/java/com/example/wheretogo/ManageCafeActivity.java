package com.example.wheretogo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ManageCafeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TableLayout tableLayout;
    private ImageView backButton;
    private SearchView searchBar;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ImageView currentImagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cafe);

        db = FirebaseFirestore.getInstance();
        tableLayout = findViewById(R.id.tableLayout);
        backButton = findViewById(R.id.backbutton);
        searchBar = findViewById(R.id.searchbar);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && currentImagePreview != null) {
                        selectedImageUri = uri;
                        currentImagePreview.setImageURI(uri);
                    }
                }
        );

        fetchCafes();

        backButton.setOnClickListener(view -> startActivity(new Intent(ManageCafeActivity.this, AdminHomePageActivity.class)));

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }

    private void fetchCafes() {
        db.collection("Cafes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tableLayout.removeAllViews();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String cafeName = document.getString("name");
                        addCafeRow(cafeName, document);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching cafes", Toast.LENGTH_SHORT).show());
    }

    private void filterCafes(String query) {
        db.collection("Cafes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tableLayout.removeAllViews();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String cafeName = document.getString("name");
                        if (cafeName != null && cafeName.toLowerCase().contains(query.toLowerCase())) {
                            addCafeRow(cafeName, document);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching cafes", Toast.LENGTH_SHORT).show());
    }

    private void addCafeRow(String cafeName, DocumentSnapshot document) {
        TableRow row = new TableRow(this);

        TextView cafeNameTextView = new TextView(this);
        cafeNameTextView.setText(cafeName);
        cafeNameTextView.setPadding(16, 16, 16, 16);
        cafeNameTextView.setTextSize(16);
        cafeNameTextView.setTextColor(getResources().getColor(R.color.white));
        row.addView(cafeNameTextView);

        Button editDetailsButton = new Button(this);
        editDetailsButton.setText("Edit");
        editDetailsButton.setOnClickListener(v -> editCafeDetails(document));
        row.addView(editDetailsButton);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> deleteCafe(document));
        row.addView(deleteButton);

        row.setBackgroundColor(getResources().getColor(R.color.gray));
        tableLayout.addView(row);
    }

    private void editCafeDetails(DocumentSnapshot cafeDocument) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Cafe Details");

        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_edit_cafe, null);
        EditText cafeNameEditText = dialogView.findViewById(R.id.cafeNameEditText);
        EditText locationEditText = dialogView.findViewById(R.id.locationEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);  // Added Phone Edit
        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);  // Added Email Edit
        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);
        Button changeImageButton = dialogView.findViewById(R.id.changeImageButton);

        cafeNameEditText.setText(cafeDocument.getString("name"));
        locationEditText.setText(cafeDocument.getString("location"));
        descriptionEditText.setText(cafeDocument.getString("description"));
        phoneEditText.setText(cafeDocument.getString("phonenumber"));
        emailEditText.setText(cafeDocument.getString("email"));

        String imageBase64 = cafeDocument.getString("imageBase64");
        if (imageBase64 != null) {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imagePreview.setImageBitmap(decodedByte);
        }

        changeImageButton.setOnClickListener(v -> {
            currentImagePreview = imagePreview;
            imagePickerLauncher.launch("image/*");
        });

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedCafeName = cafeNameEditText.getText().toString().trim();
            String updatedLocation = locationEditText.getText().toString().trim();
            String updatedDescription = descriptionEditText.getText().toString().trim();
            String updatedPhoneNumber = phoneEditText.getText().toString().trim();
            String updatedEmail = emailEditText.getText().toString().trim();

            if (updatedCafeName.isEmpty() || updatedLocation.isEmpty() || updatedDescription.isEmpty() || updatedPhoneNumber.isEmpty() || updatedEmail.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            cafeDocument.getReference()
                    .update("name", updatedCafeName, "location", updatedLocation, "description", updatedDescription,
                            "phonenumber", updatedPhoneNumber, "email", updatedEmail)
                    .addOnSuccessListener(aVoid -> {
                        if (selectedImageUri != null) {
                            String updatedImageBase64 = encodeImageToBase64(selectedImageUri);
                            if (updatedImageBase64 != null) {
                                cafeDocument.getReference().update("imageBase64", updatedImageBase64);
                            }
                        }
                        Toast.makeText(this, "Cafe updated successfully", Toast.LENGTH_SHORT).show();
                        fetchCafes();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error updating cafe", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }



    private void deleteCafe(DocumentSnapshot cafeDocument) {
        cafeDocument.getReference().delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cafe deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchCafes();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting cafe", Toast.LENGTH_SHORT).show());
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
