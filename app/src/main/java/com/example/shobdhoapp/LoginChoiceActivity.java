package com.example.shobdhoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choice);

        Button btnAdminLogin = findViewById(R.id.btnAdminLogin);
        Button btnUserLogin = findViewById(R.id.btnUserLogin);

        btnAdminLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminLoginActivity.class));
        });

        btnUserLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
