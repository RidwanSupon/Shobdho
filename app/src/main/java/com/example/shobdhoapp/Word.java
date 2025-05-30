package com.example.shobdhoapp;

public class Word {
    private String key;
    private String word;
    private String meaning;
    private String synonym;

    public Word() {}

    public Word(String word, String meaning, String synonym) {
        this.word = word;
        this.meaning = meaning;
        this.synonym = synonym;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public String getMeaning() {
        return meaning;
    }
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    public String getSynonym() {
        return synonym;
    }
    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }
}
