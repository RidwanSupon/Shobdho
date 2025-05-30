package com.example.shobdhoapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shobdhoapp.databinding.ActivityAdminLoginBinding;

public class AdminLoginActivity extends AppCompatActivity {

    private ActivityAdminLoginBinding binding;

    // Hardcoded admin credentials (change as needed)
    private static final String ADMIN_ID = "admin@example.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.adminLoginBtn.setOnClickListener(v -> {
            String adminId = binding.adminIdEt.getText().toString().trim();
            String adminPass = binding.adminPasswordEt.getText().toString().trim();

            if (TextUtils.isEmpty(adminId) || TextUtils.isEmpty(adminPass)) {
                Toast.makeText(this, "Please enter admin ID and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (adminId.equals(ADMIN_ID) && adminPass.equals(ADMIN_PASSWORD)) {
                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
                startAdminPanel();
                finish();
            } else {
                Toast.makeText(this, "Invalid admin ID or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAdminPanel() {
        Intent intent = new Intent(this, AdminPanelActivity.class);
        startActivity(intent);
    }
}
