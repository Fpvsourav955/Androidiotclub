package com.sourav.aiotclub1;

import java.util.Map;

public class AnswerModel {
    private String answerId;
    private String answerText;
    private String profileImageUrl;
    private long timestamp;
    private int likeCount;
    private Map<String, Boolean> likes;
    private int unlikeCount;
    private String userName;
    public AnswerModel() {
    }

    public AnswerModel(String answerText, String userName, String profileImageUrl, long timestamp) {
        this.answerText = answerText;
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.timestamp = timestamp;
        this.likeCount = 0;
        this.unlikeCount = 0;
    }

    public String getAnswerText() {
        return answerText;
    }
    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public String getAnswerId() {
        return answerId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setUnlikeCount(int unlikeCount) {
        this.unlikeCount = unlikeCount;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getUnlikeCount() {
        return unlikeCount;
    }

    public String getUserName() {
        return userName;
    }
    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
}
