package com.example.shobdhoapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity implements WordAdapter.OnWordActionListener {

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private EditText searchBar;
    private WordAdapter adapter;
    private final List<Word> wordList = new ArrayList<>();
    private DatabaseReference wordRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        fabAdd = findViewById(R.id.addWordBtn);
        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.searchBar);

        wordRef = FirebaseDatabase.getInstance().getReference("words");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter(wordList, true, this);
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddOrEditWordDialog(null));

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadWordsFromFirebase();
    }

    private void loadWordsFromFirebase() {
        wordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Word> updatedWords = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Word word = ds.getValue(Word.class);
                    if (word != null) {
                        word.setKey(ds.getKey());
                        updatedWords.add(word);
                    }
                }
                adapter.updateFullList(updatedWords);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminPanelActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddOrEditWordDialog(@Nullable Word existingWord) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_word, null);
        EditText etWord = view.findViewById(R.id.etWord);
        EditText etMeaning = view.findViewById(R.id.etMeaning);
        EditText etSynonym = view.findViewById(R.id.etSynonym);
        Button saveBtn = view.findViewById(R.id.saveBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        if (existingWord != null) {
            etWord.setText(existingWord.getWord());
            etMeaning.setText(existingWord.getMeaning());
            etSynonym.setText(existingWord.getSynonym());
        }

        saveBtn.setOnClickListener(v -> {
            String wordStr = etWord.getText().toString().trim();
            String meaningStr = etMeaning.getText().toString().trim();
            String synonymStr = etSynonym.getText().toString().trim();

            if (wordStr.isEmpty() || meaningStr.isEmpty()) {
                Toast.makeText(this, "Word and Meaning are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Word newWord = new Word(wordStr, meaningStr, synonymStr);

            if (existingWord == null || existingWord.getKey() == null) {
                wordRef.push().setValue(newWord)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Word added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to add word: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                wordRef.child(existingWord.getKey()).setValue(newWord)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Word updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update word: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onEditWord(Word word) {
        new AlertDialog.Builder(this)
                .setTitle("Edit Word")
                .setMessage("Do you want to edit this word?")
                .setPositiveButton("Yes", (dialog, which) -> showAddOrEditWordDialog(word))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDeleteWord(Word word) {
        if (word.getKey() == null) {
            Toast.makeText(this, "Invalid word key", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Word")
                .setMessage("Are you sure you want to delete this word?")
                .setPositiveButton("Yes", (dialog, which) -> wordRef.child(word.getKey()).removeValue()
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Word deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to delete word: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
