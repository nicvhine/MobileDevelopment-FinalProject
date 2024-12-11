package com.example.wheretogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import android.view.View;


public class AddCafeActivity extends AppCompatActivity {

    private EditText cafeNameField, locationField, descriptionField, phoneNumberField, emailField;
    private ImageView cafeImagePreview;
    private Button uploadImageButton, addCafeButton;
    private ImageView backButton;
    private Uri selectedImageUri;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cafe);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cafeNameField = findViewById(R.id.cafeNameField);
        locationField = findViewById(R.id.locationField);
        descriptionField = findViewById(R.id.descriptionField);
        phoneNumberField = findViewById(R.id.phonenumberfield);
        emailField = findViewById(R.id.emailfield);
        cafeImagePreview = findViewById(R.id.cafeImagePreview);
        backButton = findViewById(R.id.backbutton);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        addCafeButton = findViewById(R.id.addCafeButton);

        backButton.setOnClickListener(view -> startActivity(new Intent(AddCafeActivity.this, AdminHomePageActivity.class)));

        ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        cafeImagePreview.setImageURI(uri);
                        cafeImagePreview.setVisibility(View.VISIBLE);  // Show image preview
                    }
                });

        uploadImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        addCafeButton.setOnClickListener(v -> addCafe());
    }

    private void addCafe() {
        String cafeName = cafeNameField.getText().toString().trim();
        String location = locationField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();
        String phoneNumber = phoneNumberField.getText().toString().trim();
        String emailCafe = emailField.getText().toString().trim();

        if (cafeName.isEmpty() || location.isEmpty() || description.isEmpty() || phoneNumber.isEmpty() || emailCafe.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill in all fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(emailCafe)) {
            emailField.setError("Invalid email address");
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            phoneNumberField.setError("Invalid phone number");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding cafe...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String base64Image = encodeImageToBase64(selectedImageUri);

        if (base64Image == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error encoding image", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cafe = new HashMap<>();
        cafe.put("name", cafeName);
        cafe.put("location", location);
        cafe.put("description", description);
        cafe.put("phonenumber", phoneNumber);
        cafe.put("email", emailCafe);
        cafe.put("imageBase64", base64Image);
        cafe.put("addedBy", mAuth.getCurrentUser().getUid());

        db.collection("Cafes").add(cafe)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddCafeActivity.this, "Cafe added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddCafeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("[0-9]+") && phoneNumber.length() >= 10;
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();
            float aspectRatio = (float) width / height;
            int newWidth = 500;
            int newHeight = (int) (newWidth / aspectRatio);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
