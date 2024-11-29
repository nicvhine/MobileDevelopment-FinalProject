package com.example.wheretogo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cafe);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        tableLayout = findViewById(R.id.tableLayout);

        // Fetch cafes data from Firestore
        fetchCafes();
    }

    private void fetchCafes() {
        db.collection("Cafes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Clear the table before adding new rows
                        tableLayout.removeAllViews();

                        // Add header row (optional)
                        TableRow headerRow = new TableRow(this);
                        TextView headerCafeName = new TextView(this);
                        headerCafeName.setText("Cafe Name");
                        headerCafeName.setPadding(8, 8, 8, 8);
                        headerRow.addView(headerCafeName);


                        tableLayout.addView(headerRow);

                        // Add rows dynamically for each cafe
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String cafeName = document.getString("name");
                            addCafeRow(cafeName, document);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void addCafeRow(String cafeName, DocumentSnapshot document) {
        // Create a new row for each cafe
        TableRow row = new TableRow(this);

        // Create TextView for Cafe Name
        TextView cafeNameTextView = new TextView(this);
        cafeNameTextView.setText(cafeName);
        cafeNameTextView.setPadding(8, 8, 8, 8);
        row.addView(cafeNameTextView);

        // Create Edit Details button
        Button editDetailsButton = new Button(this);
        editDetailsButton.setText("Edit Details");
        editDetailsButton.setPadding(8, 8, 8, 8);
        editDetailsButton.setOnClickListener(v -> {
            // Show the edit dialog with the current details
            editCafeDetails(document);
        });

        row.addView(editDetailsButton);

        // Create Delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setPadding(8, 8, 8, 8);
        deleteButton.setOnClickListener(v -> {
            // Delete the cafe from Firestore
            deleteCafe(document);
        });

        row.addView(deleteButton);

        // Add row to TableLayout
        tableLayout.addView(row);
    }

    private void editCafeDetails(DocumentSnapshot cafeDocument) {
        // Create an AlertDialog to edit cafe details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Cafe Details");

        // Set up the input fields for editing cafe details
        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_edit_cafe, null);
        EditText cafeNameEditText = dialogView.findViewById(R.id.cafeNameEditText);
        EditText locationEditText = dialogView.findViewById(R.id.locationEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);

        // Pre-fill the fields with current values
        cafeNameEditText.setText(cafeDocument.getString("name"));
        locationEditText.setText(cafeDocument.getString("location"));
        descriptionEditText.setText(cafeDocument.getString("description"));

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get the updated values from the input fields
            String updatedCafeName = cafeNameEditText.getText().toString().trim();
            String updatedLocation = locationEditText.getText().toString().trim();
            String updatedDescription = descriptionEditText.getText().toString().trim();

            if (updatedCafeName.isEmpty() || updatedLocation.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(ManageCafeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the cafe details in Firestore
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

        // Show the dialog
        builder.create().show();
    }


    private void deleteCafe(DocumentSnapshot cafeDocument) {
        // Delete the cafe from Firestore
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
