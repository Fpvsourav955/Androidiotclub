package com.sourav.aiotclub1;

import java.util.List;
import java.util.Map;

public class QuestionModel {
    private String question;
    private String username;
    private String profileImageUrl;
    private String id;
    private long timestamp;
    private List<AnswerModel> answers;
    private Map<String, Boolean> likes; // added

    public QuestionModel() {

    }

    public QuestionModel(String question, String username, String profileImageUrl, long timestamp) {
        this.question = question;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.timestamp = timestamp;
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }

    public String getQuestion() { return question; }
    public String getUsername() { return username; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public long getTimestamp() { return timestamp; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Map<String, Boolean> getLikes() { return likes; }
    public void setLikes(Map<String, Boolean> likes) { this.likes = likes; }
}
