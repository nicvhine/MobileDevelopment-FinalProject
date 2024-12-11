package com.example.wheretogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import android.view.View;

public class ActivityAddProfile extends AppCompatActivity {

    private ShapeableImageView circularImageView;
    private Button addImageButton;
    private ImageView next;
    private Uri selectedImageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    circularImageView.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        circularImageView = findViewById(R.id.circularImageView);
        addImageButton = findViewById(R.id.button);
        next = findViewById(R.id.next);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        addImageButton.setOnClickListener(v -> openImagePicker());

        next.setOnClickListener(v -> uploadProfilePictureAndProceed());
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void uploadProfilePictureAndProceed() {
        if (selectedImageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading profile picture...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String base64Image = encodeImageToBase64(selectedImageUri);

            if (base64Image == null) {
                progressDialog.dismiss();
                Toast.makeText(ActivityAddProfile.this, "Error encoding image", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = mAuth.getCurrentUser().getUid();
            Map<String, Object> user = new HashMap<>();
            user.put("profilepic", base64Image);

            db.collection("Users").document(userId)
                    .update(user)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityAddProfile.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ActivityAddProfile.this, UserHomePageActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityAddProfile.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ActivityAddProfile.this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
        }
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

