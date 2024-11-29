package com.example.wheretogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FirstPageActivity extends AppCompatActivity {
    private Button getStarted;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        getStarted = findViewById(R.id.getstartedbtn);
        signIn = findViewById(R.id.signinbtn);


        getStarted.setOnClickListener(view -> {
            Intent intent = new Intent(FirstPageActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        signIn.setOnClickListener(view -> {
            Intent intent = new Intent(FirstPageActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
