package com.sourav.aiotclub1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class News {
    private String newsKey;
    private String title;
    private String description;
    private String imageUrl;
    private long timestamp;
    private String imagePath; // e.g., "newsImages/image123.jpg"

    private String memberName;
    private String memberProfileImageUrl;
    private String memberUid;


    public News() {

    }


    public News(String newsKey, String title, String description, String imageUrl, long timestamp,
                String memberName, String memberProfileImageUrl, String memberUid) {
        this.newsKey = newsKey;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.memberName = memberName;
        this.memberProfileImageUrl = memberProfileImageUrl;
        this.memberUid = memberUid;
    }




    public String getNewsKey() {
        return newsKey;
    }

    public void setNewsKey(String newsKey) {
        this.newsKey = newsKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberProfileImageUrl() {
        return memberProfileImageUrl;
    }

    public void setMemberProfileImageUrl(String memberProfileImageUrl) {
        this.memberProfileImageUrl = memberProfileImageUrl;
    }

    public String getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(String memberUid) {
        this.memberUid = memberUid;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
