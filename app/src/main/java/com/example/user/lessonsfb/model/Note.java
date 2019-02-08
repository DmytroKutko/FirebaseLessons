package com.example.user.lessonsfb.model;

import com.google.firebase.firestore.Exclude;

public class Note {

    private String documentId;
    private String title;
    private String description;
    private long unixTime;

    public Note() {
    }

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
        this.unixTime = System.currentTimeMillis();
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    @Override
    public String toString() {
        return "Document ID: " + documentId + '\n' +
                "Title: " + title + '\n' +
                "Description: " + description + '\n' +
                "UnixTime: " + unixTime + "\n";
    }
}
