package com.example.shobdhoapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shobdhoapp.databinding.DialogAddEditWordBinding;

public class AddEditWordDialog extends Dialog {

    private final Word wordToEdit;
    private final OnWordActionListener listener;
    private DialogAddEditWordBinding binding;

    public interface OnWordActionListener {
        void onWordAction(String word, String meaning, String synonym);
    }

    public AddEditWordDialog(@NonNull Context context, Word wordToEdit, OnWordActionListener listener) {
        super(context);
        this.wordToEdit = wordToEdit;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogAddEditWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (wordToEdit != null) {
            binding.etWord.setText(wordToEdit.getWord());
            binding.etMeaning.setText(wordToEdit.getMeaning());
            binding.etSynonym.setText(wordToEdit.getSynonym());
            binding.dialogTitle.setText("Edit Word");
            binding.saveBtn.setText("Update");
        } else {
            binding.dialogTitle.setText("Add Word");
            binding.saveBtn.setText("Add");
        }

        binding.saveBtn.setOnClickListener(v -> {
            String word = binding.etWord.getText().toString().trim();
            String meaning = binding.etMeaning.getText().toString().trim();
            String synonym = binding.etSynonym.getText().toString().trim();

            if (word.isEmpty()) {
                Toast.makeText(getContext(), "Word cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onWordAction(word, meaning, synonym);
            dismiss();
        });

        binding.cancelBtn.setOnClickListener(v -> dismiss());
    }
}
