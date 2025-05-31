package com.example.shobdhoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Word> wordList;     // Filtered/displayed list
    private final List<Word> fullList; // Complete list for filtering
    private final boolean isAdmin;
    private final OnWordActionListener listener;

    public interface OnWordActionListener {
        void onEditWord(Word word);
        void onDeleteWord(Word word);
    }

    public WordAdapter(List<Word> wordList, boolean isAdmin, OnWordActionListener listener) {
        this.wordList = new ArrayList<>(wordList);
        this.fullList = new ArrayList<>(wordList);
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.tvWord.setText(word.getWord());
        holder.tvMeaning.setText(word.getMeaning());
        holder.tvSynonym.setText(word.getSynonym());

        if (isAdmin) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> listener.onEditWord(word));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteWord(word));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    /**
     * Filters the word list based on the query.
     * @param query the search keyword
     */
    public void filter(String query) {
        wordList.clear();
        if (query == null || query.isEmpty()) {
            wordList.addAll(fullList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Word word : fullList) {
                if (word.getWord().toLowerCase().contains(lowerQuery) ||
                        word.getMeaning().toLowerCase().contains(lowerQuery) ||
                        word.getSynonym().toLowerCase().contains(lowerQuery)) {
                    wordList.add(word);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Update the full list of words from Firebase and refresh the adapter.
     * @param newWords the list of updated words
     */
    public void updateFullList(List<Word> newWords) {
        fullList.clear();
        fullList.addAll(newWords);

        // Reset the filtered list to show all words initially
        wordList.clear();
        wordList.addAll(newWords);

        notifyDataSetChanged();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvMeaning, tvSynonym;
        ImageButton btnEdit, btnDelete;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvSynonym = itemView.findViewById(R.id.tvSynonym);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
