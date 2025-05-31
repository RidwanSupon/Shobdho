package com.example.shobdhoapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shobdhoapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference wordsRef, usersRef;
    private WordAdapter wordAdapter;
    private ArrayList<Word> wordList;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Redirect if not logged in
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginChoiceActivity.class));
            finish();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        wordsRef = database.getReference("words");
        usersRef = database.getReference("users");

        wordList = new ArrayList<>();

        // Initialize adapter with default user mode (non-admin)
        wordAdapter = new WordAdapter(wordList, false, null);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(wordAdapter);

        // Logout button functionality
        binding.btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginChoiceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Add TextWatcher for search bar to filter list
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* no-op */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (wordAdapter != null) {
                    wordAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { /* no-op */ }
        });

        checkUserRoleAndSetupUI();
        loadWords();
    }

    private void checkUserRoleAndSetupUI() {
        String uid = auth.getCurrentUser().getUid();

        usersRef.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                isAdmin = "admin".equals(role);

                if (isAdmin) {
                    binding.fabAdd.setVisibility(View.VISIBLE);
                    binding.fabAdd.setOnClickListener(v -> {
                        // Open AdminActivity to add/edit words
                        startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
                    });
                } else {
                    binding.fabAdd.setVisibility(View.GONE);
                }

                // Setup adapter with listener depending on role
                WordAdapter.OnWordActionListener listener = null;
                if (isAdmin) {
                    listener = new WordAdapter.OnWordActionListener() {
                        @Override
                        public void onEditWord(Word word) {
                            // Implement edit functionality if needed
                        }

                        @Override
                        public void onDeleteWord(Word word) {
                            // Implement delete functionality if needed
                        }
                    };
                }

                // Create new adapter with updated role and listener
                wordAdapter = new WordAdapter(wordList, isAdmin, listener);
                binding.recyclerView.setAdapter(wordAdapter);

                // Apply current search filter after adapter reset
                wordAdapter.filter(binding.searchBar.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to get user role", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWords() {
        wordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wordList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Word word = ds.getValue(Word.class);
                    if (word != null) {
                        word.setKey(ds.getKey());
                        wordList.add(word);
                    }
                }

                if (wordAdapter != null) {
                    // Update adapter's original list and apply current filter
                    wordAdapter.updateFullList(wordList);
                    wordAdapter.filter(binding.searchBar.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load words.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
