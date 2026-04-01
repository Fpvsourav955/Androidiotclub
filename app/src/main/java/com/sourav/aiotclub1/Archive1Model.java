package com.sourav.aiotclub1;

public class Archive1Model {
    private String imageUrl;
    private String description;

    public Archive1Model(String imageUrl, String description) {
        this.imageUrl = imageUrl;
        this.description = description;
    }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
}
