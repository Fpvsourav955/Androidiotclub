package com.sourav.aiotclub1;

import java.util.List;
import java.util.Map;

public class Post {
    private String mediaUrl;
    private String description;
    private String tags;
    private String userId;
    private String postId;
    private String tempComment = "";
    private boolean showCommentLayout = false;
    private String profileName;
    private int likeCount;
    private String profileImage;
    private boolean isAdmin;

    private Map<String, Comment> comments;

    private String postLink;

    private long timestamp;

    public Post() {
    }

    public Post(String postId, String userId, String profileName, String profileImage, String mediaUrl, String description, String tags, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.profileName = profileName;
        this.profileImage = profileImage;
        this.mediaUrl = mediaUrl;
        this.description = description;
        this.tags = tags;
        this.timestamp = timestamp;
    }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public String getPostLink() {
        return postLink;
    }
    public boolean isShowCommentLayout() {
        return showCommentLayout;
    }

    public void setShowCommentLayout(boolean showCommentLayout) {
        this.showCommentLayout = showCommentLayout;
    }
    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTempComment() {
        return tempComment;
    }

    public void setTempComment(String tempComment) {
        this.tempComment = tempComment;
    }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
