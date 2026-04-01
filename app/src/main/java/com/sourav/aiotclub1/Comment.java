package com.sourav.aiotclub1;

import java.io.Serializable;

public class Comment implements Serializable {
    private String userId;
    private String commentText;
    private long timestamp;

    // No-arg constructor required for Firebase
    public Comment() {}

    public Comment(String userId, String commentText, long timestamp) {
        this.userId = userId;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "userId='" + userId + '\'' +
                ", commentText='" + commentText + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
