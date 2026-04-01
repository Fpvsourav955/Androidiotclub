package com.sourav.aiotclub1;

public class ReplyModel {
    public String userId;
    public String userName;
    public String profileImageUrl;
    public String replyText;
    public long timestamp;

    public ReplyModel() {}

    public ReplyModel(String userId, String userName, String profileImageUrl, String replyText, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.replyText = replyText;
        this.timestamp = timestamp;
    }
}
