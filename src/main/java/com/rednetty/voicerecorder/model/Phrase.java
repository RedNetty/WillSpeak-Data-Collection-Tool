package com.rednetty.voicerecorder.model;

/**
 * Represents a phrase to be recorded by users.
 */
public class Phrase {
    private String id;
    private String text;
    private String category;

    public Phrase(String id, String text, String category) {
        this.id = id;
        this.text = text;
        this.category = category != null ? category : "general";
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return text;
    }
}