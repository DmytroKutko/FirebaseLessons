package com.example.user.lessonsfb.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Map;

public class Note {

    private String documentId;
    private String title;
    private String description;
    private long unixTime;
    private int priority;
    Map<String, Boolean> tags;

    public Note() {
    }

    public Note(String title, String description, int priority, Map<String, Boolean> tags) {
        this.title = title;
        this.description = description;
        this.unixTime = System.currentTimeMillis();
        this.priority = priority;
        this.tags = tags;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Map<String, Boolean> getTags() {
        return tags;
    }

    public void setTags(Map<String, Boolean> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Document ID: " + documentId + '\n' +
                "Title: " + title + '\n' +
                "Description: " + description + '\n' +
                "UnixTime: " + unixTime + "\n" +
                "Priority: " + priority + "\n" +
                "Tags: " + tags.toString() + "\n";
    }
}
