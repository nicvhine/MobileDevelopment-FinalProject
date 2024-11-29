package com.example.wheretogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManageCafeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TableLayout tableLayout;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cafe);

        db = FirebaseFirestore.getInstance();
        tableLayout = findViewById(R.id.tableLayout);
        backButton = findViewById(R.id.backbutton);
        fetchCafes();

        backButton.setOnClickListener(view -> startActivity(new Intent(ManageCafeActivity.this, AdminHomePageActivity.class)));

    }

    private void fetchCafes() {
        db.collection("Cafes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        tableLayout.removeAllViews();



                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String cafeName = document.getString("name");
                            addCafeRow(cafeName, document);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching cafes", Toast.LENGTH_SHORT).show();
                });
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
        editDetailsButton.setPadding(16, 16, 16, 16);
        editDetailsButton.setBackgroundResource(R.drawable.button_style);
        editDetailsButton.setTextColor(getResources().getColor(R.color.white));
        editDetailsButton.setOnClickListener(v -> editCafeDetails(document));

        row.addView(editDetailsButton);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setPadding(16, 16, 16, 16);
        deleteButton.setBackgroundResource(R.drawable.button_style);
        deleteButton.setTextColor(getResources().getColor(R.color.white));
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

        cafeNameEditText.setText(cafeDocument.getString("name"));
        locationEditText.setText(cafeDocument.getString("location"));
        descriptionEditText.setText(cafeDocument.getString("description"));

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedCafeName = cafeNameEditText.getText().toString().trim();
            String updatedLocation = locationEditText.getText().toString().trim();
            String updatedDescription = descriptionEditText.getText().toString().trim();

            if (updatedCafeName.isEmpty() || updatedLocation.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(ManageCafeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            cafeDocument.getReference()
                    .update("name", updatedCafeName, "location", updatedLocation, "description", updatedDescription)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ManageCafeActivity.this, "Cafe details updated successfully", Toast.LENGTH_SHORT).show();
                        fetchCafes();  // Refresh the table with updated details
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ManageCafeActivity.this, "Error updating cafe", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void deleteCafe(DocumentSnapshot cafeDocument) {
        cafeDocument.getReference().delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ManageCafeActivity.this, "Cafe deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchCafes();  // Refresh the table after deletion
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManageCafeActivity.this, "Error deleting cafe", Toast.LENGTH_SHORT).show();
                });
    }



}